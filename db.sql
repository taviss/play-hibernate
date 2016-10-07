-- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: db
-- ------------------------------------------------------
-- Server version	5.7.13-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `accounts`
--

DROP TABLE IF EXISTS `accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `accounts` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `u_name` varchar(64) DEFAULT NULL,
  `u_pass` varchar(256) DEFAULT NULL,
  `u_admin` int(11) DEFAULT NULL,
  `u_mail` varchar(45) DEFAULT NULL,
  `u_token` varchar(45) DEFAULT NULL,
  `u_active` tinyint(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accounts`
--

LOCK TABLES `accounts` WRITE;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` VALUES (1,'AdminTest','1000>de8250ed7b66ac6cbd17ea9b79e3e5abb3597d8f0ce5a999>848ee98f8d39b225f325037dab99d83d6cd4ccae31bd38b3',3,'admin@admin.com','3557722b-af28-47bb-8082-58c11b450fef',1),(3,'Tavi2','1000>e2cd6227e96836c8e342c2b691db575ee02c857410386717>bc34e02976963abe6535f2ae0b2d8b0dd5846ceea2ca803f',0,'octavian.salcian2u@gmail.com','46464216-d484-48f2-bc56-e3186b3845d6',1),(4,'AbcTesting','1000>89207785a303de25af752965fd2ff7c500251f719483faaf>4898fe3532ee854065af40d3e626b2b00f9354d01fdfc213',0,'octavian.salcianu2@gmail.com','37f738af-a796-4307-b699-a7a82d555cc7',0),(5,'TestAcc','1000>0f29c39f1fa7213f2895b05f3b9da9cb1b0826fcbda4d01e>ed2285cd232b3a89a36f80085e758c0bac98e50a322afc4b',0,'octavian.salcianu3@gmail.com','727f3fe9-1ae6-4b5e-96de-8b3a92e3e4ef',0),(6,'Abc','1000>ea591ec3cc376b2941dd93712734a9dca38a736eba3175a0>a5d2c59e923cd4a60375b5dbbfb3f3ab10052006552d1387',0,'octavian.salcianu5@gmail.com','7d70d760-b220-4650-8b02-1a567733900f',0),(7,'TaviAdmin','1000>87e6b236e3c567b349afabec9ce833b04d3739f800482456>b386932c4ea9991a91bb6b8ea50ef2da70f86ef9651e0ddf',3,'octavian.salcianu@gmail.com','47b527ee-92bf-4f35-b89d-b81c76f01f20',1),(8,'Tavi3','1000>362dd7bd580acc964d3388077843e6a41b0d53d93faf0773>8b3f3223650d5598516ee126fc23d9a2658eb26de0ad2bc1',0,'test@test.com','9309b369-dc80-46f2-aacb-9ed0fd8449fb',0),(11,'Test','1000>362dd7bd580acc964d3388077843e6a41b0d53d93faf0773>8b3f3223650d5598516ee126fc23d9a2658eb26de0ad2bc1',0,'octavian.salciasdsnu@gmail.com',NULL,1),(12,'TestAccount','1000>3d53ba3d5ff5880f1a57e4ae3f298b4351446a106f70c8e2>17d3f3f021816a0f9c91c42fb7b0943ff22044c1390ca125',0,'random@mail.com','d63e4d77-44a0-4446-b916-6c734fdc2f12',0),(13,'Testing_Account2','1000>6107e4f203b0ae1ddd491efb88e8eeab5f0a365d886f8822>a3aa44125ff256c48fc04a39152a4034eb11e888045caf93',0,'admin@testtest.com','0e6b7a91-edf9-4eb9-8eab-2983580fbf0c',1),(14,'JustATestAcc','1000>766cc7cfc685917a995ed114bed00d79922cc7c490f99e01>2d3630a4d998e9f59b301ae4d74ecb4702f5b1c76d0ff1c9',0,'testmail@abc.com','2b79f1cf-ecd9-4046-8fba-4f55acf9d545',1),(15,'SampleAccount','1000>8d99ebb2ad5c51f8031924a158bf9e6d899b34ceb5deb77d>6d587d37d3a1a7d1b8bec77ed78386d4bba6209c9c07808a',0,'samplemail@abc.com','009fc55f-b053-404a-bc01-cd5d85945d03',1),(16,'Testing_Account','1000>6265061daa705481bcec409bc5960c45eeaf988c691d3866>04b767f4954c6832230b977584f1c148bb916e5c0f7f81f9',0,'admin@testtest.com','2f8a989b-214f-4c5d-bdfe-3c4a0171d368',1),(17,'TaviAdmin2','1000>fee19e9ffef2ec544c352708fcc4d99f2b741209fce384c3>3bfde772fcb99bf605ef8a41be58150050c004659f3a9ba8',0,'teeest@testare.to','722674e3-2dbf-422d-9b2b-12b22220e566',0);
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categories` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `deleted` tinyint(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (2,'Laptops',NULL),(3,'Test',NULL);
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `keywords`
--

DROP TABLE IF EXISTS `keywords`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `keywords` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) DEFAULT NULL,
  `keyword` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `product_id_idx` (`product_id`),
  CONSTRAINT `FK3slthy3ktb5l6e48sil4ngggi` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `product_id_k` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=110 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `keywords`
