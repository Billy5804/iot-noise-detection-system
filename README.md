# IoT Noise Detection System
[BSc SE Project Product Repository](https://github.com/Billy5804/iot-noise-detection-system)

## Deployment
### Prerequisites
- [Docker](https://docs.docker.com/get-docker/)
- [Docker compose](https://docs.docker.com/compose/install/)
- [Platform IO](https://platformio.org/platformio-ide)
- [Firebase project](https://console.firebase.google.com/u/0/) 
  - Setup Authentication and enable Email / Password provider 
  - Add web app from overview page without hosting and replace the existing firebaseConfig at ./frontend/src/firebase/database.js with the one for your web app.
  - Setup storage in production mode and configure the rules to match:
    ```
    rules_version = '2';
    service firebase.storage {
      match /b/{bucket}/o {
        function userIsLoggedIn() {
          return request.auth != null;
        }

        function userIsLoggedInAndEmailVerified() {
          return userIsLoggedIn() &&
            request.auth.token.email_verified;
        }

        match /floorPlans/{locationId} {
          allow read, write: if userIsLoggedInAndEmailVerified();
        }
      }
    }
    ```
  - Under the "project settings" -> "service accounts" generate a new private key. 
  
### Database & Web Service
1. Create a copy of the file named "example.env" and name it ".env" in the repos root directory.
2. Configure the variables is the .env file:
    - COMPOSE_PROJECT_NAME - optional - is the name of the deployed containers project group.
    - CERTS_PATH - optional - path to you certbot / letsencrypt directory (e.g. windows:"C:/Certbot", linux:"/etc/letsencrypt") if you wish to have certificates saved on your host machine (note populating this variable may prevent conversion of the certificate to a format supported by the web service).
    - CERT_KEY_STORE_PASSWORD - optional - password to use when creating and using the converted certificate.
    - CERT_RENEW_EMAIL - required - email address for receiving security / renewal notices for you generated certificate.  
    - WEB_SERVICE_HOSTNAME - required - hostname of the web service
    - API_KEY - required - api key used by the node mcu iot noise detection devices to authenticate with the web service.
    - SERVICE_ACCOUNT_JSON_PATH - required - path to the firebase projects admin service account private key json file.
3. Create / renew web service certificate for https by running the command in the repository root `docker-compose -f ./docker-compose.cert.yml up certgenerator` (note you will need to have port 80 exposed publicly (docker should be able allow access through you firewall on its own but not through your router) for the http challenge to pass. More info [here](https://certbot.eff.org/instructions?ws=other&os=windows))
4. Make the generated / renewed certificate compatible with the web service by next running the command `docker-compose -f ./docker-compose.cert.yml up certconverter`
5. Cleanup the containers created for managing the certificates by running the command `docker-compose -f ./docker-compose.cert.yml down`
6. To start the web service and its database make sure the port 443 is exposed publicly (docker should be able allow access through you firewall on its own but not through your router) in the repository root run the command `docker-compose up` with the optional `-d` flag at the end of the command to start the web service and database detached from the terminal (you can also use the --build flag to rebuild the database and web service images if you change any files are .env variables).
7. To stop the web service and database in the root of the repository you can run the command `docker-compose down`

### Node mcu v2 based iot noise level detection devices
1. To begin you will need the hardware used to make the devices:
   - [NodeMCU V2 WiFi Amica ESP8266](https://www.amazon.co.uk/gp/product/B0754LZ73Z)
   - [High Sensitivity Sound Microphone Sensor Detection Module](https://www.amazon.co.uk/gp/product/B07Q1BYDS7)
   - [Jumper Wires](https://www.amazon.co.uk/Elegoo-120pcs-Multicolored-Breadboard-arduino-colorful/dp/B01EV70C78)
   - Optional [16x2 LCD Display Module](https://www.amazon.co.uk/gp/product/B09B37WVFX)
2. connect pins using diagram:
   1. TODO
3. Using platform io open ./node-mcu as a project.
4. Configure the build_flags is the ./node-mcu/platformio.ini file to match the same variable values in the .env file (enter values between \\" e.g. \\"webservice.com\\"):
   - WEB_SERVICE_HOSTNAME - required - hostname of the web service.
   - API_KEY - required - api key used by the device to authenticate with the web service.
5. Connect your device to your machine using a USB-A (pc end) to micro USB B (node mcu end) cable.
6. In your platform IO environment for the project press the upload button(In vscode this appears along the bottom bar in the form or an arrow pointing right). Pressing the button will cause platform IO to build then upload the code to your device (platform IO should auto detect device if not check platform io docs).
7. When the device boots up for the first time it will open a portal where you can connect and configure the WiFi network it should use. To do this you will need to:
   1. Connect a device to its network by connecting to the network with an SSID that starts with "NodeMCU-" (the last few characters are to make the device unique so will vary from device to device).
   2. Once connected in a browser go to http://192.168.4.1.
   3. On the page that loads you will see a form where you should enter the details to connect to your network (Only 2.4Ghz WiFi is supported).
   4. When you have entered all of your network details correctly press the save button.
   5. If your network details were correct you will lose connection from the device as it cannot host the portal and connect to a network at the same time.
   6. If you wish to change the network the device uses in the future you can do so by putting the device out of range of the network it is currently using and after about a minute the device will open the portal again.
8. Using a program such as [PuTTY](https://www.putty.org/) connect over serial to the device using a baud rate of 115200 and the devices Serial line (You can find by using platform IOs device explorer an example name would be COM4 or on windows using Device Manager).
9. If the device is connected to a network you should see some JSON with an "id" key take note of its value as that is the identifier used in the web service to add your device to a site (The id should be a MAC address without the colons).