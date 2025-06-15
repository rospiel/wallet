SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS `system_parameter`;

CREATE TABLE `system_parameter` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `data` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `name` varchar(100) NOT NULL,
  `version` int NOT NULL,
  `created_by` varchar(100) NOT NULL,
  `created_at` date NOT NULL,
  `updated_by` varchar(100) NULL,
  `updated_at` date NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `type_wallet_movements`;

CREATE TABLE `type_wallet_movements` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL,
  `name` varchar(100) NOT NULL,
  `created_by` varchar(100) NOT NULL,
  `created_at` date NOT NULL,
  `updated_by` varchar(100) NULL,
  `updated_at` date NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `wallet`;

CREATE TABLE `wallet` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account` varchar(10) NOT NULL,
  `agency` varchar(5) NOT NULL,
  `balance` numeric(11, 2) NOT NULL,
  `cpf` varchar(20) NOT NULL,
  `version` int NOT NULL,
  `created_by` varchar(100) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_by` varchar(100) NULL,
  `updated_at` date NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `wallet_movements`;

CREATE TABLE `wallet_movements` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `target_wallet_id` bigint NOT NULL,
  `source_wallet_id` bigint NULL,
  `type_wallet_movements_id` bigint NOT NULL,
  `amount` numeric(11, 2) NOT NULL,
  `amount_after_operation` numeric(11, 2) NOT NULL,
  `cpf` varchar(20) NOT NULL,
  `created_by` varchar(100) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_by` varchar(100) NULL,
  `updated_at` date NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT FK_wallet_movements_wallet_1 FOREIGN KEY (`target_wallet_id`) REFERENCES `wallet`(`id`),
  CONSTRAINT FK_wallet_movements_wallet_2 FOREIGN KEY (`source_wallet_id`) REFERENCES `wallet`(`id`),
  CONSTRAINT FK_type_wallet_movements_wallet_3 FOREIGN KEY (`type_wallet_movements_id`) REFERENCES `type_wallet_movements`(`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE INDEX IDX_wallet_movements_1 ON `wallet_movements`(`created_by`);

SET FOREIGN_KEY_CHECKS=1;