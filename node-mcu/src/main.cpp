#include <ArduinoJson.h>
#include <Crypto.h>
#include <ESP8266WiFi.h>
#include <LiquidCrystal_I2C.h>
#include <NTPClient.h>
#include <WiFiUdp.h>

#include "WiFiManager.h"
#include "fetch.h"
#include "timeSync.h"
#include "webServer.h"

#define SENSOR_PIN A0

struct Task {
  unsigned long rate;
  unsigned long previous;
};

enum DeviceType : uint8_t { NOISE };
enum SensorUnit : uint8_t { DECIBEL, BEL };

struct Device {
  char id[12];
  DeviceType type;
  int8_t rssi;
  unsigned long lastBeatTime;
};

struct Sensor {
  uint8_t id;
  SensorUnit unit;
  float latestValue;
  bool hasChanged = false;
  void (*loop)(void);
};

// Define main task timeout to not delay loop
Task mainTask = {.rate = 1000, .previous = 0};

Device device;

const uint8_t totalSensors = 1;
Sensor sensors[totalSensors];
Sensor *noiseSensor = &sensors[0];

// Define lcd
LiquidCrystal_I2C lcd = LiquidCrystal_I2C(0x27, 16, 2);

// Define NTP Client to get time
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org");

DynamicJsonDocument deviceDoc(128 + (64 * totalSensors));
JsonArray deviceSensors;

const uint8_t sampleWindow = 50;  // Sample window width in mS (50 mS = 20Hz)

const String apName = "NodeMCU-" + String(ESP.getChipId(), HEX);

const String macAddress = WiFi.macAddress();

bool wasCaptivePortal = false;

// Function to log to both lcd and serial
void logger(const char *logMsg, const int lcdLine = 0) {
  lcd.setCursor(0, lcdLine);
  lcd.print(logMsg);
  Serial.println(logMsg);
}

// Set deviceId from the MAC address and store in deviceObject
void populateDeviceId(char deviceId[12]) {
  uint8_t index = 0;

  for (const char &c : macAddress) {
    if (c != ':') {
      deviceId[index] = c;
      index++;
    }
  }
}

void setChangedDeviceSensors(bool setAll = false) {
  for (uint8_t i = 0; i < totalSensors; i++) {
    if (!sensors[i].hasChanged && !setAll) {
      continue;
    }
    DynamicJsonDocument sensorDoc(64);
    sensorDoc["id"] = sensors[i].id;
    sensorDoc["unit"] = sensors[i].unit;
    sensorDoc["latestValue"] = sensors[i].latestValue;
    deviceSensors[i] = sensorDoc;
    sensors[i].hasChanged = false;
  }
}

bool sensorsLoop() {
  bool anyChanges = false;
  for (uint8_t i = 0; i < totalSensors; i++) {
    sensors[i].loop();
    anyChanges = sensors[i].hasChanged ? true : anyChanges;
  }
  return anyChanges;
}

float getCurrentNoiseLevel() {
  unsigned long startMillis = millis();  // Start of sample window
  float peakToPeak = 0;                  // peak-to-peak level

  uint16_t signalMax = 0;     // minimum value
  uint16_t signalMin = 1024;  // maximum value
  uint16_t sample;            // Sensor sample

  // collect data for 50 mS
  while (millis() - startMillis < sampleWindow) {
    sample = analogRead(SENSOR_PIN);  // get reading from microphone
    if (sample >= 1024) {
      continue;  // toss out spurious readings
    }
    if (sample > signalMax) {
      signalMax = sample;  // save just the max levels
    } else if (sample < signalMin) {
      signalMin = sample;  // save just the min levels
    }
  }

  peakToPeak = signalMax - signalMin;         // max - min = peak-peak amplitude
  return map(peakToPeak, 20, 900, 49.5, 90);  // calibrate for decibels
}

