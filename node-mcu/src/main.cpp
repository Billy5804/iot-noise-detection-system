#include <ESP8266WiFi.h>

#include <LiquidCrystal_I2C.h> // Library for LCD
LiquidCrystal_I2C lcd = LiquidCrystal_I2C(0x27, 16, 2);

const int sampleWindow = 50; // Sample window width in mS (50 mS = 20Hz)
unsigned int sample;

#define SENSOR_PIN A0

const char *ssid = "";
const char *pass = "";

WiFiClient client;

void logger(const char *logMsg, const int lcdLine = 0) {
  lcd.setCursor(0, lcdLine);
  lcd.print(logMsg);
  Serial.print(logMsg);
}

void initWiFi()
{
  WiFi.begin(ssid, pass);
  logger("Connecting to: ");
  logger(ssid, 1);

  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print('.');
    delay(1000);
  }
  lcd.clear();
  Serial.println("");
  logger("Connected, IP: ");
  logger(WiFi.localIP().toString().c_str(), 1);

  // The ESP8266 tries to reconnect automatically when the connection is lost
  WiFi.setAutoReconnect(true);
  WiFi.persistent(true);
}

void setup()
{
  pinMode(SENSOR_PIN, INPUT); // Set the signal pin as input
  Serial.begin(115200);
  lcd.init();
  lcd.backlight();
  initWiFi();
  Serial.print("\nRSSI: ");
  Serial.println(WiFi.RSSI());
  delay(1000);
  lcd.clear();
}

void loop()
{
  unsigned long startMillis = millis(); // Start of sample window
  float peakToPeak = 0;                 // peak-to-peak level

  unsigned int signalMax = 0;    // minimum value
  unsigned int signalMin = 1024; // maximum value

  // collect data for 50 mS
  while (millis() - startMillis < sampleWindow)
  {
    sample = analogRead(SENSOR_PIN); // get reading from microphone
    if (sample < 1024)               // toss out spurious readings
    {
      if (sample > signalMax)
      {
        signalMax = sample; // save just the max levels
      }
      else if (sample < signalMin)
      {
        signalMin = sample; // save just the min levels
      }
    }
  }

  peakToPeak = signalMax - signalMin;          // max - min = peak-peak amplitude
  int db = map(peakToPeak, 20, 900, 49.5, 90); // calibrate for deciBels

  lcd.setCursor(0, 0);
  lcd.print("Loudness: ");
  lcd.print(db);
  lcd.print("dB");

  if (db <= 60)
  {
    lcd.setCursor(0, 1);
    lcd.print("Level: Quite");
  }
  else if (db > 60 && db < 85)
  {
    lcd.setCursor(0, 1);
    lcd.print("Level: Moderate");
  }
  else if (db >= 85)
  {
    lcd.setCursor(0, 1);
    lcd.print("Level: High");
  }

  if (client.connect("192.168.0.35", 8083)) // Test echo server
  {
    client.print("POST / HTTP/1.1\r\n");
    client.print("Host: 192.168.0.35\r\n");
    client.print("Connection: close\r\n");
    client.print("\r\n");
  }
  client.stop();

  delay(1000);
  lcd.clear();
}