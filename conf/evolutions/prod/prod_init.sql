-- MySQL dump 10.13  Distrib 5.5.18.1, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: prod
-- ------------------------------------------------------
-- Server version	5.5.18.1-Alibaba-4410-log

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
) ENGINE=InnoDB AUTO_INCREMENT=4269 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accountinfo`
--

LOCK TABLES `accountinfo` WRITE;
/*!40000 ALTER TABLE `accountinfo` DISABLE KEYS */;
/*!40000 ALTER TABLE `accountinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `appinfo`
--

DROP TABLE IF EXISTS `appinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `appinfo` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `version_code` int(11) NOT NULL,
  `version_name` varchar(20) COLLATE utf8_bin NOT NULL,
  `url` varchar(128) COLLATE utf8_bin NOT NULL,
  `summary` varchar(256) COLLATE utf8_bin NOT NULL,
  `file_size` bigint(20) NOT NULL,
  `release_time` bigint(20) NOT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `version_code` (`version_code`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appinfo`
--

LOCK TABLES `appinfo` WRITE;
/*!40000 ALTER TABLE `appinfo` DISABLE KEYS */;
/*!40000 ALTER TABLE `appinfo` ENABLE KEYS */;
UNLOCK TABLES;

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
  `comments` text COLLATE utf8_bin NOT NULL,
  `publisher` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT '',
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`),
  KEY `child_id` (`child_id`),
  KEY `school_id` (`school_id`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assess`
--

LOCK TABLES `assess` WRITE;
/*!40000 ALTER TABLE `assess` DISABLE KEYS */;
/*!40000 ALTER TABLE `assess` ENABLE KEYS */;
UNLOCK TABLES;

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
  `title` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT '',
  `content` text COLLATE utf8_bin NOT NULL,
  `image` text COLLATE utf8_bin,
  `publisher` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT '',
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignment`
--

LOCK TABLES `assignment` WRITE;
/*!40000 ALTER TABLE `assignment` DISABLE KEYS */;
/*!40000 ALTER TABLE `assignment` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
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
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chargeinfo`
--

LOCK TABLES `chargeinfo` WRITE;
/*!40000 ALTER TABLE `chargeinfo` DISABLE KEYS */;
/*!40000 ALTER TABLE `chargeinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `childinfo`
--

DROP TABLE IF EXISTS `childinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `childinfo` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) COLLATE utf8_bin NOT NULL,
  `child_id` varchar(40) COLLATE utf8_bin NOT NULL,
  `student_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `gender` tinyint(4) NOT NULL DEFAULT '2',
  `classname` varchar(40) COLLATE utf8_bin NOT NULL DEFAULT '',
  `picurl` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `birthday` date NOT NULL DEFAULT '1800-01-01',
  `indate` date NOT NULL DEFAULT '1800-01-01',
  `school_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `address` varchar(200) COLLATE utf8_bin DEFAULT '',
  `stu_type` tinyint(4) NOT NULL DEFAULT '2',
  `hukou` tinyint(4) NOT NULL DEFAULT '2',
  `social_id` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT '',
  `nick` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT '',
  `status` tinyint(4) NOT NULL DEFAULT '1',
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  `class_id` int(11) NOT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `child_id` (`child_id`),
  KEY `birthday` (`birthday`),
  KEY `school_id` (`school_id`),
  KEY `nick` (`nick`),
  KEY `class_id` (`class_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1786 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `childinfo`
--

LOCK TABLES `childinfo` WRITE;
/*!40000 ALTER TABLE `childinfo` DISABLE KEYS */;
/*!40000 ALTER TABLE `childinfo` ENABLE KEYS */;
UNLOCK TABLES;

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
  `class_name` varchar(40) COLLATE utf8_bin NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '1',
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`),
  KEY `school_id` (`school_id`),
  KEY `class_id` (`class_id`)
) ENGINE=InnoDB AUTO_INCREMENT=186 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classinfo`
--

