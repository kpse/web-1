-- MySQL dump 10.13  Distrib 5.5.18.1, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: prod
-- ------------------------------------------------------
-- Server version	5.5.18.1-Alibaba-rds-201406-log

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
-- Table structure for table `accountinfo`
--

DROP TABLE IF EXISTS `accountinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `accountinfo` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `accountid` varchar(16) COLLATE utf8_bin NOT NULL,
  `password` varchar(32) COLLATE utf8_bin NOT NULL,
  `pushid` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT '',
  `active` tinyint(4) NOT NULL DEFAULT '0',
  `pwd_change_time` bigint(20) NOT NULL DEFAULT '0',
  `device` int(11) NOT NULL DEFAULT '3',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `accountid` (`accountid`),
  KEY `pwd_change_time` (`pwd_change_time`),
  KEY `accountid_2` (`accountid`)
) ENGINE=InnoDB AUTO_INCREMENT=7752 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accountinfo`
--

--
-- Table structure for table `appinfo`
--

DROP TABLE IF EXISTS `appinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `appinfo` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `version_code` int(11) NOT NULL,
  `version_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `url` varchar(128) COLLATE utf8_bin NOT NULL,
  `summary` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `file_size` bigint(20) NOT NULL,
  `release_time` bigint(20) NOT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `version_code` (`version_code`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appinfo`
--



--
-- Table structure for table `assess`
--

DROP TABLE IF EXISTS `assess`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assess` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `school_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `child_id` varchar(40) COLLATE utf8_bin NOT NULL,
  `emotion` tinyint(4) DEFAULT '3',
  `dining` tinyint(4) DEFAULT '3',
  `rest` int(4) DEFAULT '3',
  `activity` int(4) DEFAULT '3',
  `game` int(4) DEFAULT '3',
  `exercise` int(4) DEFAULT '3',
  `self_care` int(4) DEFAULT '3',
  `manner` int(4) DEFAULT '3',
  `comments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `publisher` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT '',
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`),
  KEY `child_id` (`child_id`),
  KEY `school_id` (`school_id`)
) ENGINE=InnoDB AUTO_INCREMENT=113 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assess`
--



--
-- Table structure for table `assignment`
--

DROP TABLE IF EXISTS `assignment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assignment` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `school_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `class_id` int(11) NOT NULL,
  `title` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `image` text COLLATE utf8_bin,
  `publisher` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT '',
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignment`
--



--
-- Table structure for table `cardinfo`
--

DROP TABLE IF EXISTS `cardinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cardinfo` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `cardnum` varchar(20) COLLATE utf8_bin NOT NULL,
  `userid` varchar(40) COLLATE utf8_bin NOT NULL,
  `expiredate` date NOT NULL DEFAULT '2200-01-01',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `cardnum` (`cardnum`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cardinfo`
--

LOCK TABLES `cardinfo` WRITE;
/*!40000 ALTER TABLE `cardinfo` DISABLE KEYS */;
/*!40000 ALTER TABLE `cardinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chargeinfo`
--

DROP TABLE IF EXISTS `chargeinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chargeinfo` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `school_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `total_phone_number` int(11) DEFAULT '0',
  `expire_date` date NOT NULL DEFAULT '2200-01-01',
  `status` int(11) DEFAULT '1',
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chargeinfo`
--



--
-- Table structure for table `childinfo`
--

DROP TABLE IF EXISTS `childinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `childinfo` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `child_id` varchar(40) COLLATE utf8_bin NOT NULL,
  `student_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `gender` tinyint(4) NOT NULL DEFAULT '2',
  `classname` varchar(40) COLLATE utf8_bin NOT NULL DEFAULT '',
  `picurl` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `birthday` date NOT NULL DEFAULT '1800-01-01',
  `indate` date NOT NULL DEFAULT '1800-01-01',
  `school_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '''''',
  `stu_type` tinyint(4) NOT NULL DEFAULT '2',
  `hukou` tinyint(4) NOT NULL DEFAULT '2',
  `social_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `nick` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `status` tinyint(4) NOT NULL DEFAULT '1',
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  `class_id` int(11) NOT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `child_id` (`child_id`),
  KEY `birthday` (`birthday`),
  KEY `school_id` (`school_id`),
  KEY `nick` (`nick`),
  KEY `class_id` (`class_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5618 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `childinfo`
--

--
-- Table structure for table `classinfo`
--

DROP TABLE IF EXISTS `classinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `classinfo` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `school_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `class_id` int(11) NOT NULL,
  `class_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '1',
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`),
  KEY `school_id` (`school_id`),
  KEY `class_id` (`class_id`)
) ENGINE=InnoDB AUTO_INCREMENT=523 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classinfo`
--



--
-- Table structure for table `conversation`
--

DROP TABLE IF EXISTS `conversation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `conversation` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `school_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `phone` varchar(16) COLLATE utf8_bin NOT NULL,
  `content` text COLLATE utf8_bin NOT NULL,
  `image` text COLLATE utf8_bin,
  `sender` varchar(20) COLLATE utf8_bin DEFAULT '''''',
  `sender_id` varchar(16) COLLATE utf8_bin DEFAULT '',
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`),
  KEY `school_id` (`school_id`),
  KEY `phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=389 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `conversation`
--


--
-- Table structure for table `cookbookinfo`
--

DROP TABLE IF EXISTS `cookbookinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cookbookinfo` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `school_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `cookbook_id` int(11) NOT NULL,
  `mon_breakfast` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tue_breakfast` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `wed_breakfast` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `thu_breakfast` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fri_breakfast` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mon_lunch` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tue_lunch` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `wed_lunch` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `thu_lunch` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fri_lunch` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mon_dinner` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tue_dinner` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `wed_dinner` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `thu_dinner` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fri_dinner` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mon_extra` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tue_extra` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `wed_extra` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `thu_extra` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fri_extra` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `extra_tip` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` int(11) DEFAULT '1',
  `timestamp` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`),
  KEY `school_id` (`school_id`),
  KEY `cookbook_id` (`cookbook_id`)
) ENGINE=InnoDB AUTO_INCREMENT=98 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cookbookinfo`
--


