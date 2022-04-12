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
  uint64_t id;
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

unsigned int sample;
DynamicJsonDocument deviceDoc(128 + (64 * totalSensors));
JsonArray deviceSensors;

const uint8_t sampleWindow = 50;  // Sample window width in mS (50 mS = 20Hz)

const String apName = "NodeMCU-" + String(ESP.getChipId(), HEX);

const String macAddress = WiFi.macAddress();
uint64_t deviceId;

bool wasCaptivePortal = false;

// Function to log to both lcd and serial
void logger(const char *logMsg, const int lcdLine = 0) {
  lcd.setCursor(0, lcdLine);
  lcd.print(logMsg);
  Serial.println(logMsg);
}

// Get deviceId from the MAC address and store in deviceObject
uint64_t getDeviceId() {
  char deviceIdHex[12], *end;
  uint8_t index = 0;

  for (const char &c : macAddress) {
    if (c != ':') {
      deviceIdHex[index] = c;
      index++;
    }
  }

  return strtoull(deviceIdHex, &end, HEX);
}

void initDevice() { device = {.id = getDeviceId(), .type = DeviceType::NOISE}; }

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
  GUI.begin();
  WiFiManager.begin(apName.c_str());
  timeSync.begin();
  timeClient.begin();
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
  Serial.println(ESP.getFreeHeap());
  Serial.println(getTime());

  unsigned long startMillis = millis();  // Start of sample window
  float peakToPeak = 0;                  // peak-to-peak level

  unsigned int signalMax = 0;     // minimum value
  unsigned int signalMin = 1024;  // maximum value

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

  peakToPeak = signalMax - signalMin;  // max - min = peak-peak amplitude
  int db = map(peakToPeak, 20, 900, 49.5, 90);  // calibrate for deciBels

  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Loudness: ");
  lcd.print(db);
  lcd.print("dB");

  if (db <= 60) {
    lcd.setCursor(0, 1);
    lcd.print("Level: Quite");
  } else if (db > 60 && db < 85) {
    lcd.setCursor(0, 1);
    lcd.print("Level: Moderate");
  } else if (db >= 85) {
    lcd.setCursor(0, 1);
    lcd.print("Level: High");
  }

  String exampleData =
      "{\"id\":123456789101112, \"lastBeatTime\": 1333330000, \"rssi\", -40, "
      "\"sensors\": [{\"id\": 0, \"unit\": 0, \"latestValue\": 49.3}]}";
  String hash = experimental::crypto::SHA256::hash(exampleData);
  // uint8_t resultArray[experimental::crypto::SHA256::NATURAL_LENGTH]{ 0 };

  // // Hash
  // // experimental::crypto::SHA256::hash(exampleData.c_str(),
  // exampleData.length(), resultArray);
  // // Serial.println(String(F("\nThis is the SHA256 hash of our example data,
  // in HEX format:\n")) + TypeCast::uint8ArrayToHexString(resultArray, sizeof
  // resultArray));
  Serial.println(
      "This is the SHA256 hash of our example data, in HEX format, using "
      "String output:\r\n" +
      hash);

  fetch.begin("http://192.168.0.35:8083");
  fetch.addHeader("Authorization", hash);
  fetch.POST(String(db));

  Serial.write(fetch.readString().c_str());

  while (fetch.busy()) {
    if (fetch.available()) {
      Serial.write(fetch.read());
    }
  }

  fetch.clean();
}