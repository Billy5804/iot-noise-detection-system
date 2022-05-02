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

CREATE TABLE `site_invitation` (
  `id` binary(16) NOT NULL,
  `site_id` binary(16) NOT NULL,
  `display_name` varchar(32) NOT NULL,
  `available_uses` int NULL,
  `expires_at` timestamp NOT NULL,-- DEFAULT TIMESTAMPADD(WEEK, 1, CURRENT_TIMESTAMP),
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `site_invitation_ibfk_1` FOREIGN KEY (`site_id`) REFERENCES `site` (`id`) ON DELETE CASCADE,
  CONSTRAINT `site_invitation_chk_1` CHECK (IS_UUID(BIN_TO_UUID(`id`))),
  CONSTRAINT `site_invitation_chk_2` CHECK (TIMESTAMPDIFF(YEAR,`created_at`,`expires_at`) < 1)
  -- CONSTRAINT `site_invitation_chk_3` CHECK (`expires_at` >= CURRENT_TIMESTAMP)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `device` (
  `id` binary(6) NOT NULL,
  `type` tinyint NOT NULL,
  `rssi` tinyint NOT NULL,
  `last_beat_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `device_chk_1` CHECK (`rssi` BETWEEN -100 AND 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `device_sensor` (
  `id` tinyint unsigned NOT NULL,
  `device_id` binary(6) NOT NULL,
  `unit` tinyint NOT NULL,
  `latest_value` float(5,2) NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`,`device_id`),
  CONSTRAINT `device_sensor_ibfk_1` FOREIGN KEY (`device_id`) REFERENCES `device` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `site_device` (
  `site_id` binary(16) NOT NULL,
  `device_id` binary(6) NOT NULL UNIQUE,
  `display_name` varchar(32) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`site_id`,`device_id`),
  CONSTRAINT `site_device_ibfk_1` FOREIGN KEY (`site_id`) REFERENCES `site` (`id`) ON DELETE CASCADE,
  CONSTRAINT `site_device_ibfk_2` FOREIGN KEY (`device_id`) REFERENCES `device` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `site_device_sensor_history` (
  `device_id` binary(6) NOT NULL,
  `sensor_id` tinyint unsigned NOT NULL,
  `site_id` binary(16) NOT NULL,
  `value` float(5,2) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`device_id`,`sensor_id`,`timestamp`),
  CONSTRAINT `device_sensor_history_ibfk_1` FOREIGN KEY (`device_id`,`sensor_id`) REFERENCES `device_sensor` (`device_id`,`id`) ON DELETE CASCADE,
  CONSTRAINT `device_sensor_history_ibfk_2` FOREIGN KEY (`device_id`,`site_id`) REFERENCES `site_device` (`device_id`,`site_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `location` (
  `id` binary(16) NOT NULL,
  `site_id` binary(16) NOT NULL,
  `display_name` varchar(32) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`,`site_id`),
  CONSTRAINT `location_ibfk_1` FOREIGN KEY (`site_id`) REFERENCES `site` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `location_device` (
  `location_id` binary(16) NOT NULL,
  `device_id` binary(6) NOT NULL,
  `site_id` binary(16) NOT NULL,
  `position_x` INT NULL,
  `position_y` INT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`location_id`,`device_id`),
  CONSTRAINT `location_device_ibfk_1` FOREIGN KEY (`location_id`, `site_id`) REFERENCES `location` (`id`, `site_id`) ON DELETE CASCADE,
  CONSTRAINT `location_device_ibfk_2` FOREIGN KEY (`site_id`, `device_id`) REFERENCES `site_device` (`site_id`, `device_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO iot.site (id, display_name) VALUES (UUID_TO_BIN("f6f329b6-a9d9-11ec-b2f6-0242ac130002"), "Test Site");
INSERT INTO iot.user (id) VALUES (FROM_BASE64("D3SLtLY9sIcoj5ZA2MQCA1U17HA2"));
INSERT INTO iot.site_user (site_id, user_id, role)
	VALUES (UUID_TO_BIN("f6f329b6-a9d9-11ec-b2f6-0242ac130002"), FROM_BASE64("D3SLtLY9sIcoj5ZA2MQCA1U17HA2"), 1);