--
-- Table structure for table `dailylog`
--

DROP TABLE IF EXISTS `dailylog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dailylog` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `school_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `child_id` varchar(40) COLLATE utf8_bin NOT NULL,
  `parent_name` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT '',
  `record_url` text COLLATE utf8_bin,
  `card_no` varchar(20) COLLATE utf8_bin NOT NULL,
  `card_type` int(11) DEFAULT '0',
  `notice_type` int(4) NOT NULL DEFAULT '0',
  `check_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`),
  KEY `check_at` (`check_at`),
  KEY `school_id` (`school_id`),
  KEY `child_id` (`child_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10198 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dailylog`
--


--
-- Table structure for table `employeeinfo`
--

DROP TABLE IF EXISTS `employeeinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `employeeinfo` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `employee_id` varchar(40) COLLATE utf8_bin NOT NULL,
  `phone` varchar(16) COLLATE utf8_bin NOT NULL,
  `gender` tinyint(4) NOT NULL DEFAULT '2',
  `workgroup` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `workduty` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `picurl` varchar(128) COLLATE utf8_bin DEFAULT '',
  `birthday` date NOT NULL DEFAULT '1800-01-01',
  `school_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `login_password` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '',
  `login_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `status` tinyint(4) NOT NULL DEFAULT '1',
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `phone` (`phone`),
  UNIQUE KEY `employee_id` (`employee_id`),
  KEY `birthday` (`birthday`),
  KEY `school_id` (`school_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1252 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employeeinfo`
--


--
-- Table structure for table `feedback`
--

DROP TABLE IF EXISTS `feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `feedback` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `phone` varchar(16) COLLATE utf8_bin NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `status` tinyint(4) NOT NULL DEFAULT '1',
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedback`
--

LOCK TABLES `feedback` WRITE;
/*!40000 ALTER TABLE `feedback` DISABLE KEYS */;
INSERT INTO `feedback` VALUES (23,'18782242007','你好',NULL,1,1396793987095),(24,'18782242007','Crag',NULL,1,1396794496964),(25,'13408654680','救苦救难\n',NULL,1,1396844212386),(26,'13402815317','测试测试',NULL,1,1396852594805),(27,'13880498549','我好',NULL,1,1396890815610),(28,'13880498549','我的神',NULL,1,1396923638692),(29,'13880498549','我',NULL,1,1396951651151),(30,'13402815317','他突然让人',NULL,1,1397014857713),(31,'13408654680','哈哈哈',NULL,1,1397025388158),(32,'13408654680','测试',NULL,1,1397116267566),(33,'18028191093','hafojulaenmolaledeemo',NULL,1,1399946737328),(34,'18028191093','你好',NULL,1,1400333021874),(35,'18028191093','挺不错的',NULL,1,1401069530796),(36,'13408654680','需要的话测试\n',NULL,1,1401374866309),(37,'15208306033','我饿',NULL,1,1401436036978);
/*!40000 ALTER TABLE `feedback` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `macwhitelist`
--

DROP TABLE IF EXISTS `macwhitelist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `macwhitelist` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `school_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `mac` char(20) COLLATE utf8_bin NOT NULL,
  `encoded_mac` char(32) COLLATE utf8_bin NOT NULL,
  `status` int(11) DEFAULT '1',
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `mac` (`mac`,`encoded_mac`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `macwhitelist`
--

LOCK TABLES `macwhitelist` WRITE;
/*!40000 ALTER TABLE `macwhitelist` DISABLE KEYS */;
/*!40000 ALTER TABLE `macwhitelist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `news`
--

DROP TABLE IF EXISTS `news`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `news` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `school_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `class_id` int(11) NOT NULL DEFAULT '0',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `image` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  `published` tinyint(4) NOT NULL DEFAULT '0',
  `status` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=380 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news`
--



--
-- Table structure for table `newsread`
--

DROP TABLE IF EXISTS `newsread`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `newsread` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `school_id` varchar(20) NOT NULL,
  `parent_id` varchar(40) NOT NULL,
  `news_id` int(11) NOT NULL,
  `readTime` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `newsread`
--

LOCK TABLES `newsread` WRITE;
/*!40000 ALTER TABLE `newsread` DISABLE KEYS */;
/*!40000 ALTER TABLE `newsread` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parentinfo`
--

DROP TABLE IF EXISTS `parentinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `parentinfo` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) COLLATE utf8_bin NOT NULL,
  `parent_id` varchar(40) COLLATE utf8_bin NOT NULL,
  `relationship` varchar(20) COLLATE utf8_bin NOT NULL,
  `phone` varchar(16) COLLATE utf8_bin NOT NULL,
  `gender` tinyint(4) NOT NULL DEFAULT '2',
  `company` varchar(200) COLLATE utf8_bin DEFAULT '''''',
  `picurl` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `birthday` date NOT NULL DEFAULT '1800-01-01',
  `school_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '1',
  `member_status` tinyint(4) NOT NULL DEFAULT '1',
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `parent_id` (`parent_id`),
  UNIQUE KEY `phone_2` (`phone`),
  KEY `school_id` (`school_id`),
  KEY `phone` (`phone`),
  KEY `birthday` (`birthday`)
) ENGINE=InnoDB AUTO_INCREMENT=7783 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parentinfo`
--


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
  `apply_script` text,
  `revert_script` text,
  `state` varchar(255) DEFAULT NULL,
  `last_problem` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `play_evolutions`
--


--
-- Table structure for table `privilege`
--

DROP TABLE IF EXISTS `privilege`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `privilege` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `school_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `employee_id` varchar(40) COLLATE utf8_bin NOT NULL,
  `group` varchar(10) COLLATE utf8_bin NOT NULL,
  `subordinate` varchar(128) COLLATE utf8_bin NOT NULL,
  `promoter` varchar(40) COLLATE utf8_bin NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '1',
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=128 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `privilege`
--



--
-- Table structure for table `relationmap`
--

DROP TABLE IF EXISTS `relationmap`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `relationmap` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `child_id` varchar(40) COLLATE utf8_bin NOT NULL,
  `parent_id` varchar(40) COLLATE utf8_bin NOT NULL,
  `card_num` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT '''''',
  `relationship` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT '''妈妈''',
  `status` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `card_num` (`card_num`),
  KEY `child_id` (`child_id`),
  KEY `parent_id` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7822 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `relationmap`
--



--
-- Table structure for table `scheduleinfo`
--

DROP TABLE IF EXISTS `scheduleinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scheduleinfo` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `school_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `class_id` int(11) NOT NULL,
  `schedule_id` int(11) NOT NULL,
  `mon_am` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tue_am` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `wed_am` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `thu_am` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fri_am` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mon_pm` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tue_pm` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `wed_pm` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `thu_pm` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fri_pm` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` int(11) DEFAULT '1',
  `timestamp` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scheduleinfo`
--



--
-- Table structure for table `schoolinfo`
--

DROP TABLE IF EXISTS `schoolinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `schoolinfo` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `school_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `province` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `city` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `token` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `full_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `phone` varchar(16) COLLATE utf8_bin NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '1',
  `logo_url` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `update_at` bigint(20) NOT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `school_id` (`school_id`),
  UNIQUE KEY `full_name` (`full_name`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schoolinfo`
--



--
-- Table structure for table `sessionlog`
--

DROP TABLE IF EXISTS `sessionlog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sessionlog` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `school_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `session_id` varchar(40) COLLATE utf8_bin NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `media_url` text COLLATE utf8_bin,
  `media_type` varchar(10) COLLATE utf8_bin DEFAULT 'image',
  `sender` varchar(40) COLLATE utf8_bin NOT NULL DEFAULT '',
  `sender_type` char(1) COLLATE utf8_bin NOT NULL DEFAULT 't',
  `status` tinyint(4) NOT NULL DEFAULT '1',
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=335 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sessionlog`
--



--
-- Table structure for table `sessionread`
--

DROP TABLE IF EXISTS `sessionread`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sessionread` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `school_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `reader_id` varchar(40) COLLATE utf8_bin NOT NULL,
  `topic` varchar(40) COLLATE utf8_bin NOT NULL,
  `last_read_session_id` int(11) NOT NULL,
  `read_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sessionread`
--



--
-- Dumping events for database 'prod'
--

--
-- Dumping routines for database 'prod'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-06-17  2:09:18
