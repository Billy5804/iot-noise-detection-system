# iot-noise-detection-system
BSc SE Project

.env file:
COMPOSE_PROJECT_NAME required example "iot-prod"
CERTS_PATH optional example windows:"C:/Certbot", linux:"/etc/letsencrypt"
CERT_KEY_STORE_PASSWORD optional example "securePassword"
WEB_SERVICE_HOSTNAME required example "iot.alexroyle.com"
CERT_RENEW_EMAIL required example "username@example.com"
FIREBASE_SERVICE_ACCOUNT
DEVICE_AUTH_KEY

docker-compose -f docker-compose.cert.yml up
docker-compose -f docker-compose.cert-convert.yml up
docker-compose up (-d)

docker-compose -f docker-compose.dev.yml -p <project_name> up -d

docker volume prune
docker system prune -a