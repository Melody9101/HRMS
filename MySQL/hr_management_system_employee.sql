-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: hr_management_system
-- ------------------------------------------------------
-- Server version	8.0.39

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee` (
  `id` int NOT NULL AUTO_INCREMENT,
  `department` varchar(50) DEFAULT NULL,
  `name` varchar(200) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(60) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `gender` tinyint NOT NULL DEFAULT '0',
  `grade` int NOT NULL DEFAULT '0',
  `entry_date` date DEFAULT NULL,
  `resignation_date` date DEFAULT NULL,
  `resignation_reason` varchar(1000) DEFAULT NULL,
  `salaries` int DEFAULT '0',
  `position` varchar(45) DEFAULT NULL,
  `employed` tinyint NOT NULL DEFAULT '0',
  `remaining_previous_annual_leave` decimal(10,4) DEFAULT '0.0000',
  `remaining_current_annual_leave` decimal(10,4) DEFAULT '0.0000',
  `remaining_paid_sick_leave` decimal(10,4) DEFAULT '0.0000',
  `unpaid_leave_start_date` date DEFAULT NULL,
  `unpaid_leave_end_date` date DEFAULT NULL,
  `unpaid_leave_reason` varchar(200) DEFAULT NULL,
  `final_update_date` date DEFAULT NULL,
  `final_update_employee_id` int DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES (5,'BOSS','maru','maru@gmail.com','$2a$10$1bvktSqgwZynUrWHgvTFAedcoiR2nLA6LkOWLW2qbqWS3N3LpIS0K','0973009542',1,11,'2020-01-01',NULL,NULL,1000000,'BOSS',1,0.0000,0.0000,5.0000,NULL,NULL,NULL,'2025-08-06',1),(6,'HR','melody','melody@gmail.com','$2a$10$jsIc7/MSqiNQR.GoEXv9KOSjHf0CUFwBdMvG03r7IxVdJ3Zv54RUO','0973254621',0,10,'2020-01-01',NULL,NULL,100000,'Manager',1,0.0000,0.0000,5.0000,NULL,NULL,NULL,'2025-08-06',1),(7,'HR','Anna','Anna@gmail.com','$2a$10$gJwmNEbxyvZMuq8ck91EVeNyUBFYA8x9oo5Ue3kUP06KbPMJk5TBy','0975642012',0,5,'2023-03-21',NULL,NULL,40000,'Employee',1,0.0000,0.0000,5.0000,NULL,NULL,NULL,'2025-08-06',1),(8,'Acct','Alan','Alan@gmail.com','$2a$10$N/IItZhOffvle/u7BKOV0eO8lZo.IYDxCrLHHFRcvEap4y/uVCoHW','0975421635',1,6,'2022-02-20',NULL,NULL,38000,'Manager',1,0.0000,0.0000,5.0000,NULL,NULL,NULL,'2025-08-06',1),(9,'Acct','David','David@gmail.com','$2a$10$CoYngnW1vHlV7vC0kT7yf.T55MEps/dHJr2Asz.g1OwLdcq4HKk82','0922654258',1,3,'2024-06-12',NULL,NULL,31000,'Employee',1,0.0000,0.0000,5.0000,NULL,NULL,NULL,'2025-08-06',1),(10,'GA','Shu','Shu@gmail.com','$2a$10$rgRiIJqrcdXAR2ZsfWuAGOUC0He9py7.0Hba0sSWBA1QTlOOi7VWS','0956428595',1,8,'2021-04-20',NULL,NULL,37000,'Manager',1,0.0000,0.0000,5.0000,NULL,NULL,NULL,'2025-08-06',1),(11,'GA','Vera','Vera@gmail.com','$2a$10$vjEOsFNg18fgugtYMrwiueBrIyZoBKaJtcxIkBLHdmb02zp1InFoO','0954621586',0,2,'2025-08-17',NULL,NULL,30000,'Employee',1,0.0000,0.0000,5.0000,NULL,NULL,NULL,'2025-08-06',1);
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-06  0:55:19
