DROP DATABASE IF EXISTS `frenemy`;
CREATE DATABASE IF NOT EXISTS `frenemy`;
USE `frenemy`;

CREATE TABLE `frenemies` (
  `id`          INT          NOT NULL AUTO_INCREMENT,
  `name`        VARCHAR(100) NULL,
  `email`       VARCHAR(200) NULL,
  `imei`        VARCHAR(16)  NOT NULL,
  `device_hash` TEXT         NOT NULL,
  `fcm_id`      TEXT         NULL,
  `api_key`     VARCHAR(20)  NOT NULL,
  `is_active`   TINYINT(4)   NOT NULL DEFAULT '1',
  `created_at`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`imei`),
  UNIQUE KEY (`api_key`)
);

INSERT INTO frenemies (name, email, imei, device_hash, fcm_id, api_key)
VALUES ('Test user', 'test@user.com', '1234567890123456', 'theDeviceHash', 'theFcmId', 'theApiKey');