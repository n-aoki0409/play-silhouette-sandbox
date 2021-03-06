use `silhouette_db`;

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `USER_ID` char(20) NOT NULL COMMENT 'USER_ID',
  `FIRST_NAME` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT 'FIRST_NAME',
  `LAST_NAME` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT 'LAST_NAME',
  `EMAIL` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT 'EMAIL',
  `HASHED_PASSWORD` varchar(256) COLLATE utf8mb4_bin NOT NULL COMMENT 'HASHED_PASSWORD',
  `CREATED_AT` timestamp COMMENT 'CREATED_AT',
  `UPDATED_AT` timestamp COMMENT 'UPDATED_AT',
  PRIMARY KEY (`USER_ID`),
  UNIQUE KEY `EMAIL` (`EMAIL`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='USERS';
