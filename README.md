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

### Node mcu v2 based iot noise detection devices
1. required hardware:
2. connect pins
3. in platform io open ./node-mcu as a project
4. connect device to machine
5. build and upload (should auto detect device if not check platform io docs)


docker-compose -f docker-compose.cert.yml up
docker-compose -f docker-compose.cert-convert.yml up
docker-compose up (-d)

docker-compose -f docker-compose.dev.yml -p <project_name> up -d

docker volume prune
docker system prune -a