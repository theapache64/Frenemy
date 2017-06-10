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


    CREATE TABLE IF NOT EXISTS `preference` (
      `id`     INT(11)      NOT NULL AUTO_INCREMENT,
      `_key`   VARCHAR(100) NOT NULL,
      `_value` TEXT         NOT NULL,
      PRIMARY KEY (`id`),
      UNIQUE KEY `_key` (`_key`)
    );

    INSERT INTO `preference` (`_key`, `_value`) VALUES
      ('gmail_username', 'gpixofficial@gmail.com'),
      ('gmail_password', 'thepassword'),
      ('is_direct_contact', 1),
      ('admin_email', 'theapache64@gmail.com'),
      ('test_api_key', 'testApiKey');