--

LOCK TABLES `keywords` WRITE;
/*!40000 ALTER TABLE `keywords` DISABLE KEYS */;
INSERT INTO `keywords` VALUES (1,1,'livrare'),(2,1,'finantare'),(3,1,'core'),(4,1,'p2520la-xo0764t'),(5,1,'intel®'),(6,1,'laptop'),(7,1,'dvd-rw'),(8,1,'asus'),(9,1,'procesor'),(10,1,'emag'),(11,1,'i7-5500u'),(12,1,'windows'),(13,1,'pentru'),(14,1,'24ghz'),(15,1,'156'),(16,1,'4gb'),(17,1,'graphics'),(18,1,'black'),(19,1,'rate'),(20,1,'microsoft'),(21,1,'240ghz'),(22,1,'500gb'),(23,1,'10'),(24,1,'gratuita'),(25,1,'broadwell'),(26,2,'livrare'),(27,2,'finantare'),(28,2,'core'),(29,2,'p2520la-xo0764t'),(30,2,'intel®'),(31,2,'laptop'),(32,2,'dvd-rw'),(33,2,'asus'),(34,2,'procesor'),(35,2,'emag'),(36,2,'i7-5500u'),(37,2,'windows'),(38,2,'pentru'),(39,2,'24ghz'),(40,2,'156'),(41,2,'4gb'),(42,2,'graphics'),(43,2,'black'),(44,2,'rate'),(45,2,'microsoft'),(46,2,'240ghz'),(47,2,'500gb'),(48,2,'10'),(49,2,'gratuita'),(50,2,'broadwell'),(51,3,'i7-5500u'),(52,3,'windows'),(53,3,'pentru'),(54,3,'24ghz'),(55,3,'156'),(56,3,'finantare'),(57,3,'4gb'),(58,3,'core'),(59,3,'p2520la-xo0764t'),(60,3,'graphics'),(61,3,'intel®'),(62,3,'black'),(63,3,'rate'),(64,3,'laptop'),(65,3,'microsoft'),(66,3,'240ghz'),(67,3,'500gb'),(68,3,'dvd-rw'),(69,3,'10'),(70,3,'gratuita'),(71,3,'asus'),(72,3,'procesor'),(73,3,'broadwell'),(74,4,'15v'),(75,4,'fury'),(76,4,'memorie'),(77,4,'blue'),(78,4,'cl10'),(79,4,'1600mhz'),(80,4,'hyperx'),(81,4,'4gb'),(82,4,'ddr3'),(83,5,'IdeaPad'),(84,5,'1TB-5400rpm'),(85,5,'DVDRW'),(86,5,'4GB'),(87,5,'HD'),(88,5,'Laptop'),(89,5,'Lenovo'),(90,5,'100-15'),(91,5,'i5-5200U'),(92,6,'Accessories'),(93,6,'Smartphone'),(94,6,'Mobile'),(95,6,'Apple'),(96,6,'4G'),(97,6,'Touch'),(98,6,'Phones'),(99,6,'16GB'),(100,6,'ID'),(101,6,'5S'),(102,6,'LTE'),(103,6,'A1533'),(104,6,'Cell'),(105,6,'Factory'),(106,6,'&'),(107,6,'iPhone'),(108,6,'Smartphones'),(109,6,'Unlocked');
/*!40000 ALTER TABLE `keywords` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `play_evolutions`
--

DROP TABLE IF EXISTS `play_evolutions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `play_evolutions` (
  `id` int(11) NOT NULL,
  `hash` varchar(255) NOT NULL,
  `applied_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `apply_script` mediumtext,
  `revert_script` mediumtext,
  `state` varchar(255) DEFAULT NULL,
  `last_problem` mediumtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `play_evolutions`
--

LOCK TABLES `play_evolutions` WRITE;
/*!40000 ALTER TABLE `play_evolutions` DISABLE KEYS */;
INSERT INTO `play_evolutions` VALUES (1,'2591b0ed3698e462ad7ca4a8bbb9964f1282d329','2016-08-09 11:57:37','ALTER TABLE `db`.`products`\nCHANGE COLUMN `link_address` `link_address` VARCHAR(512) NULL DEFAULT NULL ;\n\nALTER TABLE `db`.`websites`\nADD COLUMN `price_element` VARCHAR(45) NULL AFTER `keyword`,\nADD COLUMN `currency_element` VARCHAR(45) NULL AFTER `price_element`;\n\nALTER TABLE `db`.`prices`\nCHANGE COLUMN `id` `id` BIGINT(20) NOT NULL AUTO_INCREMENT ;','ALTER TABLE `db`.`products`\nCHANGE COLUMN `link_address` `link_address` VARCHAR(128) NULL DEFAULT NULL ;\n\nALTER TABLE `db`.`websites`\nDROP COLUMN `price_element`,\nDROP COLUMN `currency_element`;\n\nALTER TABLE `db`.`prices`\nCHANGE COLUMN `id` `id` BIGINT(20) NOT NULL ;','applied','Duplicate column name \'price_element\' [ERROR:1060, SQLSTATE:42S21]'),(2,'7cc3ec8b61af284b4beb91af15a2072ec6a8d463','2016-08-16 11:15:59','ALTER TABLE `db`.`websites`\nADD COLUMN `deleted` TINYINT(2) NULL DEFAULT NULL AFTER `currency_element`;','ALTER TABLE `db`.`websites`\nDROP COLUMN `deleted` TINYINT(2) NULL DEFAULT NULL AFTER `currency_element`;','applied',''),(3,'1f22aa505626bd3307f56eb27691534dbe67625c','2016-08-16 14:07:03','ALTER TABLE `db`.`categories`\nADD COLUMN `deleted` TINYINT(2) NULL DEFAULT NULL AFTER `name`;','ALTER TABLE `db`.`categories`\nDROP COLUMN `deleted` TINYINT(2) NULL DEFAULT NULL AFTER `name`;','applied','');
/*!40000 ALTER TABLE `play_evolutions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prices`
--

DROP TABLE IF EXISTS `prices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `prices` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) DEFAULT NULL,
  `price` float DEFAULT NULL,
  `input_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `product_id_idx` (`product_id`),
  CONSTRAINT `FKhpva2t51a39twh6gdkxdcllyf` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `product_id` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prices`
--

LOCK TABLES `prices` WRITE;
/*!40000 ALTER TABLE `prices` DISABLE KEYS */;
INSERT INTO `prices` VALUES (1,4,22.1016,'2016-08-16 15:11:54'),(2,2,682.283,'2016-08-16 15:11:54'),(3,3,682.283,'2016-08-16 15:11:54'),(4,1,682.283,'2016-08-16 15:11:54'),(5,5,682.059,'2016-08-16 15:12:10'),(6,6,219.157,'2016-08-17 07:43:45');
/*!40000 ALTER TABLE `prices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `products` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `link_address` varchar(512) DEFAULT NULL,
  `site_id` bigint(20) DEFAULT NULL,
  `product_name` varchar(128) DEFAULT NULL,
  `category_id` bigint(20) DEFAULT NULL,
  `deleted` tinyint(2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `site_id` (`site_id`),
  KEY `cat_id_idx` (`category_id`),
  CONSTRAINT `FKivma0jbyrtbk47vjlc8kjhcva` FOREIGN KEY (`site_id`) REFERENCES `websites` (`id`),
  CONSTRAINT `cat_id` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `site_id` FOREIGN KEY (`site_id`) REFERENCES `websites` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'http://www.emag.ro/laptop-asus-cu-procesor-intelr-coretm-i7-5500u-2-40ghz-broadwelltm-2-4ghz-15-6-4gb-500gb-dvd-rw-intelr-hd-graphics-microsoft-windows-10-black-p2520la-xo0764t/pd/DFDV13BBM/?ref=hp_prod-widget_1_5_4&recid=PRODUCTS_ANY',1,'Laptop ASUS P2520LA-XO0764T',2,0),(2,'http://www.emag.ro/laptop-asus-cu-procesor-intelr-coretm-i7-5500u-2-40ghz-broadwelltm-2-4ghz-15-6-4gb-500gb-dvd-rw-intelr-hd-graphics-microsoft-windows-10-black-p2520la-xo0764t/pd/DFDV13BBM/?ref=hp_prod-widget_1_5_4&recid=PRODUCTS_ANY',1,'Laptop ASUS P2520LA-XO0764T',2,0),(3,'http://www.emag.ro/laptop-asus-cu-procesor-intelr-coretm-i7-5500u-2-40ghz-broadwelltm-2-4ghz-15-6-4gb-500gb-dvd-rw-intelr-hd-graphics-microsoft-windows-10-black-p2520la-xo0764t/pd/DFDV13BBM/?ref=hp_prod-widget_1_5_4&recid=PRODUCTS_ANY',1,'Laptop ASUS P2520LA-XO0764T',2,0),(4,'http://www.emag.ro/memorie-hyperx-fury-blue-4gb-ddr3-1600mhz-cl10-1-5v-hx316c10f-4/pd/D0HP1BBBM/',1,'Memorie HyperX Fury Blue 4GB',NULL,0),(5,'http://www.pcgarage.ro/smartphone/samsung/sm-g935-samsung-galaxy-s7-edge-32gb-4g-black/',3,'Laptop Lenovo IdeaPad 100-15 i5-5200U 1TB-5400rpm 4GB DVDRW HD',2,0),(6,'http://www.ebay.com/itm/Factory-Unlocked-Apple-iPhone-5S-A1533-16GB-4G-LTE-Touch-ID-Mobile-Smartphone-/141843019610?hash=item210680b75a:g:6d4AAOSwbdpWXxNE',4,'iPhone 5s 16GB 4G',NULL,0);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `search_history`
--

DROP TABLE IF EXISTS `search_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `query_string` varchar(512) DEFAULT NULL,
  `date` timestamp NULL DEFAULT NULL,
  `account_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `product_id_idx` (`query_string`),
  KEY `account_id_idx` (`account_id`),
  CONSTRAINT `account_id` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `search_history`
--

LOCK TABLES `search_history` WRITE;
/*!40000 ALTER TABLE `search_history` DISABLE KEYS */;
INSERT INTO `search_history` VALUES (1,'test','2016-08-12 09:53:47',7),(2,'test','2016-08-12 09:53:48',7),(3,'test','2016-08-12 13:30:47',7),(4,'test','2016-08-12 13:30:48',7),(5,'test','2016-08-12 08:28:07',7),(6,'test','2016-08-12 08:28:08',7),(7,'test','2016-08-12 09:46:26',7),(8,'test','2016-08-12 09:46:27',7),(9,'test','2016-08-12 09:52:38',7),(10,'test','2016-08-12 09:52:39',7),(11,'abc','2016-08-16 09:59:18',1),(12,'laptop','2016-08-16 09:59:25',1),(13,'samsung','2016-08-16 10:30:39',1),(14,'samsung','2016-08-16 11:16:01',1);
/*!40000 ALTER TABLE `search_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `websites`
--

DROP TABLE IF EXISTS `websites`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `websites` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `s_url` varchar(255) NOT NULL,
  `keyword` varchar(45) DEFAULT NULL,
  `price_element` varchar(45) DEFAULT NULL,
  `currency_element` varchar(45) DEFAULT NULL,
  `deleted` tinyint(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `websites`
--

LOCK TABLES `websites` WRITE;
/*!40000 ALTER TABLE `websites` DISABLE KEYS */;
INSERT INTO `websites` VALUES (1,'http://www.emag.ro','none','price','priceCurrency',0),(2,'http://www.emag.com','none','price','priceCurrency',1),(3,'http://www.pcgarage.ro','none','price','priceCurrency',0),(4,'http://www.ebay.com','none','prcIsum','priceCurrency',0);
/*!40000 ALTER TABLE `websites` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-10-07 10:44:52
