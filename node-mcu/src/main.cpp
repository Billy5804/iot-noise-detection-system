#include <ESP8266WiFi.h>
#include <LiquidCrystal_I2C.h>
#include <NTPClient.h>
#include <WiFiUdp.h>

#include "WiFiManager.h"
#include "fetch.h"
#include "timeSync.h"
#include "webServer.h"

struct task {
  unsigned long rate;
  unsigned long previous;
};

// Define main task timeout to not delay loop
task mainTask = {.rate = 500, .previous = 0};

// Define lcd
LiquidCrystal_I2C lcd = LiquidCrystal_I2C(0x27, 16, 2);

// Define NTP Client to get time
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org");

const int sampleWindow = 50;  // Sample window width in mS (50 mS = 20Hz)
unsigned int sample;

#define SENSOR_PIN A0

const String macAddress = WiFi.macAddress();
uint64_t deviceId;

bool wasCaptivePortal = false;

// Function to log to both lcd and serial
void logger(const char *logMsg, const int lcdLine = 0) {
  lcd.setCursor(0, lcdLine);
  lcd.print(logMsg);
  Serial.print(logMsg);
}

// Get deviceId from the MAC address
void initDeviceId() {
  char deviceIdHex[12], *end;
  uint8_t index = 0;

  for (const char &c : macAddress) {
    if (c != ':') {
      deviceIdHex[index] = c;
      index++;
    }
  }

  deviceId = strtoull(deviceIdHex, &end, 16);
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
  initDeviceId();
  Serial.println(deviceId);
  GUI.begin();
  WiFiManager.begin("NodeMCU");
  timeSync.begin();
  Serial.println(timeSync.waitForSyncResult());
  // fetch.begin("http://192.168.0.35:8083");
  // delay(1000);
  lcd.clear();
  timeClient.begin();
}

void loop() {
  WiFiManager.loop();

  if (WiFiManager.isCaptivePortal()) {
    lcd.clear();
    wasCaptivePortal = true;
    lcd.setCursor(0, 0);
    lcd.print("ConfigureNetwork");
    return;
  } else if (wasCaptivePortal) {
    lcd.clear();
    wasCaptivePortal = false;
    lcd.setCursor(0, 0);
    lcd.print("WiFi Connected!");
    delay(1000);
    return;
  }

  // Serial.println(deviceId);

  // // software interrupts
  // WiFiManager.loop();
  // configManager.loop();

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

  fetch.POST("http://192.168.0.35:8083", String(db));

  Serial.write(fetch.readString().c_str());

  while (fetch.busy()) {
    if (fetch.available()) {
      Serial.write(fetch.read());
    }
  }

  fetch.clean();
}