CREATE DATABASE  IF NOT EXISTS `user`
USE `user`;

--
-- Table structure for table `confirmation_token`
--
DROP TABLE IF EXISTS `confirmation_token`;
CREATE TABLE `confirmation_token` (
  `token_id` bigint(20) NOT NULL,
  `confirmation_token` varchar(255) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`token_id`),
  KEY `fk_user_id` (`user_id`)
);

--
-- Table structure for table `password_reset_token`
--
DROP TABLE IF EXISTS `password_reset_token`;
CREATE TABLE `password_reset_token` (
  `token_id` bigint(20) NOT NULL,
  `created_date` datetime DEFAULT NULL,
  `reset_token` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`token_id`),
  KEY `fk_user_id` (`user_id`)
);

--
-- Table structure for table `user`
--
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL,
  `created_on` date DEFAULT NULL,
  `delivery_address` varchar(255) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `is_activated` bit(1) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `last_updated_on` date DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_email_address` (`email`)
);

--
-- Table structure for table `user_favorite`
--
DROP TABLE IF EXISTS `user_favorite`;
CREATE TABLE `user_favorite` (
  `id` bigint(20) NOT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_id` (`user_id`)
);

--
-- Table structure for table `hibernate_sequence`
--
DROP TABLE IF EXISTS `hibernate_sequence`;
CREATE TABLE `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
);