LOCK TABLES `classinfo` WRITE;
/*!40000 ALTER TABLE `classinfo` DISABLE KEYS */;
/*!40000 ALTER TABLE `classinfo` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=206 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `conversation`
--

LOCK TABLES `conversation` WRITE;
/*!40000 ALTER TABLE `conversation` DISABLE KEYS */;
/*!40000 ALTER TABLE `conversation` ENABLE KEYS */;
UNLOCK TABLES;

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
  `mon_breakfast` varchar(40) COLLATE utf8_bin DEFAULT '',
  `tue_breakfast` varchar(40) COLLATE utf8_bin DEFAULT '',
  `wed_breakfast` varchar(40) COLLATE utf8_bin DEFAULT '',
  `thu_breakfast` varchar(40) COLLATE utf8_bin DEFAULT '',
  `fri_breakfast` varchar(40) COLLATE utf8_bin DEFAULT '',
  `mon_lunch` varchar(40) COLLATE utf8_bin DEFAULT '',
  `tue_lunch` varchar(40) COLLATE utf8_bin DEFAULT '',
  `wed_lunch` varchar(40) COLLATE utf8_bin DEFAULT '',
  `thu_lunch` varchar(40) COLLATE utf8_bin DEFAULT '',
  `fri_lunch` varchar(40) COLLATE utf8_bin DEFAULT '',
  `mon_dinner` varchar(40) COLLATE utf8_bin DEFAULT '',
  `tue_dinner` varchar(40) COLLATE utf8_bin DEFAULT '',
  `wed_dinner` varchar(40) COLLATE utf8_bin DEFAULT '',
  `thu_dinner` varchar(40) COLLATE utf8_bin DEFAULT '',
  `fri_dinner` varchar(40) COLLATE utf8_bin DEFAULT '',
  `mon_extra` varchar(40) COLLATE utf8_bin DEFAULT '',
  `tue_extra` varchar(40) COLLATE utf8_bin DEFAULT '',
  `wed_extra` varchar(40) COLLATE utf8_bin DEFAULT '',
  `thu_extra` varchar(40) COLLATE utf8_bin DEFAULT '',
  `fri_extra` varchar(40) COLLATE utf8_bin DEFAULT '',
  `extra_tip` text COLLATE utf8_bin NOT NULL,
  `status` int(11) DEFAULT '1',
  `timestamp` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`),
  KEY `school_id` (`school_id`),
  KEY `cookbook_id` (`cookbook_id`)
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cookbookinfo`
--

LOCK TABLES `cookbookinfo` WRITE;
/*!40000 ALTER TABLE `cookbookinfo` DISABLE KEYS */;
/*!40000 ALTER TABLE `cookbookinfo` ENABLE KEYS */;
UNLOCK TABLES;

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
  `notice_type` int(11) DEFAULT '0',
  `check_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`),
  KEY `check_at` (`check_at`),
  KEY `school_id` (`school_id`),
  KEY `child_id` (`child_id`)
) ENGINE=InnoDB AUTO_INCREMENT=244 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dailylog`
--

LOCK TABLES `dailylog` WRITE;
/*!40000 ALTER TABLE `dailylog` DISABLE KEYS */;
/*!40000 ALTER TABLE `dailylog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employeeinfo`
--

DROP TABLE IF EXISTS `employeeinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `employeeinfo` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) COLLATE utf8_bin NOT NULL,
  `employee_id` varchar(40) COLLATE utf8_bin NOT NULL,
  `phone` varchar(16) COLLATE utf8_bin NOT NULL,
  `gender` tinyint(4) NOT NULL DEFAULT '2',
  `workgroup` varchar(40) COLLATE utf8_bin NOT NULL DEFAULT '',
  `workduty` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT '',
  `picurl` varchar(128) COLLATE utf8_bin DEFAULT '',
  `birthday` date NOT NULL DEFAULT '1800-01-01',
  `school_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `login_password` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '',
  `login_name` varchar(32) COLLATE utf8_bin NOT NULL DEFAULT '',
  `status` tinyint(4) NOT NULL DEFAULT '1',
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `phone` (`phone`),
  UNIQUE KEY `employee_id` (`employee_id`),
  KEY `birthday` (`birthday`),
  KEY `school_id` (`school_id`)
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employeeinfo`
--