void displayNoiseLevel(float decibels) {
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Loudness: ");
  lcd.print(decibels);
  lcd.print("dB");

  if (decibels <= 60) {
    lcd.setCursor(0, 1);
    lcd.print("Level: Quite");
  } else if (decibels > 60 && decibels < 85) {
    lcd.setCursor(0, 1);
    lcd.print("Level: Moderate");
  } else if (decibels >= 85) {
    lcd.setCursor(0, 1);
    lcd.print("Level: High");
  }
}

void noiseSensorLoop() {
  float decibels = getCurrentNoiseLevel();
  (*noiseSensor).hasChanged = decibels != (*noiseSensor).latestValue;
  if ((*noiseSensor).hasChanged) {
    displayNoiseLevel(decibels);
    (*noiseSensor).latestValue = decibels;
  }
}

void initDevice() {
  device = {.type = DeviceType::NOISE};
  populateDeviceId(device.id);
}

void initSensors() {
  *noiseSensor = {.id = 0,
                  .unit = SensorUnit::DECIBEL,
                  .latestValue = 49,
                  .loop = noiseSensorLoop};
}

// Get deviceId from the MAC address and store in deviceObject
void initDeviceObject() {
  deviceDoc.clear();
  deviceDoc["id"] = device.id;
  deviceDoc["type"] = device.type;
  deviceDoc["rssi"] = device.rssi;
  deviceDoc["lastBeatTime"] = device.lastBeatTime;
  deviceSensors = deviceDoc.createNestedArray("sensors");
}

void initLCD() {
  lcd.init();
  lcd.backlight();
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Starting...");
}

// Function that gets current epoch time
unsigned long getTime() {
  timeClient.update();
  return timeClient.getEpochTime();
}

void setup() {
  pinMode(SENSOR_PIN, INPUT);  // Set the signal pin as input
  Serial.begin(115200);
  Serial.println("");
  initLCD();
  initDevice();
  initSensors();
  GUI.begin();
  WiFiManager.begin(apName.c_str());
  timeSync.begin();
  timeClient.begin();
}

void sendChangesToAPI(bool create = false) {
  String serializedDevice;
  serializeJson(deviceDoc, serializedDevice);
  String hash =
      experimental::crypto::SHA256::hash(serializedDevice + "SuperSecretKey");

  Serial.println(serializedDevice);

  fetch.begin("http://192.168.0.35:8083");
  fetch.addHeader("Authorization", hash);
  fetch.addHeader("Content-Type", "application/json");
  if (create) {
    fetch.POST(serializedDevice);
  } else {
    fetch.PUT(serializedDevice);
  }

  while (fetch.busy()) {
    if (fetch.available()) {
      Serial.write(fetch.read());
    }
  }

  fetch.clean();

  if (false) {
    initDeviceObject();
    setChangedDeviceSensors(true);
    sendChangesToAPI(true);
  }
}

void loop() {
  WiFiManager.loop();

  if (WiFiManager.isCaptivePortal()) {
    if (wasCaptivePortal) {
      return;
    }
    lcd.clear();
    wasCaptivePortal = true;
    logger("Access Point:");
    logger(apName.c_str(), 1);
    return;
  } else if (wasCaptivePortal) {
    lcd.clear();
    wasCaptivePortal = false;
    logger("WiFi Connected!");
    mainTask.previous = millis() - mainTask.rate;
    return;
  }

  if (!(mainTask.previous == 0 ||
        (millis() - mainTask.previous > mainTask.rate))) {
    return;
  }

  mainTask.previous = millis();

  // do task
  device.rssi = WiFi.RSSI();
  const unsigned long lastBeatTime = getTime();
  bool anyChanges = sensorsLoop();

  if (!anyChanges && (device.lastBeatTime + 900) > lastBeatTime) {
    return;
  }

  device.lastBeatTime = lastBeatTime;

  initDeviceObject();
  setChangedDeviceSensors();

  sendChangesToAPI();
}
