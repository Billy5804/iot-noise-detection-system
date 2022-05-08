FROM node:16-slim AS frontend_build
ARG WEB_SERVICE_HOSTNAME
ENV BASE_URL="https://${WEB_SERVICE_HOSTNAME}"
COPY frontend/src /app/src
COPY frontend/public /app/public
COPY frontend/index.html /app
COPY frontend/package.json /app
COPY frontend/vite.config.js /app
WORKDIR /app
RUN yarn
RUN yarn build

FROM maven:3.8-openjdk-17 AS build
COPY backend/src /app/src
COPY backend/pom.xml /app
COPY --from=frontend_build /app/dist /app/src/main/resources/public
WORKDIR /app
RUN mvn clean package -Dmaven.test.skip

FROM gcr.io/distroless/java17:latest
ARG WEB_SERVICE_HOSTNAME
ARG CERT_KEY_STORE_PASSWORD
ENV WEB_SERVICE_HOSTNAME=$WEB_SERVICE_HOSTNAME
ENV CERT_KEY_STORE_PASSWORD=$CERT_KEY_STORE_PASSWORD
COPY --from=build /app/target/iot-noise-detection-backend-0.0.1-SNAPSHOT.jar /app/iot-noise-detection-backend-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/app/iot-noise-detection-backend-0.0.1-SNAPSHOT.jar"]