LOCK TABLES `employeeinfo` WRITE;
/*!40000 ALTER TABLE `employeeinfo` DISABLE KEYS */;
INSERT INTO `employeeinfo` VALUES (6,'超人','3_93740362_9972','13060003723',1,'保安组','员工','','1982-06-04','0','CDF3C60380FE6094BBF7F979243B280B','operator',1,0),(46,'老宋','3_0_1395417666704','12332232323',1,'','',NULL,'1980-01-01','0','7F79E592D3844379D1963409A78012D7','dataadmin',1,1395417666706);
/*!40000 ALTER TABLE `employeeinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feedback`
--

DROP TABLE IF EXISTS `feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `feedback` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `phone` varchar(16) COLLATE utf8_bin NOT NULL,
  `content` text COLLATE utf8_bin,
  `comment` text COLLATE utf8_bin,
  `status` tinyint(4) NOT NULL DEFAULT '1',
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedback`
--

LOCK TABLES `feedback` WRITE;
/*!40000 ALTER TABLE `feedback` DISABLE KEYS */;
/*!40000 ALTER TABLE `feedback` ENABLE KEYS */;
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
  `title` varchar(255) COLLATE utf8_bin NOT NULL,
  `content` text COLLATE utf8_bin,
  `image` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  `published` tinyint(4) NOT NULL DEFAULT '0',
  `status` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=348 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news`
--

LOCK TABLES `news` WRITE;
/*!40000 ALTER TABLE `news` DISABLE KEYS */;
/*!40000 ALTER TABLE `news` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
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
  KEY `school_id` (`school_id`),
  KEY `phone` (`phone`),
  KEY `birthday` (`birthday`)
) ENGINE=InnoDB AUTO_INCREMENT=4289 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parentinfo`
--

LOCK TABLES `parentinfo` WRITE;
/*!40000 ALTER TABLE `parentinfo` DISABLE KEYS */;
/*!40000 ALTER TABLE `parentinfo` ENABLE KEYS */;
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

