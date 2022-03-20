CREATE SCHEMA IF NOT EXISTS `iot`;

USE `iot`;

CREATE TABLE `site` (
  `id` binary(16) NOT NULL,
  `siteName` varchar(255) NOT NULL,
  `createdAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `site_chk_1` CHECK (IS_UUID(BIN_TO_UUID(`id`)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `site_invitation` (
  `token` binary(16) NOT NULL,
  `siteId` binary(16) NOT NULL,
  `requireAuth` tinyint(1) NOT NULL DEFAULT 1,
  `expiresAt` timestamp NULL,
  `createdAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`token`),
  CONSTRAINT `site_invitation_ibfk_1` FOREIGN KEY (`siteId`) REFERENCES `site` (`id`) ON DELETE CASCADE,
  CONSTRAINT `site_invitation_chk_1` CHECK (IS_UUID(BIN_TO_UUID(`token`))),
  CONSTRAINT `site_invitation_chk_2` CHECK ((`requireAuth` in (0,1)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `device` (
  `id` binary(16) NOT NULL,
  `rssi` tinyint NOT NULL,
  `lastBeatTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createdAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `device_chk_1` CHECK (IS_UUID(BIN_TO_UUID(`id`))),
  CONSTRAINT `device_chk_2` CHECK (`rssi` BETWEEN -100 AND 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `sensor` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` varchar(16) NOT NULL UNIQUE,
  `unit` varchar(4) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `device_sensor` (
  `deviceId` binary(16) NOT NULL,
  `sensorId` int NOT NULL,
  `latestValue` int NULL,
  `createdAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`deviceId`,`sensorId`),
  CONSTRAINT `device_sensor_ibfk_1` FOREIGN KEY (`deviceId`) REFERENCES `device` (`id`) ON DELETE CASCADE,
  CONSTRAINT `device_sensor_ibfk_2` FOREIGN KEY (`sensorId`) REFERENCES `sensor` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `device_sensor_history` (
  `deviceId` binary(16) NOT NULL,
  `sensorId` int NOT NULL,
  `value` int NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createdAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`deviceId`,`sensorId`,`timestamp`),
  CONSTRAINT `device_sensor_history_ibfk_1` FOREIGN KEY (`deviceId`,`sensorId`) REFERENCES `device_sensor` (`deviceId`,`sensorId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user` (
  `id` binary(21) NOT NULL,
  `createdAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `site_user` (
  `siteId` binary(16) NOT NULL,
  `userId` binary(21) NOT NULL,
  `role` varchar(6) NOT NULL,
  `authenticated` tinyint(1) NOT NULL DEFAULT 0,
  `createdAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`siteId`,`userId`),
  CONSTRAINT `site_user_ibfk_1` FOREIGN KEY (`siteId`) REFERENCES `site` (`id`) ON DELETE CASCADE,
  CONSTRAINT `site_user_ibfk_2` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `site_user_chk_1` CHECK ((`role` in (_utf8mb4'owner',_utf8mb4'editor',_utf8mb4'viewer'))),
  CONSTRAINT `site_user_chk_2` CHECK ((`authenticated` in (0,1)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `site_device` (
  `siteId` binary(16) NOT NULL,
  `deviceId` binary(16) NOT NULL,
  `displayName` varchar(32) NOT NULL,
  `createdAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`siteId`,`deviceId`),
  CONSTRAINT `site_device_ibfk_1` FOREIGN KEY (`siteId`) REFERENCES `site` (`id`) ON DELETE CASCADE,
  CONSTRAINT `site_device_ibfk_2` FOREIGN KEY (`deviceId`) REFERENCES `device` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `location` (
  `id` binary(16) NOT NULL,
  `siteId` binary(16) NOT NULL,
  `displayName` varchar(32) NOT NULL,
  `floorPlan` varchar(255) NULL,
  `createdAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`,`siteId`),
  CONSTRAINT `location_ibfk_1` FOREIGN KEY (`siteId`) REFERENCES `site` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `location_device` (
  `locationId` binary(16) NOT NULL,
  `siteId` binary(16) NOT NULL,
  `deviceId` binary(16) NOT NULL,
  `positionX` INT NOT NULL,
  `positionY` INT NOT NULL,
  `createdAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`locationId`,`deviceId`),
  CONSTRAINT `location_device_ibfk_1` FOREIGN KEY (`locationId`, `siteId`) REFERENCES `location` (`id`, `siteId`) ON DELETE CASCADE,
  CONSTRAINT `location_device_ibfk_2` FOREIGN KEY (`siteId`, `deviceId`) REFERENCES `site_device` (`siteId`, `deviceId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
