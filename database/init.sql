CREATE SCHEMA IF NOT EXISTS `iot`;

USE `iot`;

CREATE TABLE `site` (
  `id` binary(16) NOT NULL,
  `display_name` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `site_chk_1` CHECK (IS_UUID(BIN_TO_UUID(`id`)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `site_invitation` (
  `token` binary(16) NOT NULL,
  `site_id` binary(16) NOT NULL,
  `require_auth` tinyint(1) NOT NULL DEFAULT 1,
  `expires_at` timestamp NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`token`),
  CONSTRAINT `site_invitation_ibfk_1` FOREIGN KEY (`site_id`) REFERENCES `site` (`id`) ON DELETE CASCADE,
  CONSTRAINT `site_invitation_chk_1` CHECK (IS_UUID(BIN_TO_UUID(`token`))),
  CONSTRAINT `site_invitation_chk_2` CHECK ((`require_auth` in (0,1)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `device` (
  `id` binary(16) NOT NULL,
  `rssi` tinyint NOT NULL,
  `last_beat_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
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
  `device_id` binary(16) NOT NULL,
  `sensor_id` int NOT NULL,
  `latest_value` int NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`device_id`,`sensor_id`),
  CONSTRAINT `device_sensor_ibfk_1` FOREIGN KEY (`device_id`) REFERENCES `device` (`id`) ON DELETE CASCADE,
  CONSTRAINT `device_sensor_ibfk_2` FOREIGN KEY (`sensor_id`) REFERENCES `sensor` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `device_sensor_history` (
  `device_id` binary(16) NOT NULL,
  `sensor_id` int NOT NULL,
  `value` int NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`device_id`,`sensor_id`,`timestamp`),
  CONSTRAINT `device_sensor_history_ibfk_1` FOREIGN KEY (`device_id`,`sensor_id`) REFERENCES `device_sensor` (`device_id`,`sensor_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `site_user` (
  `site_id` binary(16) NOT NULL,
  `user_id` binary(21) NOT NULL,
  `role` tinyint(1) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`site_id`,`user_id`),
  CONSTRAINT `site_user_ibfk_1` FOREIGN KEY (`site_id`) REFERENCES `site` (`id`) ON DELETE CASCADE,
  CONSTRAINT `site_user_chk_1` CHECK ((`role` BETWEEN 0 AND 3))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `site_device` (
  `site_id` binary(16) NOT NULL,
  `device_id` binary(16) NOT NULL,
  `display_name` varchar(32) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`site_id`,`device_id`),
  CONSTRAINT `site_device_ibfk_1` FOREIGN KEY (`site_id`) REFERENCES `site` (`id`) ON DELETE CASCADE,
  CONSTRAINT `site_device_ibfk_2` FOREIGN KEY (`device_id`) REFERENCES `device` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `location` (
  `id` binary(16) NOT NULL,
  `site_id` binary(16) NOT NULL,
  `display_name` varchar(32) NOT NULL,
  `floor_plan` varchar(255) NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`,`site_id`),
  CONSTRAINT `location_ibfk_1` FOREIGN KEY (`site_id`) REFERENCES `site` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `location_device` (
  `locationId` binary(16) NOT NULL,
  `site_id` binary(16) NOT NULL,
  `device_id` binary(16) NOT NULL,
  `position_x` INT NOT NULL,
  `position_y` INT NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`locationId`,`device_id`),
  CONSTRAINT `location_device_ibfk_1` FOREIGN KEY (`locationId`, `site_id`) REFERENCES `location` (`id`, `site_id`) ON DELETE CASCADE,
  CONSTRAINT `location_device_ibfk_2` FOREIGN KEY (`site_id`, `device_id`) REFERENCES `site_device` (`site_id`, `device_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