LOCK TABLES `play_evolutions` WRITE;
/*!40000 ALTER TABLE `play_evolutions` DISABLE KEYS */;
INSERT INTO `play_evolutions` VALUES (1,'84a41a633ffaa7f2c6772b52c788c6ec4aef7bd9','2013-12-19 15:21:22','CREATE TABLE kindergarten (\nid    SERIAL PRIMARY KEY,\nname  VARCHAR(255) NOT NULL,\ntitle VARCHAR(255) NOT NULL\n);\n\nINSERT INTO kindergarten (name, title) VALUES\n(\'school23\', \'成都市第二十三幼儿园\'),\n(\'school1\', \'西安电子科技大学\'),\n(\'school2\', \'清华池\');','DROP TABLE IF EXISTS kindergarten;','applied',''),(2,'73dac56aef9073a57e231af00729cb43942ef3d5','2013-12-19 15:21:22','CREATE TABLE news (\nid        SERIAL PRIMARY KEY,\nk_id      LONG         NOT NULL,\ntitle     VARCHAR(255) NOT NULL,\ncontent   TEXT         NOT NULL,\nissueDate DATE         NOT NULL,\npublished INT          NOT NULL DEFAULT 0,\nstatus    INT          NOT NULL DEFAULT 1\n);\n\nINSERT INTO news (k_id, title, content, issueDate, published) VALUES\n(1, \'通知1\',\n\'缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知\',\n\'2013-10-01\', 1),\n(1, \'通知2\',\n\'学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电\',\n\'2013-1-01\', 1),\n(1, \'通知3\',\n\'恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击\',\n\'2012-10-01\', 0);','DROP TABLE IF EXISTS news;','applied',''),(3,'eb8a5c9a226a6f069a1074d0e74e131c2691ab0d','2013-12-19 15:21:22','CREATE TABLE parent (\nid       SERIAL PRIMARY KEY,\nk_id     LONG         NOT NULL,\nchild_id INT          NOT NULL,\nname     VARCHAR(255) NOT NULL,\ndesc     VARCHAR(255) NOT NULL\n);\n\nINSERT INTO parent (k_id, child_id, name, desc) VALUES (1, 1, \'你爸\', \'圆圆家长\'),\n(1, 2, \'尼玛\', \'土豆家长\'),\n(1, 3, \'你妹\', \'地瓜家长\');','DROP TABLE IF EXISTS parent;','applying_up','You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'desc     VARCHAR(255) NOT NULL\n)\' at line 6 [ERROR:1064, SQLSTATE:42000]');
/*!40000 ALTER TABLE `play_evolutions` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `privilege`
--

LOCK TABLES `privilege` WRITE;
/*!40000 ALTER TABLE `privilege` DISABLE KEYS */;
INSERT INTO `privilege` VALUES (2,'0','3_93740362_9972','operator','','',1,1333390313123),(10,'0','3_0_1395417666704','operator','','operator',1,1333390313123);
/*!40000 ALTER TABLE `privilege` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=4286 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `relationmap`
--

LOCK TABLES `relationmap` WRITE;
/*!40000 ALTER TABLE `relationmap` DISABLE KEYS */;
/*!40000 ALTER TABLE `relationmap` ENABLE KEYS */;
UNLOCK TABLES;

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
  `mon_am` varchar(40) COLLATE utf8_bin DEFAULT '',
  `tue_am` varchar(40) COLLATE utf8_bin DEFAULT '',
  `wed_am` varchar(40) COLLATE utf8_bin DEFAULT '',
  `thu_am` varchar(40) COLLATE utf8_bin DEFAULT '',
  `fri_am` varchar(40) COLLATE utf8_bin DEFAULT '',
  `mon_pm` varchar(40) COLLATE utf8_bin DEFAULT '',
  `tue_pm` varchar(40) COLLATE utf8_bin DEFAULT '',
  `wed_pm` varchar(40) COLLATE utf8_bin DEFAULT '',
  `thu_pm` varchar(40) COLLATE utf8_bin DEFAULT '',
  `fri_pm` varchar(40) COLLATE utf8_bin DEFAULT '',
  `status` int(11) DEFAULT '1',
  `timestamp` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scheduleinfo`
--

LOCK TABLES `scheduleinfo` WRITE;
/*!40000 ALTER TABLE `scheduleinfo` DISABLE KEYS */;
/*!40000 ALTER TABLE `scheduleinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schoolinfo`
--

DROP TABLE IF EXISTS `schoolinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `schoolinfo` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `school_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `province` varchar(20) COLLATE utf8_bin NOT NULL,
  `city` varchar(20) COLLATE utf8_bin NOT NULL,
  `address` text COLLATE utf8_bin,
  `token` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `name` varchar(20) COLLATE utf8_bin NOT NULL,
  `full_name` varchar(128) COLLATE utf8_bin NOT NULL,
  `description` text COLLATE utf8_bin,
  `phone` varchar(16) COLLATE utf8_bin NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '1',
  `logo_url` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `update_at` bigint(20) NOT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `school_id` (`school_id`),
  UNIQUE KEY `full_name` (`full_name`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schoolinfo`
--

LOCK TABLES `schoolinfo` WRITE;
/*!40000 ALTER TABLE `schoolinfo` DISABLE KEYS */;
/*!40000 ALTER TABLE `schoolinfo` ENABLE KEYS */;
UNLOCK TABLES;

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

-- Dump completed on 2014-04-01 18:11:13
