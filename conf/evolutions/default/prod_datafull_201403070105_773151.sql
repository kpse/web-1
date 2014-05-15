-- MySQL dump 10.13  Distrib 5.5.18.1, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: prod
-- ------------------------------------------------------
-- Server version	5.5.18.1-Alibaba-4410-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE = @@TIME_ZONE */;
/*!40103 SET TIME_ZONE = '+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;

--
-- Table structure for table `accountinfo`
--

DROP TABLE IF EXISTS `accountinfo`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `accountinfo` (
  `uid`             INT(11)          NOT NULL AUTO_INCREMENT,
  `accountid`       VARCHAR(16)
                    COLLATE utf8_bin NOT NULL,
  `password`        VARCHAR(32)
                    COLLATE utf8_bin NOT NULL,
  `pushid`          VARCHAR(20)
                    COLLATE utf8_bin NOT NULL DEFAULT '',
  `active`          TINYINT(4)       NOT NULL DEFAULT '0',
  `pwd_change_time` BIGINT(20)       NOT NULL DEFAULT '0',
  `device`          INT(11)          NOT NULL DEFAULT '3',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `accountid` (`accountid`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =156
  DEFAULT CHARSET =utf8
  COLLATE =utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accountinfo`
--

LOCK TABLES `accountinfo` WRITE;
/*!40000 ALTER TABLE `accountinfo` DISABLE KEYS */;
INSERT INTO `accountinfo` VALUES
  (2, '18980030903 ', 'E3CEB5881A0A1FDAAD01296D7554868D', '963386802751977894', 1, 1392189362137,
   3), (3, '13408654680', '343B1C4A3EA721B2D640FC8700DB0F36', '', 1, 1390314328393, 3), (28, '13408654681', 'B773DEC77068E157D4DD6D07CBABE20D', '925387477040814447', 1, 1389413907801, 3), (30, '13408654688', 'F7EAA1EF40E26AF302BA377084250DF4', '925387477040814447', 1, 1389445860969, 3), (31, '13408654689', 'ED80DA657637479B9CEF31C6F756F70A', '925387477040814447', 1, 1389452077481, 3), (108, '13708089040', 'E10ADC3949BA59ABBE56E057F20F883E', '1036517187638489028', 1, 1389681948671, 3), (141, '13402815317', '3FDE6BB0541387E4EBDADF7C2FF31123', '615635431466128843', 1, 1394011486816, 3), (146, '13279491366', '7A7BEBBADD2FF79E3F2E19F09F1F236F', '1048149352597220995', 1, 1392731487949, 3), (147, '15828178309', 'DE97E09CDEF1830B5D8673D6563BE48E', '703964538983633996', 1, 1392003788416, 3), (148, '18611055373', 'FCBFDD6EA9DEC7E15F348558ACFC0F46', '1100187991584280825', 1, 1391836984695, 3), (152, '21211222232', '8DBE2D9405914A3F9D402B3CD1A3856A', '', 0, 0, 3),
  (153, '34234234234', 'ABF14FFD60451BC74BE025D0ADB77CAE', '', 0, 0, 3),
  (154, '34252535344', 'DF4FFB9462AD143C5AFA969D9DF7AE2A', '', 0, 0, 3),
  (155, '18782242007', '991F52446AB4B4DC28BF4256B45F0CA8', '', 1, 1394011917714, 4);
/*!40000 ALTER TABLE `accountinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `appinfo`
--

DROP TABLE IF EXISTS `appinfo`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `appinfo` (
  `uid`          INT(11)          NOT NULL AUTO_INCREMENT,
  `version_code` INT(11)          NOT NULL,
  `version_name` VARCHAR(20)
                 COLLATE utf8_bin NOT NULL,
  `url`          VARCHAR(128)
                 COLLATE utf8_bin NOT NULL,
  `summary`      VARCHAR(256)
                 COLLATE utf8_bin NOT NULL,
  `file_size`    BIGINT(20)       NOT NULL,
  `release_time` BIGINT(20)       NOT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `version_code` (`version_code`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =30
  DEFAULT CHARSET =utf8
  COLLATE =utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appinfo`
--

LOCK TABLES `appinfo` WRITE;
/*!40000 ALTER TABLE `appinfo` DISABLE KEYS */;
INSERT INTO `appinfo` VALUES (1, 4, 'V1.1', '/version/LoginTest_4.apk', '1.修正错误', 12344,
                              2013), (9, 7, 'v1.2', '/version/LoginTest_7.apk', '1.新增功能\\n2.该版本号是7', 22345, 2013), (10, 11, 'v2.0', '/version/release-11.apk', '1.新增园内通知功能\\n2.该版本号为11', 324709, 2013), (12, 12, 'v2.1', '/version/release-12.apk', '1.新增学习内容和每日育情\\n2.该版本号为12', 324877, 2013), (13, 5, 'V2.0', 'http://suoqin-test.u.qiniudn.com/release_2.apk', '测试上传版本', 510721, 1387896730359), (14, 6, 'V2.1', 'http://suoqin-test.u.qiniudn.com/test-release.apk', '测试更新版本功能哈哈哈', 510724, 1388141224584), (18, 13, '2.2', 'http://suoqin-test.u.qiniudn.com/FizK0yXn71Oh4LmtMxzbEhrpX-D2', 'asdasdasd', 23496, 1389623823877), (19, 14, 'V1.4', 'http://suoqin-test.u.qiniudn.com/FmTOwQUwoZ4CD80Y8LDVR7mjiAfX', '111111', 2899008, 1389624010031), (20, 15, 'V1.5', 'http://suoqin-test.u.qiniudn.com/FqdF63co-YiwhDYOrGaPxf2fr9tM', '1.优化UI \n2.修改bug', 2121426, 1389632745282), (21, 16, 'V1.0.5', 'http://suoqin-test.u.qiniudn.com/FhdFQTwKiAIx7dlzKiTF-GQ1q0J0', '1.优化ui\n2.修改bug', 2186864, 1389717968289), (22, 17, '测试', 'http://suoqin-test.u.qiniudn.com/FghHtfBwv0-sr9XD1UvELZIuLQSc', '测试版本发布', 169551, 1390143035711),
  (23, 18, 'V1.0.8', 'http://suoqin-test.u.qiniudn.com/FowKN7BRJl6ymTDd8vAcU2dGetmR', '1.适配全新UI\n2.支持小孩信息同步到服务器',
   2293089, 1390144908530),
  (24, 19, 'v1.0.9', 'http://suoqin-test.u.qiniudn.com/Fm7VsKQXZEsXKRWonMzu1Sa-_S7n', '1.解决bug\n2.微调ui', 2191986,
   1390240282358),
  (25, 20, 'v1.1.0', 'http://suoqin-test.u.qiniudn.com/Fo1wBIbxS2zMWF7SdkmhVgMWo-UN', '1.增加用户反馈功能\n2.继续调整UI', 2918776,
   1390321975834), (26, 21, 'V1.1.1', 'http://suoqin-test.u.qiniudn.com/FiNcefonODjeTdmEe0r8AqdbA3Yw',
                    '1.优化push相关功能\n2.增加\"关于我们\"页面\n3.微调UI', 2752582, 1390408857869),
  (27, 22, 'V1.1.2', 'http://suoqin-test.u.qiniudn.com/FuPHaXiIWB2Uz6MDMAT140yKMhMF', '1.使用新图', 3238372, 1390526428176),
  (28, 23, 'V1.1.3', 'http://suoqin-test.u.qiniudn.com/FjeVSUUUluaLZjzxdMp73SXVAM3q', '1.调整UI\n2.后台数据结构调整\n3.代码整理',
   3325608, 1390876493462);
/*!40000 ALTER TABLE `appinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assess`
--

DROP TABLE IF EXISTS `assess`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assess` (
  `uid`        INT(11)          NOT NULL AUTO_INCREMENT,
  `school_id`  VARCHAR(20)
               COLLATE utf8_bin NOT NULL,
  `child_id`   VARCHAR(40)
               COLLATE utf8_bin NOT NULL,
  `emotion`    TINYINT(4) DEFAULT '3',
  `dining`     TINYINT(4) DEFAULT '3',
  `rest`       INT(4) DEFAULT '3',
  `activity`   INT(4) DEFAULT '3',
  `game`       INT(4) DEFAULT '3',
  `exercise`   INT(4) DEFAULT '3',
  `self_care`  INT(4) DEFAULT '3',
  `manner`     INT(4) DEFAULT '3',
  `comments`   TEXT
               COLLATE utf8_bin NOT NULL,
  `publisher`  VARCHAR(20)
               COLLATE utf8_bin NOT NULL DEFAULT '',
  `publish_at` BIGINT(20)       NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`),
  KEY `child_id` (`child_id`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =5
  DEFAULT CHARSET =utf8
  COLLATE =utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assess`
--

LOCK TABLES `assess` WRITE;
/*!40000 ALTER TABLE `assess` DISABLE KEYS */;
INSERT INTO `assess` VALUES (2, '93740362', '1_1391836223533', 1, 3, 3, 3, 3, 3, 3, 3, '我没说什么。', '马老师', 12312313123),
  (3, '93740362', '1_1391836223533', 2, 3, 3, 3, 3, 3, 3, 3, '总的来说，不认识', '朱老师', 12390313123),
  (4, '93740362', '1_93740362_456', 3, 3, 3, 3, 3, 3, 3, 3, '我这周请假了', '杨老师', 12314313123);
/*!40000 ALTER TABLE `assess` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assignment`
--

DROP TABLE IF EXISTS `assignment`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assignment` (
  `uid`       INT(11)          NOT NULL AUTO_INCREMENT,
  `school_id` VARCHAR(20)
              COLLATE utf8_bin NOT NULL,
  `class_id`  INT(11)          NOT NULL,
  `title`     VARCHAR(20)
              COLLATE utf8_bin NOT NULL DEFAULT '',
  `content`   TEXT
              COLLATE utf8_bin NOT NULL,
  `image`     TEXT
              COLLATE utf8_bin,
  `publisher` VARCHAR(20)
              COLLATE utf8_bin NOT NULL DEFAULT '',
  `timestamp` BIGINT(20)       NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =5
  DEFAULT CHARSET =utf8
  COLLATE =utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignment`
--

LOCK TABLES `assignment` WRITE;
/*!40000 ALTER TABLE `assignment` DISABLE KEYS */;
INSERT INTO `assignment` VALUES
  (2, '93740362', 777888, '老师你好。', '今天的作业是写生。', 'http://suoqin-test.u.qiniudn.com/FgPmIcRG6BGocpV1B9QMCaaBQ9LK', '马老师',
   12312313123),
  (3, '93740362', 777888, '家长你好。', '今天的作业是吃饭。', 'http://suoqin-test.u.qiniudn.com/Fget0Tx492DJofAy-ZeUg1SANJ4X', '朱老师',
   12313313123),
  (4, '93740362', 777666, '老师再见。', '今天请带一只小兔子回家。1', 'http://suoqin-test.u.qiniudn.com/FgPmIcRG6BGocpV1B9QMCaaBQ9LK',
   '杨老师', 1393344722731);
/*!40000 ALTER TABLE `assignment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cardinfo`
--

DROP TABLE IF EXISTS `cardinfo`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cardinfo` (
  `uid`        INT(11)          NOT NULL AUTO_INCREMENT,
  `cardnum`    VARCHAR(20)
               COLLATE utf8_bin NOT NULL,
  `userid`     VARCHAR(40)
               COLLATE utf8_bin NOT NULL,
  `expiredate` DATE             NOT NULL DEFAULT '2200-01-01',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `cardnum` (`cardnum`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =35
  DEFAULT CHARSET =utf8
  COLLATE =utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cardinfo`
--

LOCK TABLES `cardinfo` WRITE;
/*!40000 ALTER TABLE `cardinfo` DISABLE KEYS */;
INSERT INTO `cardinfo` VALUES (1, '0001234567', '2_93740362_123',
                               '2200-01-01'), (2, '1231212312', '2_93740362_456', '2200-01-01'), (3, '0001234569', '2_93740362_789', '2200-01-01'), (4, '0001234560', '2_93740362_792', '2200-01-01'), (5, '0002234567', '3_93740362_1122', '2200-01-01'), (6, '0003234567', '3_93740362_3344', '2200-01-01'), (7, '0004234567', '3_93740362_9977', '2200-01-01'), (8, '1234567890', '2_1389282589843', '2200-01-01'), (9, '1234567891', '2_1389413328789', '2200-01-01'), (10, '1234567888', '2_1389444979512', '2200-01-01'), (11, '5555555555', '2_1389445750151', '2200-01-01'), (12, '1231231231', '2_1389452053171', '2200-01-01'), (13, '1111111111', '2_1389455065144', '2200-01-01'), (16, '1111122222', '2_1389629587768', '2200-01-01'), (17, '1234563245', '2_1389630084136', '2200-01-01'), (18, '1234567823', '2_1389631806879', '2200-01-01'), (20, '1234599919', '2_1389632090337', '2200-01-01'), (21, '1212121212', '2_1389634766817', '2200-01-01'), (22, '0001234561', '2_1389638862033', '2200-01-01'), (23, '1231231212', '2_1389696347532', '2200-01-01'), (25, '1231231110', '2_1389808175629', '2200-01-01'),
  (26, '1212123123', '2_1389886107277', '2200-01-01'), (27, '1212111212', '2_1390238560925', '2200-01-01'),
  (28, '0004087891', '2_1390359054366', '2200-01-01'), (29, '1234598765', '2_1391836223533', '2200-01-01'),
  (31, '1111144444', '2_1392191766937', '2200-01-01'), (32, '3423423423', '2_1393038181636', '2200-01-01'),
  (33, '2233333222', '2_1393168619323', '2200-01-01'), (34, '3434343434', '2_1393257448133', '2200-01-01');
/*!40000 ALTER TABLE `cardinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `childinfo`
--

DROP TABLE IF EXISTS `childinfo`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `childinfo` (
  `uid`        INT(11)          NOT NULL AUTO_INCREMENT,
  `name`       VARCHAR(20)
               COLLATE utf8_bin NOT NULL,
  `child_id`   VARCHAR(40)
               COLLATE utf8_bin NOT NULL,
  `student_id` VARCHAR(20)
               COLLATE utf8_bin NOT NULL,
  `gender`     TINYINT(4)       NOT NULL DEFAULT '2',
  `classname`  VARCHAR(40)
               COLLATE utf8_bin NOT NULL DEFAULT '',
  `picurl`     VARCHAR(128)
               COLLATE utf8_bin NOT NULL DEFAULT '',
  `birthday`   DATE             NOT NULL DEFAULT '1800-01-01',
  `indate`     DATE             NOT NULL DEFAULT '1800-01-01',
  `school_id`  VARCHAR(20)
               COLLATE utf8_bin NOT NULL,
  `address`    VARCHAR(200)
               COLLATE utf8_bin NOT NULL DEFAULT '',
  `stu_type`   TINYINT(4)       NOT NULL DEFAULT '2',
  `hukou`      TINYINT(4)       NOT NULL DEFAULT '2',
  `social_id`  VARCHAR(20)
               COLLATE utf8_bin NOT NULL DEFAULT '',
  `nick`       VARCHAR(20)
               COLLATE utf8_bin NOT NULL DEFAULT '',
  `status`     TINYINT(4)       NOT NULL DEFAULT '1',
  `update_at`  BIGINT(20)       NOT NULL DEFAULT '0',
  `class_id`   INT(11)          NOT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `child_id` (`child_id`),
  KEY `birthday` (`birthday`),
  KEY `school_id` (`school_id`),
  KEY `nick` (`nick`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =160
  DEFAULT CHARSET =utf8
  COLLATE =utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `childinfo`
--

LOCK TABLES `childinfo` WRITE;
/*!40000 ALTER TABLE `childinfo` DISABLE KEYS */;
INSERT INTO `childinfo` VALUES (1, '小香蕉', '1_93740362_374', '0032', 1, '',
                                'http://cocobabys.oss.aliyuncs.com/child_photo/93740362/1_93740362_374/1_93740362_374.jpg',
                                '2010-08-04', '2007-08-04', '93740362', '', 1, 1, '510107123416547856', '大西瓜', 1,
                                1391831671469,
                                777888), (2, '李小华', '1_93740362_456', '0021', 0, '', 'http://cocobabys.oss.aliyuncs.com/child_photo/93740362/1_93740362_456/1_93740362_456.jpg', '2011-09-09', '2011-09-09', '93740362', '', 1, 1, '510107123416511111', '李燕22', 1, 1393035671444, 777999), (3, '王大侠', '1_93740362_9982', '322', 1, '', 'http://img.zwbk.org/baike/spic/2010/12/01/2010120110214141_3528.jpg', '2007-01-01', '2011-09-03', '93740362', '', 0, 2, '510107123416523456', '小香蕉', 1, 1387360036, 777999), (4, '王大锤', '1_93740362_778', '323', 1, '', 'http://suoqin-test.u.qiniudn.com/FjaALPIxt3XksxDYH7dChgeXbcW3', '2007-01-01', '2007-01-01', '93740362', '', 0, 2, '510107123416523459', '绿葡萄', 1, 1389206977034, 777666), (5, '张小白', '1_1388951098434', '13889', 1, '', '/assets/images/portrait_placeholder.png', '1999-07-05', '1999-07-05', '93740362', 'address', 2, 1, 'social_id', '小白', 1, 1388951098434, 101), (7, '小杨未', '1_1389025352164', '13890', 0, '', '/assets/images/portrait_placeholder.png', '1999-07-05', '1999-07-05', '93740362', 'address', 2, 1, 'social_id', '洋洋', 1, 1389027748456, 101), (8, '端到端', '1_1389027537681', '13890', 1, '', '/assets/images/portrait_placeholder.png', '1999-07-05', '1999-07-05', '93740362', 'address', 2, 1, 'social_id', '按时打算的', 1, 1389027537681, 101), (9, '啊大啊啊为爱的', '1_1389082522909', '13890', 1, '', '/assets/images/portrait_placeholder.png', '1999-07-05', '1999-07-05', '93740362', 'address', 2, 1, 'social_id', '啊大', 1, 1389082522909, 101), (10, '额大大', '1_1389083059431', '13890', 1, '', '/assets/images/portrait_placeholder.png', '1999-07-05', '1999-07-05', '93740362', 'address', 2, 1, 'social_id', '的爱的', 1, 1389083059431, 101), (11, '13 13', '1_1389083126310', '13890', 1, '', 'http://suoqin-test.u.qiniudn.com/FjeuQNQ-pfNzn1tBcVShNufUNRjh', '1999-07-05', '1999-07-05', '93740362', 'address', 2, 1, 'social_id', '13 13', 1, 1389148199425, 101), (12, 'jl;lj;', '1_1389182159072', '13891', 1, '', '/assets/images/portrait_placeholder.png', '1999-07-05', '1999-07-05', '93740362', 'address', 2, 1, 'social_id', ';ll;l;l;', 1, 1389182159072, 101), (13, '4の24', '1_1389182248245', '13891', 1, '', '/assets/images/portrait_placeholder.png', '1999-07-05', '1999-07-05', '93740362', 'address', 2, 1, 'social_id', '323', 1, 1389182248245, 101), (14, '测试小宝', '1_1389282589846', '13892', 1, '', 'http://suoqin-test.u.qiniudn.com/undefined', '2009-01-01', '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '西安', 1, 1389282589846, 777888), (16, '测试小宝', '1_1389282590261', '13892', 1, '', 'http://suoqin-test.u.qiniudn.com/undefined', '2009-01-01', '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '西安', 1, 1389282590261, 777888), (17, '测试小宝', '1_1389282590437', '13892', 1, '', 'http://suoqin-test.u.qiniudn.com/FjaALPIxt3XksxDYH7dChgeXbcW3', '2009-01-01', '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '西安', 1, 1389282590437, 777888), (18, '测试小宝', '1_1389282590433', '13892', 1, '', 'http://suoqin-test.u.qiniudn.com/FjaALPIxt3XksxDYH7dChgeXbcW3', '2009-01-01', '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '西安', 1, 1389282590433, 777888), (19, '测试小宝', '1_1389282590523', '13892', 1, '', 'http://suoqin-test.u.qiniudn.com/FjaALPIxt3XksxDYH7dChgeXbcW3', '2009-01-01', '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '西安', 1, 1389282590523, 777888), (20, '测试小宝', '1_1389282590553', '13892', 1, '', 'http://suoqin-test.u.qiniudn.com/FjaALPIxt3XksxDYH7dChgeXbcW3', '2009-01-01', '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '西安', 1, 1389282590553, 777888), (21, '测试小宝', '1_1389282590889', '13892', 1, '', 'http://suoqin-test.u.qiniudn.com/FjaALPIxt3XksxDYH7dChgeXbcW3', '2009-01-01', '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '西安', 1, 1389282590889, 777888), (22, '测试小宝', '1_1389282591129', '13892', 1, '', 'http://suoqin-test.u.qiniudn.com/FjaALPIxt3XksxDYH7dChgeXbcW3', '2009-01-01', '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '西安', 1, 1389282591129, 777888), (23, '测试小宝', '1_1389282591221', '13892', 1, '', 'http://suoqin-test.u.qiniudn.com/undefined', '2009-01-01', '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '西安', 1, 1389282591221, 777888), (24, '测试小宝', '1_1389282591271', '13892', 1, '', 'http://suoqin-test.u.qiniudn.com/FjaALPIxt3XksxDYH7dChgeXbcW3', '2009-01-01', '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '西安', 1, 1389282591271, 777888), (25, '测试小宝', '1_1389282591313', '13892', 1, '', 'http://suoqin-test.u.qiniudn.com/FjaALPIxt3XksxDYH7dChgeXbcW3', '2009-01-01', '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '西安', 1, 1389282591313, 777888), (26, '测试小宝', '1_1389282591357', '13892', 1, '', 'http://suoqin-test.u.qiniudn.com/FjaALPIxt3XksxDYH7dChgeXbcW3', '2009-01-01', '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '西安', 1, 1389282591357, 777888), (27, '代斌', '1_1389413328789', '13894', 0, '', 'http://suoqin-test.u.qiniudn.com/Fia-Mez-8gCBOZtZHhtWycny1_xW', '2009-01-01', '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '娃娃', 1, 1389413328789, 777666), (28, '大保健', '1_1389444979512', '13894', 1, '水果班', '/assets/images/portrait_placeholder.png', '2009-01-01', '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '笑嫣然', 1, 1389550479763, 777999), (29, '妞妞', '1_1389445750151', '13894', 1, '水果班', 'http://suoqin-test.u.qiniudn.com/Fv6nuPg3hg7IMxlukZnn1r4S9crB', '2009-01-01', '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '妞妞', 1, 1389445820830, 777999), (30, '小龙', '1_1389452053171', '13894', 0, '水果班', 'http://suoqin-test.u.qiniudn.com/FjFLCY-DwjqHupfVc1eS1N-JOyO0', '2009-01-01', '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '小龙', 1, 1389452526277, 777666), (31, '看看', '1_1389455065144', '13894', 0, '水果班', '/assets/images/portrait_placeholder.png', '2009-01-01', '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '看看', 1, 1389455065144, 777888), (38, '232', '1_1389627183845', '13896', 1, '水果班', '/assets/images/portrait_placeholder.png', '2009-01-01', '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '123123', 1, 1389627183845, 777888), (39, '建创新', '1_1389627298519', '13896', 1, '水果班', '/assets/images/portrait_placeholder.png', '2009-01-01', '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '出差', 1, 1389627298519, 777888), (49, '21321', '1_1389630084136', '13896', 1, '水果班', '/assets/images/portrait_placeholder.png', '2009-01-01', '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '123123', 1, 1389630084136, 777888), (85, '李沛', '1_1389631907403', '13896', 0, '水果班', '/assets/images/portrait_placeholder.png', '2011-08-25', '2011-08-25', '93740362', 'address', 2, 1, 'social_id', '豆瓣', 1, 1389631907403, 777888), (86, '李沛', '1_1389631909956', '13896', 0, '水果班', '/assets/images/portrait_placeholder.png', '2011-08-25', '2011-08-25', '93740362', 'address', 2, 1, 'social_id', '豆瓣', 1, 1389631909956, 777888), (87, '李沛', '1_1389631910163', '13896', 0, '水果班', '/assets/images/portrait_placeholder.png', '2011-08-25', '2011-08-25', '93740362', 'address', 2, 1, 'social_id', '豆瓣', 1, 1389631910163, 777888), (88, '李沛', '1_1389631910405', '13896', 0, '水果班', '/assets/images/portrait_placeholder.png', '2011-08-25', '2011-08-25', '93740362', 'address', 2, 1, 'social_id', '豆瓣', 1, 1389631910405, 777888), (89, '李沛', '1_1389631913819', '13896', 0, '水果班', '/assets/images/portrait_placeholder.png', '2011-08-25', '2011-08-25', '93740362', 'address', 2, 1, 'social_id', '豆', 1, 1389631913819, 777888), (90, '李沛', '1_1389631914800', '13896', 0, '水果班', '/assets/images/portrait_placeholder.png', '2011-08-25', '2011-08-25', '93740362', 'address', 2, 1, 'social_id', '豆', 1, 1389631914800, 777888), (91, '李沛', '1_1389631915168', '13896', 0, '水果班', '/assets/images/portrait_placeholder.png', '2011-08-25', '2011-08-25', '93740362', 'address', 2, 1, 'social_id', '豆', 1, 1389631915168, 777888), (92, '李沛', '1_1389631915442', '13896', 0, '水果班', '/assets/images/portrait_placeholder.png', '2011-08-25', '2011-08-25', '93740362', 'address', 2, 1, 'social_id', '豆', 1, 1389631915442, 777888), (93, '李沛', '1_1389631915745', '13896', 0, '水果班', '/assets/images/portrait_placeholder.png', '2011-08-25', '2011-08-25', '93740362', 'address', 2, 1, 'social_id', '豆', 1, 1389631915745, 777888), (94, '李沛', '1_1389631916017', '13896', 0, '水果班', '/assets/images/portrait_placeholder.png', '2011-08-25', '2011-08-25', '93740362', 'address', 2, 1, 'social_id', '豆', 1, 1389631916017, 777888), (95, '李沛', '1_1389631916300', '13896', 0, '水果班', '/assets/images/portrait_placeholder.png', '2011-08-25', '2011-08-25', '93740362', 'address', 2, 1, 'social_id', '豆', 1, 1389631916300, 777888), (96, '李沛', '1_1389631916859', '13896', 0, '水果班', '/assets/images/portrait_placeholder.png', '2011-08-25', '2011-08-25', '93740362', 'address', 2, 1, 'social_id', '豆', 1, 1389631916859, 777888), (97, '李沛', '1_1389631917435', '13896', 0, '水果班', '/assets/images/portrait_placeholder.png', '2011-08-25', '2011-08-25', '93740362', 'address', 2, 1, 'social_id', '豆', 1, 1389631917435, 777888), (98, '李沛', '1_1389631917778', '13896', 0, '水果班', '/assets/images/portrait_placeholder.png', '2011-08-25', '2011-08-25', '93740362', 'address', 2, 1, 'social_id', '豆', 1, 1389631917778, 777888), (99, '李沛', '1_1389631919144', '13896', 0, '水果班', '/assets/images/portrait_placeholder.png', '2011-08-25', '2011-08-25', '93740362', 'address', 2, 1, 'social_id', '豆', 1, 1389631919144, 777888), (100, '李沛', '1_1389631919422', '13896', 0, '水果班', '/assets/images/portrait_placeholder.png', '2011-08-25', '2011-08-25', '93740362', 'address', 2, 1, 'social_id', '豆', 1, 1389631919422, 777888), (101, '李沛', '1_1389631919673', '13896', 0, '水果班', '/assets/images/portrait_placeholder.png', '2011-08-25', '2011-08-25', '93740362', 'address', 2, 1, 'social_id', '豆', 1, 1389631919673, 777888), (102, '李沛', '1_1389631925801', '13896', 0, '水果班', '/assets/images/portrait_placeholder.png', '2011-08-10', '2011-08-10', '93740362', 'address', 2, 1, 'social_id', '豆', 1, 1389631925801, 777888), (103, '李沛', '1_1389631926838', '13896', 0, '水果班', '/assets/images/portrait_placeholder.png', '2011-08-10', '2011-08-10', '93740362', 'address', 2, 1, 'social_id', '豆', 1, 1389631926838, 777888), (104, '李沛', '1_1389631927238', '13896', 0, '水果班', '/assets/images/portrait_placeholder.png', '2011-08-10', '2011-08-10', '93740362', 'address', 2, 1, 'social_id', '豆', 1, 1389631927238, 777888), (107, '李沛谦', '1_1389634766817', '13896', 0, '水果班', 'http://cocobabys.oss.aliyuncs.com/child_photo/93740362/1_1389634766817/1_1389634766817.jpg', '2011-08-25', '2011-08-25', '93740362', 'address', 2, 1, 'social_id', '豆瓣', 1, 1389634766817, 777888), (108, '马达', '1_1389638862033', '13896', 1, '水果班', '/assets/images/portrait_placeholder.png', '2009-01-01', '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '测啦', 1, 1389812883180, 777666), (139, '李沛谦', '1_1389696347532', '13896', 0, '水果班', 'http://cocobabys.oss.aliyuncs.com/child_photo/93740362/1_1389696347532/1_1389696347532.jpg', '2011-08-25', '2011-08-25', '93740362', 'address', 2, 1, 'social_id', '一一', 1, 1389795264463, 777888), (140, '李沛谦', '1_1389767960243', '13897', 0, '水果班', 'http://suoqin-test.u.qiniudn.com/FhFa41APn3S4YjlHdjeCW-Y-s8Xn', '2011-08-25', '2011-08-25', '93740362', 'address', 2, 1, 'social_id', '豆瓣', 1, 1389767960243, 777888), (141, '李沛谦', '1_1389767964266', '13897', 0, '水果班', 'http://suoqin-test.u.qiniudn.com/FhFa41APn3S4YjlHdjeCW-Y-s8Xn', '2011-08-25', '2011-08-25', '93740362', 'address', 2, 1, 'social_id', '豆瓣', 1, 1389767964266, 777888), (142, '李沛谦', '1_1389767969378', '13897', 0, '水果班', 'http://suoqin-test.u.qiniudn.com/FhFa41APn3S4YjlHdjeCW-Y-s8Xn', '2011-08-25', '2011-08-25', '93740362', 'address', 2, 1, 'social_id', '豆瓣', 1, 1389767969378, 777888), (143, '李沛谦', '1_1389767969868', '13897', 0, '水果班', 'http://suoqin-test.u.qiniudn.com/FhFa41APn3S4YjlHdjeCW-Y-s8Xn', '2011-08-25', '2011-08-25', '93740362', 'address', 2, 1, 'social_id', '豆瓣', 1, 1389767969868, 777888), (144, '小孩', '1_1389808175629', '13898', 1, '水果班', 'http://cocobabys.oss.aliyuncs.com/child_photo/93740362/1_1389808175629/1_1389808175629.jpg', '2006-01-01', '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '呵呵消费', 1, 1389811764956, 777888), (145, '西安第一人', '1_1389886107277', '13898', 1, '水果班', '/assets/images/portrait_placeholder.png', '2009-01-01', '2009-01-01', '93740562', 'address', 2, 1, 'social_id', '第二梦', 1, 1389886107277, 666777),
  (146, '索一禾', '1_1390238560925', '13902', 1, '水果班',
   'http://cocobabys.oss.aliyuncs.com/child_photo/93740362/1_1390238560925/1_1390238560925.jpg', '2009-07-24',
   '2009-07-24', '93740362', 'address', 2, 1, 'social_id', '', 1, 1390238560925, 777888),
  (147, '烦烦烦', '1_1390359054366', '13903', 1, '水果班',
   'http://cocobabys.oss.aliyuncs.com/child_photo/93740362/1_1390359054366/1_1390359054366.jpg', '2009-01-01',
   '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '烦烦烦', 1, 1390359054366, 777999),
  (148, '小郭总', '1_1391836223533', '13918', 0, '水果班',
   'http://cocobabys.oss.aliyuncs.com/child_photo/93740362/1_1391836223533/1_1391836223533.jpg', '2009-01-01',
   '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '郭政哲', 1, 1391836223533, 777888),
  (150, '我去玩', '1_1392191766937', '13921', 1, '水果班', '/assets/images/portrait_placeholder.png', '2009-01-01',
   '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '请问12', 1, 1392191766937, 777999),
  (156, '是电', '1_1393038181636', '13930', 1, '水果班', '/assets/images/portrait_placeholder.png', '2009-01-01',
   '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '个人股', 1, 1393082335703, 777888),
  (157, '朱小兵', '1_1393168619323', '13931', 1, '水果班', '/assets/images/portrait_placeholder.png', '2009-01-01',
   '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '打包', 1, 1393168619323, 777888),
  (158, '公告的', '1_1393257448133', '13932', 1, '水果班', '/assets/images/portrait_placeholder.png', '2009-01-01',
   '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '道德规范', 1, 1393257448133, 777888),
  (159, '袋鼠小朋友', '1_1393768956259', '13937', 1, '水果班', 'http://suoqin-test.u.qiniudn.com/FiEP4Ht1ghs22hgsE4f3HuwIQORG',
   '2012-01-01', '2009-01-01', '93740362', 'address', 2, 1, 'social_id', '瓜瓜', 1, 1393768956259, 777888);
/*!40000 ALTER TABLE `childinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `classinfo`
--

DROP TABLE IF EXISTS `classinfo`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `classinfo` (
  `uid`        INT(11)          NOT NULL AUTO_INCREMENT,
  `school_id`  VARCHAR(20)
               COLLATE utf8_bin NOT NULL,
  `class_id`   INT(11)          NOT NULL,
  `class_name` VARCHAR(40)
               COLLATE utf8_bin NOT NULL,
  `update_at`  BIGINT(20)       NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =9
  DEFAULT CHARSET =utf8
  COLLATE =utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classinfo`
--

LOCK TABLES `classinfo` WRITE;
/*!40000 ALTER TABLE `classinfo` DISABLE KEYS */;
INSERT INTO `classinfo`
VALUES (2, '93740362', 777888, '苹果班', 0), (3, '93740362', 777999, '香蕉班', 0), (4, '93740362', 777666, '梨儿班', 0),
  (5, '93740562', 666777, '圆圆班', 0), (7, '93740562', 666778, '点点班', 0), (8, '93740562', 666779, '小树班', 0);
/*!40000 ALTER TABLE `classinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `conversation`
--

DROP TABLE IF EXISTS `conversation`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `conversation` (
  `uid`       INT(11)          NOT NULL AUTO_INCREMENT,
  `school_id` VARCHAR(20)
              COLLATE utf8_bin NOT NULL,
  `phone`     VARCHAR(16)
              COLLATE utf8_bin NOT NULL,
  `content`   TEXT
              COLLATE utf8_bin NOT NULL,
  `image`     TEXT
              COLLATE utf8_bin,
  `sender`    VARCHAR(20)
              COLLATE utf8_bin DEFAULT '''''',
  `timestamp` BIGINT(20)       NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =45
  DEFAULT CHARSET =utf8
  COLLATE =utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `conversation`
--

LOCK TABLES `conversation` WRITE;
/*!40000 ALTER TABLE `conversation` DISABLE KEYS */;
INSERT INTO `conversation` VALUES
  (3, '93740362', '13408654680', '家长你好。', 'http://suoqin-test.u.qiniudn.com/Fget0Tx492DJofAy-ZeUg1SANJ4X', '老师A',
   12313313123), (5, '93740362', '13402815317', '请问在吗', '', '老师', 1393001678336), (10, '93740362', '13408654680', '8888', '', '', 1393158430705), (11, '93740362', '13408654680', '999', '', '', 1393158568411), (12, '93740362', '13408654680', '000', '', '', 1393158644435), (13, '93740362', '13408654680', '饿得', '', '', 1393158908732), (14, '93740362', '13408654680', '大大', '', '', 1393166506317), (15, '93740362', '13408654680', '哈哈哈', '', '', 1393166524532), (16, '93740362', '13408654680', 'Sdd', '', '', 1393242991180), (17, '93740362', '13408654680', 'Fdd', '', '', 1393243446511), (18, '93740362', '13408654680', '222', '', '', 1393249764643), (19, '93740362', '13408654680', '', '', '', 1393249771515), (20, '93740362', '13408654680', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/13408654680/1393250202075.jpg', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/13408654680/1393250202075.jpg', '', 1393250203800), (21, '93740362', '13408654680', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/13408654680/1393250239138.jpg', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/13408654680/1393250239138.jpg', '', 1393250239656), (22, '93740362', '13408654680', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/13408654680/1393254452309.jpg', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/13408654680/1393254452309.jpg', '', 1393254454771), (23, '93740362', '13408654680', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/13408654680/1393341364801.jpg', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/13408654680/1393341364801.jpg', '', 1393341366439), (24, '93740362', '13408654680', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/13408654680/1393341379029.jpg', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/13408654680/1393341379029.jpg', '', 1393341382008), (25, '93740362', '13408654680', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/13408654680/1393341394862.jpg', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/13408654680/1393341394862.jpg', '', 1393341396140), (26, '93740362', '13408654680', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/13408654680/1393341466995.jpg', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/13408654680/1393341466995.jpg', '', 1393341470471), (27, '93740362', '13408654680', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/13408654680/1393341521176.jpg', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/13408654680/1393341521176.jpg', '', 1393341521858), (28, '93740362', '13408654680', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/13408654680/1393341544331.jpg', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/13408654680/1393341544331.jpg', '', 1393341547150), (29, '93740362', '18980030903', '老师，宝宝吃饭吃得比较少，请老师规范一下，谢谢', '', '', 1393658169900), (30, '93740362', '18980030903', '老师，宝宝吃饭吃得比较少，请老师规范一下，谢谢', '', '', 1393658172775), (31, '93740362', '18980030903', '可口可乐了', '', '', 1393658186436), (32, '93740362', '18980030903', '爸爸去哪儿', '', '', 1393765919122), (33, '93740362', '18980030903', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/18980030903/1393766056287.jpg', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/18980030903/1393766056287.jpg', '', 1393766065151), (34, '93740362', '18980030903', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/18980030903/1393766083043.jpg', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/18980030903/1393766083043.jpg', '', 1393766090322), (35, '93740362', '18980030903', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/18980030903/1393766342431.jpg', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/18980030903/1393766342431.jpg', '', 1393766347711), (36, '93740362', '18980030903', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/18980030903/1393768395790.jpg', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/18980030903/1393768395790.jpg', '', 1393768400428), (37, '93740362', '18980030903', '宝宝今天早上有点不舒服，麻烦老师帮忙注意下\n', '', '', 1393817371166), (38, '93740362', '21211222232', 'asasdadad', '', '老师', 1393865162572),
  (39, '93740362', '21211222232', 'asdasd', '', '超人', 1393952749022),
  (40, '93740362', '18980030903', 'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/18980030903/1394031409650.jpg',
   'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/18980030903/1394031409650.jpg', '', 1394031413684),
  (41, '93740362', '21211222232', 'qdqwd', '', '超人', 1394040034779),
  (42, '93740362', '18980030903', '123456789', '', '', 1394085178104), (43, '93740362', '18980030903', '',
                                                                        'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/18980030903/1394113778480.jpg',
                                                                        '', 1394113779114),
  (44, '93740362', '18980030903', '',
   'http://cocobabys.oss.aliyuncs.com/chat_icon/93740362/18980030903/1394113800676.jpg', '', 1394113803019);
/*!40000 ALTER TABLE `conversation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cookbookinfo`
--

DROP TABLE IF EXISTS `cookbookinfo`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cookbookinfo` (
  `uid`           INT(11)          NOT NULL AUTO_INCREMENT,
  `school_id`     VARCHAR(20)
                  COLLATE utf8_bin NOT NULL,
  `cookbook_id`   INT(11)          NOT NULL,
  `mon_breakfast` VARCHAR(40)
                  COLLATE utf8_bin DEFAULT '',
  `tue_breakfast` VARCHAR(40)
                  COLLATE utf8_bin DEFAULT '',
  `wed_breakfast` VARCHAR(40)
                  COLLATE utf8_bin DEFAULT '',
  `thu_breakfast` VARCHAR(40)
                  COLLATE utf8_bin DEFAULT '',
  `fri_breakfast` VARCHAR(40)
                  COLLATE utf8_bin DEFAULT '',
  `mon_lunch`     VARCHAR(40)
                  COLLATE utf8_bin DEFAULT '',
  `tue_lunch`     VARCHAR(40)
                  COLLATE utf8_bin DEFAULT '',
  `wed_lunch`     VARCHAR(40)
                  COLLATE utf8_bin DEFAULT '',
  `thu_lunch`     VARCHAR(40)
                  COLLATE utf8_bin DEFAULT '',
  `fri_lunch`     VARCHAR(40)
                  COLLATE utf8_bin DEFAULT '',
  `mon_dinner`    VARCHAR(40)
                  COLLATE utf8_bin DEFAULT '',
  `tue_dinner`    VARCHAR(40)
                  COLLATE utf8_bin DEFAULT '',
  `wed_dinner`    VARCHAR(40)
                  COLLATE utf8_bin DEFAULT '',
  `thu_dinner`    VARCHAR(40)
                  COLLATE utf8_bin DEFAULT '',
  `fri_dinner`    VARCHAR(40)
                  COLLATE utf8_bin DEFAULT '',
  `mon_extra`     VARCHAR(40)
                  COLLATE utf8_bin DEFAULT '',
  `tue_extra`     VARCHAR(40)
                  COLLATE utf8_bin DEFAULT '',
  `wed_extra`     VARCHAR(40)
                  COLLATE utf8_bin DEFAULT '',
  `thu_extra`     VARCHAR(40)
                  COLLATE utf8_bin DEFAULT '',
  `fri_extra`     VARCHAR(40)
                  COLLATE utf8_bin DEFAULT '',
  `extra_tip`     TEXT
                  COLLATE utf8_bin NOT NULL,
  `status`        INT(11) DEFAULT '1',
  `timestamp`     BIGINT(20)       NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =60
  DEFAULT CHARSET =utf8
  COLLATE =utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cookbookinfo`
--

LOCK TABLES `cookbookinfo` WRITE;
/*!40000 ALTER TABLE `cookbookinfo` DISABLE KEYS */;
INSERT INTO `cookbookinfo` VALUES
  (2, '93740362', 123, '荷包蛋，鹌鹑蛋，牛肉饼', '啤酒，鹌鹑蛋，牛肉饼', '咖啡，鹌鹑蛋，牛肉饼', '臭豆腐，鹌鹑蛋，牛肉饼', '二锅头，鹌鹑蛋，牛肉饼', '兰州拉面，京酱肉丝',
   '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '土豆西红柿，野菜炖蘑菇', '啤酒，鹌鹑蛋，牛肉饼', '兰州拉面，京酱肉丝', '土豆西红柿，野菜炖蘑菇',
   '土豆西红柿，野菜炖蘑菇', '兰州烧饼', '兰州烧饼', '兰州烧饼', '兰州烧饼', '兰州烧饼', '陷好皮薄', 0,
   0), (3, '93740362', 121, '三聚氰胺', '二噁英', '炭疽', '霉菌', '要你命三千', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '土豆西红柿，野菜炖蘑菇', '啤酒，鹌鹑蛋，牛肉饼', '兰州拉面，京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '兰州烧饼', '兰州烧饼', '兰州烧饼', '兰州烧饼', '兰州烧饼', '陷好皮薄', 0, 0), (4, '93740362', 124, '没吃的', '啤酒，鹌鹑蛋，牛肉饼', '咖啡，鹌鹑蛋，牛肉饼', '臭豆腐，鹌鹑蛋，牛肉饼', '二锅头，鹌鹑蛋，牛肉饼', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '土豆西红柿，野菜炖蘑菇', '啤酒，鹌鹑蛋，牛肉饼', '兰州拉面，京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '兰州烧饼', '兰州烧饼', '兰州烧饼', '兰州烧饼', '兰州烧饼', '陷好皮薄', 0, 1389372267536), (5, '93740362', 125, '狗不理包子', '啤酒，鹌鹑蛋，牛肉饼', '咖啡，鹌鹑蛋，牛肉饼', '臭豆腐，鹌鹑蛋，牛肉饼', '二锅头，鹌鹑蛋，牛肉饼', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '土豆西红柿，野菜炖蘑菇', '啤酒，鹌鹑蛋，牛肉饼', '兰州拉面，京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '兰州烧饼', '兰州烧饼', '兰州烧饼', '兰州烧饼', '兰州烧饼', '陷好皮薄', 0, 1389409629453), (6, '93740362', 126, '水电费的冯绍峰的', '范文芳', '咖啡，鹌鹑蛋，牛肉饼', '臭豆腐，鹌鹑蛋，牛肉饼', '二锅头，鹌鹑蛋，牛肉饼', '发生的范围', '二分钱文峰', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '分为服务', '万二分位', '兰州拉面，京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '而无法为二人分', '俄方认为', '兰州烧饼', '兰州烧饼', '兰州烧饼', '陷好皮薄', 0, 1389593547875), (7, '93740362', 127, '水电费的冯绍峰的', '范文芳', '咖啡，鹌鹑蛋，牛肉饼', '臭豆腐，鹌鹑蛋，牛肉饼', '二锅头，鹌鹑蛋，牛肉饼', '发生的范围', '二分钱文峰', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '分为服务', '万二分位', '兰州拉面，京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '而无法为二人分', '俄方认为', '兰州烧饼', '兰州烧饼', '兰州烧饼', '陷好皮薄', 0, 1389593904151), (8, '93740362', 128, '热干面、豆浆  热干面65克 、牛奶200毫升', '范文芳', '咖啡，鹌鹑蛋，牛肉饼', '臭豆腐，鹌鹑蛋，牛肉饼', '二锅头，鹌鹑蛋，牛肉饼', '发生的范围', '二分钱文峰', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '分为服务', '万二分位', '兰州拉面，京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '而无法为二人分', '俄方认为', '兰州烧饼', '兰州烧饼', '兰州烧饼', '陷好皮薄', 0, 1389593918355), (9, '93740362', 129, '热干面、豆浆  热干面65克 、牛奶200毫升', '范文芳', '咖啡，鹌鹑蛋，牛肉饼', '臭豆腐，鹌鹑蛋，牛肉饼', '二锅头，鹌鹑蛋，牛肉饼', '发生的范围', '二分钱文峰', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '分为服务', '万二分位', '兰州拉面，京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '而无法为二人分', '俄方认为', '兰州烧饼', '兰州烧饼', '兰州烧饼', '陷好皮薄', 0, 1389593941296), (10, '93740362', 130, '热干面、豆浆（热干面65克 、牛奶200毫升）', '范文芳', '咖啡，鹌鹑蛋，牛肉饼', '臭豆腐，鹌鹑蛋，牛肉饼', '二锅头，鹌鹑蛋，牛肉饼', '发生的范围', '二分钱文峰', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '分为服务', '万二分位', '兰州拉面，京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '而无法为二人分', '俄方认为', '兰州烧饼', '兰州烧饼', '兰州烧饼', '陷好皮薄', 0, 1389593948576), (11, '93740362', 131, '热干面、豆浆（热干面65克 、牛奶200毫升）', '范文芳', '咖啡，鹌鹑蛋，牛肉饼', '臭豆腐，鹌鹑蛋，牛肉饼', '二锅头，鹌鹑蛋，牛肉饼', '发生的范围大师', '二分钱文峰', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '分为服务', '万二分位', '兰州拉面，京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '而无法为二人分', '俄方认为', '兰州烧饼', '兰州烧饼', '兰州烧饼', '陷好皮薄', 0, 1389595596388), (12, '93740362', 132, '热干面、豆浆（热干面65克 、牛奶200毫升）', '范文芳', '咖啡，鹌鹑蛋，牛肉饼', '臭豆腐，鹌鹑蛋，牛肉饼', '二锅头，鹌鹑蛋，牛肉饼', '发生的范围大师', '二分钱文峰', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '分为服务', '万二分位', '兰州拉面，京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '而无法为二人分', '俄方认为', '兰州烧饼', '兰州烧饼', '兰州烧饼', '陷好皮薄', 0, 1389595603301), (13, '93740362', 131, '热干面、豆浆（热干面65克 、牛奶200毫升）', '范文芳', '咖啡，鹌鹑蛋，牛肉饼', '臭豆腐，鹌鹑蛋，牛肉饼', '二锅头，鹌鹑蛋，牛肉饼', '发生的范围', '二分钱文峰', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '分为服务', '万二分位', '兰州拉面，京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '而无法为二人分', '俄方认为', '兰州烧饼', '兰州烧饼', '兰州烧饼', '陷好皮薄', 0, 1389596514262), (14, '93740362', 132, '热干面、豆浆（热干面65克 、牛奶200毫升）', '范文芳', '咖啡，鹌鹑蛋，牛肉饼', '臭豆腐，鹌鹑蛋，牛肉饼', '二锅头，鹌鹑蛋，牛肉饼', '发生的范围', '二分钱文峰', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '分为服务', '万二分位', '兰州拉面，京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '而无法为二人分', '俄方认为', '兰州烧饼', '兰州烧饼', '兰州烧饼', '陷好皮薄', 0, 1389596520542), (15, '93740362', 133, '热干面、豆浆（热干面65克 、牛奶200毫升）', '范文芳', '咖啡，鹌鹑蛋，牛肉饼', '臭豆腐，鹌鹑蛋，牛肉饼', '二锅头，鹌鹑蛋，牛肉饼', '发生的范围', '二分钱文峰', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '分为服务', '万二分位', '兰州拉面，京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '而无法为二人分', '俄方认为', '兰州烧饼', '兰州烧饼', '兰州烧饼', '陷好皮薄', 0, 1389596522299), (16, '93740362', 134, '就就就就就就就就就就就就就就就就就就就就就就就就就就就就就就就就就就就就就就就就', '范文芳热干面、豆', '咖啡，鹌鹑蛋，牛肉饼', '臭豆腐，鹌鹑蛋，牛肉饼', '二锅头，鹌鹑蛋，牛肉饼', '发生的范围', '二分钱文峰', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '分为服务', '万二分位', '兰州拉面，京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '而无法为二人分', '俄方认为', '兰州烧饼', '兰州烧饼', '兰州烧饼', '陷好皮薄', 0, 1389596553827), (17, '93740362', 135, '就就就就就就就就就就就就就就就就就就就就就就就就就就就就就就就就就就就就就就就就', '范文芳热干面、豆', '咖啡，鹌鹑蛋，牛肉饼', '臭豆腐，鹌鹑蛋，牛肉饼', '二锅头，鹌鹑蛋，牛肉饼', '发生的范围', '二分钱文峰', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '分为服务', '万二分位', '兰州拉面，京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '而无法为二人分', '俄方认为', '兰州烧饼', '兰州烧饼', '兰州烧饼', '陷好皮薄', 0, 1389596584725), (18, '93740362', 136, '没有早饭', '范文芳热干面、豆', '咖啡，鹌鹑蛋，牛肉饼', '臭豆腐，鹌鹑蛋，牛肉饼', '二锅头，鹌鹑蛋，牛肉饼', '发生的范围', '二分钱文峰', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '分为服务', '万二分位', '兰州拉面，京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '而无法为二人分', '俄方认为', '兰州烧饼', '兰州烧饼', '兰州烧饼', '陷好皮薄', 0, 1389638879802), (19, '93740362', 137, '没有早饭，要不要鸡蛋', '范文芳热干面、豆', '咖啡，鹌鹑蛋，牛肉饼', '臭豆腐，鹌鹑蛋，牛肉饼', '二锅头，鹌鹑蛋，牛肉饼', '发生的范围', '二分钱文峰', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '兰州拉面，京酱肉丝', '分为服务', '万二分位', '兰州拉面，京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '而无法为二人分', '俄方认为', '兰州烧饼', '兰州烧饼', '兰州烧饼', '陷好皮薄', 0, 1389808949423), (20, '93740362', 138, '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼', '烧饼', '兰州烧饼', '烧饼', 0, 1390294625677), (21, '93740362', 139, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼', '烧饼', '兰州烧饼', '烧饼', 0, 1391845617973), (22, '93740362', 140, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼', '烧饼', '兰州烧饼', '烧饼', 0, 1391845627409), (23, '93740362', 141, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼', '烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845673909), (24, '93740362', 142, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼', '烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845828245), (25, '93740362', 143, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼', '烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845835069), (26, '93740362', 144, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼', '烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845838906), (27, '93740362', 145, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼', '烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845843614), (28, '93740362', 146, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼', '烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845846509), (29, '93740362', 147, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845853725), (30, '93740362', 148, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845857125), (31, '93740362', 149, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845861754), (32, '93740362', 150, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845865562), (33, '93740362', 151, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845869967), (34, '93740362', 152, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845872694), (35, '93740362', 153, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845876067), (36, '93740362', 154, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845878468), (37, '93740362', 155, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845881837), (38, '93740362', 156, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845883463), (39, '93740362', 157, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845886350), (40, '93740362', 158, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845886704), (41, '93740362', 159, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845889932), (42, '93740362', 160, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845890285), (43, '93740362', 161, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845894040), (44, '93740362', 162, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845894425), (45, '93740362', 163, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845898169), (46, '93740362', 164, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845898656), (47, '93740362', 165, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '小鸡烧蘑菇，猪肉炖粉条烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '京酱肉丝烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845901888), (48, '93740362', 166, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '小鸡烧蘑菇，猪肉炖粉条烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '京酱肉丝烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845902232), (49, '93740362', 167, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '小鸡烧蘑菇，猪肉炖粉条烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '京酱肉丝烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '包子', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845904552), (50, '93740362', 168, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '小鸡烧蘑菇，猪肉炖粉条烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '京酱肉丝烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '包子', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845904907), (51, '93740362', 169, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '小鸡烧蘑菇，猪肉炖粉条烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '京酱肉丝烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '包子烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845906844), (52, '93740362', 170, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '小鸡烧蘑菇，猪肉炖粉条烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '京酱肉丝烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '包子烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', 0, 1391845907115),
  (53, '93740362', 171, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
   '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
   '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧',
   '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝',
   '小鸡烧蘑菇，猪肉炖粉条烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '小鸡烧蘑菇，猪肉炖粉条烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧',
   '京酱肉丝烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇',
   '包子烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '包子烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
   '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼',
   '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
   0, 1391845999142),
  (54, '93740362', 172, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
   '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
   '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧',
   '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝',
   '小鸡烧蘑菇，猪肉炖粉条烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '小鸡烧蘑菇，猪肉炖粉条烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧',
   '京酱肉丝烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇',
   '包子烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '包子烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
   '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼',
   '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
   0, 1391845999665),
  (55, '93740362', 173, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
   '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
   '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧',
   '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝',
   '小鸡烧蘑菇，猪肉炖粉条烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '小鸡烧蘑菇，猪肉炖粉条烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧',
   '京酱肉丝烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇',
   '包子烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '包子烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
   '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼',
   '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
   0, 1391846007045),
  (56, '93740362', 174, '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
   '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
   '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧',
   '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝',
   '小鸡烧蘑菇，猪肉炖粉条烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '小鸡烧蘑菇，猪肉炖粉条烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧',
   '京酱肉丝烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇',
   '包子烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '包子烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
   '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼',
   '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
   0, 1391846008096), (57, '93740362', 175, '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
                       '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
                       '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面', '番茄鸡蛋面',
                       '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧',
                       '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
                       '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子', '包子',
                       '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼',
                       '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
                       0, 1392216296216), (58, '93740362', 176, '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
                                           '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
                                           '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
                                           '牛奶、鸡蛋、蛋糕烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '番茄鸡蛋面', '番茄鸡蛋面',
                                           '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧',
                                           '番茄鸡蛋面烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条',
                                           '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
                                           '土豆西红柿，野菜炖蘑菇烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧', '土豆西红柿，野菜炖蘑菇', '包子', '包子',
                                           '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
                                           '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼', '兰州烧饼',
                                           '烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼烧饼',
                                           0, 1392216725961),
  (59, '93740362', 177, '牛奶、鸡蛋、蛋糕', '牛奶、鸡蛋', '牛奶、鸡蛋、蛋', '牛奶、鸡蛋、', '牛奶、鸡蛋、蛋', '番茄鸡蛋面', '番茄鸡蛋面', '番茄鸡蛋面烧饼', '番茄鸡蛋面',
   '兰州拉面，京酱肉丝', '小鸡烧蘑菇，猪肉炖粉条', '小鸡烧蘑菇，猪肉炖粉条', '京酱肉丝', '土豆西红柿，野菜炖蘑菇', '土豆西红柿，野菜炖蘑菇', '包子', '包子', '烧饼', '烧饼烧饼烧饼', '兰州烧饼',
   '的', 1, 1392216760012);
/*!40000 ALTER TABLE `cookbookinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dailylog`
--

DROP TABLE IF EXISTS `dailylog`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dailylog` (
  `uid`         INT(11)          NOT NULL AUTO_INCREMENT,
  `school_id`   VARCHAR(20)
                COLLATE utf8_bin NOT NULL,
  `child_id`    VARCHAR(40)
                COLLATE utf8_bin NOT NULL,
  `parent_name` VARCHAR(20)
                COLLATE utf8_bin NOT NULL DEFAULT '',
  `pushid`      VARCHAR(20)
                COLLATE utf8_bin NOT NULL,
  `record_url`  TEXT
                COLLATE utf8_bin,
  `card_no`     VARCHAR(20)
                COLLATE utf8_bin NOT NULL,
  `card_type`   INT(11) DEFAULT '0',
  `notice_type` INT(11) DEFAULT '0',
  `check_at`    BIGINT(20)       NOT NULL DEFAULT '0',
  `device`      INT(11)          NOT NULL DEFAULT '3',
  PRIMARY KEY (`uid`),
  KEY `check_at` (`check_at`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =90
  DEFAULT CHARSET =utf8
  COLLATE =utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dailylog`
--

LOCK TABLES `dailylog` WRITE;
/*!40000 ALTER TABLE `dailylog` DISABLE KEYS */;
INSERT INTO `dailylog` VALUES (2, '93740362', '1_1390238560925', '', '1048149352597220995',
                               'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '1212111212', 0, 2,
                               1390899235260,
                               3), (3, '93740362', '1_93740362_456', '', '715674199931568765', 'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '0009880539', 0, 2, 1391827475787, 3), (4, '93740362', '1_93740362_456', '', '715674199931568765', 'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '0009880539', 0, 2, 1391827478103, 3), (5, '93740362', '1_93740362_374', '', '703964538983633996', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 1, 1391830957312, 3), (6, '93740362', '1_93740362_374', '', '703964538983633996', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 1, 1391830980938, 3), (7, '93740362', '1_93740362_374', '', '703964538983633996', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 1, 1391831021188, 3), (8, '93740362', '1_93740362_374', '', '703964538983633996', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 1, 1391831036328, 3), (9, '93740362', '1_93740362_374', '', '703964538983633996', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 1, 1391831040828, 3), (10, '93740362', '1_93740362_374', '', '703964538983633996', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 1, 1391831043875, 3), (11, '93740362', '1_93740362_374', '', '703964538983633996', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 1, 1391831072344, 3), (12, '93740362', '1_93740362_374', '', '703964538983633996', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 1, 1391831179531, 3), (13, '93740362', '1_93740362_374', '', '703964538983633996', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 1, 1391831226500, 3), (14, '93740362', '1_93740362_374', '', '703964538983633996', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 1, 1391831692438, 3), (15, '93740362', '1_93740362_374', '', '703964538983633996', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 1, 1391831727359, 3), (16, '93740362', '1_93740362_374', '', '703964538983633996', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 1, 1391831738859, 3), (17, '93740362', '1_93740362_374', '', '703964538983633996', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 1, 1391831760016, 3), (18, '93740362', '1_93740362_374', '', '703964538983633996', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 1, 1391831764375, 3), (19, '93740362', '1_93740362_456', '', '703964538983633996', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0009880539.jpg', '0009880539', 0, 1, 1391843483125, 3), (20, '93740362', '1_93740362_456', '', '703964538983633996', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0009880539.jpg', '0009880539', 0, 1, 1391843507156, 3), (21, '93740362', '1_93740362_456', '', '703964538983633996', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0009880539.jpg', '0009880539', 0, 1, 1391843701812, 3), (22, '93740362', '1_1389696347532', '', '615635431466128843', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/1231231212.jpg', '1231231212', 0, 1, 1391855397444, 3), (23, '93740362', '1_1389808175629', '', '615635431466128843', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/1231231110.jpg', '1231231110', 0, 1, 1391855422429, 3), (24, '93740362', '1_1389808175629', '', '615635431466128843', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/1231231110.jpg', '1231231110', 0, 0, 1391855562332, 3), (25, '93740362', '1_1389696347532', '', '615635431466128843', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/1231231212.jpg', '1231231212', 0, 0, 1391855590740, 3), (26, '93740362', '1_93740362_374', '', '925387477040814447', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 1, 1391924636922, 3), (27, '93740362', '1_93740362_374', '', '925387477040814447', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 1, 1391924657031, 3), (28, '93740362', '1_93740362_374', '', '925387477040814447', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 1, 1391926857375, 3), (29, '93740362', '1_93740362_374', '', '925387477040814447', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 1, 1391935395703, 3), (30, '93740362', '1_93740362_374', '', '925387477040814447', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 1, 1391935505469, 3), (31, '93740362', '1_93740362_374', '', '925387477040814447', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 0, 1391935694375, 3), (32, '93740362', '1_93740362_374', '', '925387477040814447', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 0, 1391939162828, 3), (33, '93740362', '1_93740362_374', '', '925387477040814447', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 1, 1391939171719, 3), (34, '93740362', '1_93740362_374', '', '925387477040814447', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 0, 1391939187031, 3), (35, '93740362', '1_93740362_374', '', '925387477040814447', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 1, 1391940308516, 3), (36, '93740362', '1_93740362_374', '', '925387477040814447', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 0, 1391951319828, 3), (37, '93740362', '1_93740362_374', '', '925387477040814447', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 0, 1391951697984, 3), (38, '93740362', '1_1389696347532', '', '615635431466128843', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/1231231212.jpg', '1231231212', 0, 1, 1391960418020, 3), (39, '93740362', '1_1390359054366', '', '703964538983633996', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0004087891.jpg', '0004087891', 0, 1, 1392004199906, 3), (40, '93740362', '1_1390359054366', '', '703964538983633996', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0004087891.jpg', '0004087891', 0, 1, 1392004236719, 3), (41, '93740362', '1_1390359054366', '', '703964538983633996', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0004087891.jpg', '0004087891', 0, 1, 1392009658969, 3), (42, '93740362', '1_93740362_456', '', '703964538983633996', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0009880539.jpg', '0009880539', 0, 1, 1392009724375, 3), (43, '93740362', '1_93740362_456', '', '715674199931568765', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0009880539/1392010286000.jpg', '0009880539', 0, 0, 1392010286000, 3), (44, '93740362', '1_1390359054366', '', '703964538983633996', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0004087891/1392010378000.jpg', '0004087891', 0, 0, 1392010378000, 3), (45, '93740362', '1_93740362_456', '', '715674199931568765', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0009880539/1392010407000.jpg', '0009880539', 0, 1, 1392010407000, 3), (46, '93740362', '1_1390359054366', '', '703964538983633996', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0004087891/1392010440000.jpg', '0004087891', 0, 1, 1392010440000, 3), (47, '93740362', '1_93740362_456', '', '715674199931568765', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0009880539/1392010870000.jpg', '0009880539', 0, 0, 1392010870000, 3), (48, '93740362', '1_93740362_456', '', '715674199931568765', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0009880539/1392011903000.jpg', '0009880539', 0, 1, 1392011903000, 3), (49, '93740362', '1_93740362_456', '', '803532607811824048', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0009880539.jpg', '0009880539', 0, 1, 1392188064811, 3), (50, '93740362', '1_93740362_456', '', '803532607811824048', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0009880539.jpg', '0009880539', 0, 0, 1392188221320, 3), (51, '93740362', '1_93740362_456', '', '803532607811824048', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0009880539.jpg', '0009880539', 0, 1, 1392188311403, 3), (52, '93740362', '1_93740362_456', '', '803532607811824048', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0009880539.jpg', '0009880539', 0, 1, 1392188818078, 3), (53, '93740362', '1_93740362_456', '', '803532607811824048', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0009880539.jpg', '0009880539', 0, 0, 1392188865658, 3), (54, '93740362', '1_93740362_456', '', '803532607811824048', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0009880539.jpg', '0009880539', 0, 0, 1392188878669, 3), (55, '93740362', '1_93740362_456', '', '803532607811824048', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0009880539.jpg', '0009880539', 0, 0, 1392191984151, 3), (56, '93740362', '1_93740362_374', '', '963386802751977894', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 1, 1392307099422, 3), (57, '93740362', '1_93740362_374', '', '963386802751977894', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/0001234569.jpg', '0001234569', 0, 1, 1392452136969, 3), (58, '93740362', '1_1391836223533', '', '1100187991584280825', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/1234598765.jpg', '1234598765', 0, 1, 1392615642932, 3), (59, '93740362', '1_1391836223533', '', '1100187991584280825', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/1234598765.jpg', '1234598765', 0, 0, 1392616138718, 3), (60, '93740362', '1_1390238560925', '', '1048149352597220995', 'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '1212111212', 0, 2, 1392731366192, 3), (61, '93740362', '1_1390238560925', '', '1048149352597220995', 'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '1212111212', 0, 2, 1392731512647, 3), (62, '93740362', '1_1390238560925', '', '1048149352597220995', 'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '1212111212', 0, 2, 1392733100078, 3), (63, '93740362', '1_1390238560925', '', '1048149352597220995', 'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '1212111212', 0, 2, 1392733387646, 3), (64, '93740362', '1_93740362_374', '林玄', '815206836867002199', 'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '1231212312', 0, 0, 1393658860169, 3), (65, '93740362', '1_93740362_374', '林玄', '815206836867002199', 'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '1231212312', 0, 1, 1393658878841, 3), (66, '93740362', '1_93740362_374', '林玄', '815206836867002199', 'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '1231212312', 0, 0, 1393766525398, 3), (67, '93740362', '1_93740362_374', '林玄', '815206836867002199', 'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '1231212312', 0, 1, 1393766528629, 3), (68, '93740362', '1_93740362_374', '林玄', '815206836867002199', 'http://cocobabys.oss.aliyuncs.com/swap_photo/93740362/1231212312.jpg', '1231212312', 0, 1, 1393766581979, 3), (69, '93740362', '1_93740362_374', '林玄', '815206836867002199', 'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '1231212312', 0, 0, 1393766769865, 3), (70, '93740362', '1_93740362_374', '林玄', '815206836867002199', 'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '1231212312', 0, 1, 1393766772899, 3), (71, '93740362', '1_1389634766817', '李毅', '1010547872381347384', 'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '6655656565', 0, 0, 1394010638586, 3), (72, '93740362', '1_1389634766817', '李毅', '1010547872381347384', 'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '6655656565', 0, 1, 1394010648040, 3), (73, '93740362', '1_1389634766817', '李毅', '1010547872381347384', 'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '6655656565', 0, 0, 1394011422015, 3), (74, '93740362', '1_1389634766817', '李毅', '1010547872381347384', 'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '6655656565', 0, 0, 1394011538586, 3), (75, '93740362', '1_1389634766817', '李毅', '1010547872381347384', 'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '6655656565', 0, 1, 1394011542042, 3), (76, '93740362', '1_1390359054366', '鑫哥', '1010547872381347384', 'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '3333222323', 0, 0, 1394011953550, 3), (77, '93740362', '1_1393768956259', '鑫哥', '1071733574818091771', 'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '2133123232', 0, 1, 1394011959044, 3), (78, '93740362', '1_1393768956259', '鑫哥', '1010547872381347384', 'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '2133123232', 0, 1, 1394011959044, 3), (79, '93740362', '1_1390359054366', '鑫哥', '1010547872381347384', 'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '3333222323', 0, 0, 1394012213588, 3), (80, '93740362', '1_1390359054366', '鑫哥', '1010547872381347384', 'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '3333222323', 0, 1, 1394012216451, 3), (81, '93740362', '1_1393768956259', '鑫哥', '1071733574818091771', 'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '2133123232', 0, 0, 1394012335912, 3), (82, '93740362', '1_1393768956259', '鑫哥', '1010547872381347384', 'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '2133123232', 0, 0, 1394012335912, 3),
  (83, '93740362', '1_1390359054366', '鑫哥', '638246371385326070',
   'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '3333222323', 0, 0, 1394082640769, 4),
  (84, '93740362', '1_1390359054366', '鑫哥', '638246371385326070',
   'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '3333222323', 0, 1, 1394082678625, 4),
  (85, '93740362', '1_1390359054366', '鑫哥', '638246371385326070',
   'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '3333222323', 0, 0, 1394084482754, 4),
  (86, '93740362', '1_1393768956259', '鑫哥', '963386802751977894',
   'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '2133123232', 0, 0, 1394084526848, 3),
  (87, '93740362', '1_1393768956259', '鑫哥', '638246371385326070',
   'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '2133123232', 0, 0, 1394084526848, 4),
  (88, '93740362', '1_1393768956259', '鑫哥', '808474149922701546',
   'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '2133123232', 0, 0, 1394085601369, 3),
  (89, '93740362', '1_1393768956259', '鑫哥', '638246371385326070',
   'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL', '2133123232', 0, 0, 1394085601369, 4);
/*!40000 ALTER TABLE `dailylog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employeeinfo`
--

DROP TABLE IF EXISTS `employeeinfo`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `employeeinfo` (
  `uid`            INT(11)          NOT NULL AUTO_INCREMENT,
  `name`           VARCHAR(20)
                   COLLATE utf8_bin NOT NULL,
  `employee_id`    VARCHAR(40)
                   COLLATE utf8_bin NOT NULL,
  `phone`          VARCHAR(16)
                   COLLATE utf8_bin NOT NULL,
  `gender`         TINYINT(4)       NOT NULL DEFAULT '2',
  `workgroup`      VARCHAR(40)
                   COLLATE utf8_bin NOT NULL DEFAULT '',
  `workduty`       VARCHAR(20)
                   COLLATE utf8_bin NOT NULL DEFAULT '',
  `picurl`         VARCHAR(128)
                   COLLATE utf8_bin DEFAULT '',
  `birthday`       DATE             NOT NULL DEFAULT '1800-01-01',
  `school_id`      VARCHAR(20)
                   COLLATE utf8_bin NOT NULL,
  `login_password` VARCHAR(32)
                   COLLATE utf8_bin NOT NULL DEFAULT '',
  `login_name`     VARCHAR(32)
                   COLLATE utf8_bin NOT NULL DEFAULT '',
  `status`         TINYINT(4)       NOT NULL DEFAULT '1',
  `update_at`      BIGINT(20)       NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `phone` (`phone`),
  UNIQUE KEY `employee_id` (`employee_id`),
  KEY `birthday` (`birthday`),
  KEY `school_id` (`school_id`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =8
  DEFAULT CHARSET =utf8
  COLLATE =utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employeeinfo`
--

LOCK TABLES `employeeinfo` WRITE;
/*!40000 ALTER TABLE `employeeinfo` DISABLE KEYS */;
INSERT INTO `employeeinfo` VALUES
  (1, '王豫', '3_93740362_1122', '13258249821', 0, '教师组', '教师', '', '1986-06-04', '93740362',
   '3FDE6BB0541387E4EBDADF7C2FF31123', 'e0001', 1, 0),
  (2, '何忍', '3_93740362_3344', '13708089040', 0, '教师组', '教师', '', '1987-06-04', '93740362',
   '3FDE6BB0541387E4EBDADF7C2FF31123', 'e0002', 1, 0),
  (3, '富贵', '3_93740362_9977', '13060003702', 1, '保安组', '员工', '', '1982-06-04', '93740362',
   '3FDE6BB0541387E4EBDADF7C2FF31123', 'e0003', 1, 0),
  (5, '蜘蛛侠', '3_93740362_9971', '13060003722', 1, '保安组', '员工', '', '1982-06-04', '93740362',
   '5EBE2294ECD0E0F08EAB7690D2A6EE69', 'admin', 1, 0),
  (6, '超人', '3_93740362_9972', '13060003723', 1, '保安组', '员工', '', '1982-06-04', '93740362',
   'CDF3C60380FE6094BBF7F979243B280B', 'operator', 1, 0),
  (7, '西安老师', '3_93740362_9978', '13060003703', 1, '保安组', '员工', '', '1982-06-04', '93740562',
   '3FDE6BB0541387E4EBDADF7C2FF31123', 'e0004', 1, 0);
/*!40000 ALTER TABLE `employeeinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feedback`
--

DROP TABLE IF EXISTS `feedback`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `feedback` (
  `uid`       INT(11)          NOT NULL AUTO_INCREMENT,
  `phone`     VARCHAR(16)
              COLLATE utf8_bin NOT NULL,
  `content`   TEXT
              COLLATE utf8_bin,
  `comment`   TEXT
              COLLATE utf8_bin,
  `status`    TINYINT(4)       NOT NULL DEFAULT '1',
  `update_at` BIGINT(20)       NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =16
  DEFAULT CHARSET =utf8
  COLLATE =utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedback`
--

LOCK TABLES `feedback` WRITE;
/*!40000 ALTER TABLE `feedback` DISABLE KEYS */;
INSERT INTO `feedback` VALUES (2, '123123132123',
                               '科学幻想小说，简称科幻小说，主要描写想象的科学或技术对社会或个人的影响的虚构性文学作品。中文最早也有译作科学小说。虽然从科幻史的角度来看，暂时还没有一个能被所有研究者所公认的定义标准。一些可供参考的例子有：“科幻小说是描述科学或想象中的科学对人类影响的小说”；“科幻小说是描绘对象处于未知范畴中的小说”。以上定义虽然角度不同，但科幻小说的定义中总是反复出现一些词语，例如：“幻想”、“未来”、“科技”、“人类”、“变化”等。从这些关键词中可以看到，科幻小说所涉及的范畴总是与人类的好奇心、求知欲紧密相连。',
                               NULL, 1,
                               1390146513452), (3, '13408654680', '测试反馈\n\n\n\n\n', NULL, 1, 1390320672940), (4, '13408654680', '', NULL, 1, 1390321724680), (5, '13402815317', '我看看', NULL, 1, 1390354831006), (6, '13402815317', '这个好', NULL, 1, 1390484543926), (7, '13402815317', 'Though y \n\n', NULL, 1, 1390723481739), (8, '13408654680', '', NULL, 1, 1391531917177), (9, '18980030903', '神马', NULL, 1, 1391827995942), (10, '18980030903', '退lz图V5咯余V5咯你', NULL, 1, 1391829386630), (11, '18980030903', '提go余V5笨最基本', NULL, 1, 1391829398783), (12, '13408654680', '反馈', NULL, 1, 1391831122301),
  (13, '18611055373', '', NULL, 1, 1391837053562), (14, '18980030903', '我们俩大动干戈得到 的风格', NULL, 1, 1392189337831),
  (15, '18980030903', '你好！', NULL, 1, 1394085040719);
/*!40000 ALTER TABLE `feedback` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `news`
--

DROP TABLE IF EXISTS `news`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `news` (
  `uid`       INT(11)          NOT NULL AUTO_INCREMENT,
  `school_id` VARCHAR(20)
              COLLATE utf8_bin NOT NULL,
  `title`     VARCHAR(255)
              COLLATE utf8_bin NOT NULL,
  `content`   TEXT
              COLLATE utf8_bin,
  `update_at` BIGINT(20)       NOT NULL DEFAULT '0',
  `published` TINYINT(4)       NOT NULL DEFAULT '0',
  `status`    TINYINT(4)       NOT NULL DEFAULT '1',
  PRIMARY KEY (`uid`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =293
  DEFAULT CHARSET =utf8
  COLLATE =utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news`
--

LOCK TABLES `news` WRITE;
/*!40000 ALTER TABLE `news` DISABLE KEYS */;
INSERT INTO `news` VALUES (2, '93740362', '缴费通知', '请缴费100万！', 1387701374754, 1,
                           0), (3, '93740362', '停电通知', '学校明天(12月23号星期一)停电，放假一天，特此通知', 1387701423115, 1, 0), (4, '93740362', '通知3', '恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击', 1387728575353, 1, 0), (5, '937403621', '新通知', '新通知内容，点击进行编辑', 1387651053380, 1, 1), (6, '937403621', '新通知', '新通知内容，点击进行编辑', 1387653641092, 1, 1), (7, '937403621', '新通知', '新通知内容，点击进行编辑', 1387653646037, 1, 1), (8, '937403621', '新通知', '新通知内容，点击进行编辑', 1387653646987, 1, 1), (9, '937403621', '新通知', '新通知内容，点击进行编辑', 1387653647891, 1, 1), (10, '937403621', '新通知', '新通知内容，点击进行编辑', 1387653651152, 1, 1), (11, '93740362', '家长会通知', '明天下午2点开家长会，请各位家长准时出席，谢谢！\n\n\n\n                                                      XXX幼儿园', 1387701350840, 1, 0), (12, '93740362', '新通知', '新通知内容，点击进行编辑', 1387724407622, 1, 0), (13, '93740362', '新通知', '新通知内容，点击进行编辑', 1387731743427, 1, 0), (14, '93740362', '新通知', '新通知内容，点击进行编辑', 1387730690565, 1, 0), (15, '93740362', '新通知', '新通知内容，点击进行编辑', 1387730688665, 1, 0), (16, '93740362', '新通知', '新通知内容，点击进行编辑', 1387730689543, 1, 0), (17, '93740362', '新通知', '新通知内容，点击进行编辑', 1387730575174, 1, 0), (18, '93740362', '新通知', '新通知内容，点击进行编辑', 1387730574423, 1, 0), (19, '93740362', '新通知', '新通知内容，点击进行编辑', 1387730573602, 1, 0), (20, '93740362', '新通知', '新通知内容，点击进行编辑', 1387730692156, 1, 0), (21, '93740362', '新通知', '新通知内容，点击进行编辑', 1387730515471, 1, 0), (22, '93740362', '新通知', '新通知内容，点击进行编辑', 1387730514649, 1, 0), (23, '93740362', '新通知', '新通知内容，点击进行编辑', 1387730501574, 1, 0), (24, '93740362', '新通知', '新通知内容，点击进行编辑', 1387730461294, 1, 0), (25, '93740362', '新通知', '新通知内容，点击进行编辑', 1387731744155, 1, 0), (26, '93740362', '新通知', '新通知内容，点击进行编辑', 1387731744841, 1, 0), (27, '93740362', '新通知', '新通知内容，点击进行编辑', 1387731745671, 1, 0), (28, '93740362', '新通知', '新通知内容，点击进行编辑', 1387730893879, 1, 0), (29, '93740362', '新通知', '新通知内容，点击进行编辑', 1387730893196, 1, 0), (30, '93740362', '新通知', '新通知内容，点击进行编辑', 1387730891071, 1, 0), (31, '93740362', '新通知', '新通知内容，点击进行编辑', 1387730892999, 1, 0), (32, '93740362', '新通知', 'sdasasdasd', 1388140013902, 0, 0), (33, '93740362', '新通知', '新通知内容，点击进行编辑', 1387731964611, 1, 0), (34, '93740362', '新通知', '新通知内容，点击进行编辑', 1387731965403, 1, 0), (35, '93740362', '新通知', '新通知内容，点击进行编辑', 1387731965977, 1, 0), (36, '93740362', '新通知', 'dsadasdasdsad', 1388140026110, 1, 0), (37, '93740362', '新通知', '新通知内容，点击进行编辑', 1388414614249, 1, 0), (38, '93740362', 'addd', 'asas', 1388414649930, 1, 0), (39, '93740362', '新通知', '新通知内容，点击进行编辑', 1388414661532, 1, 0), (40, '93740362', '新通知', '新通知内容，点击进行编辑', 1388414674325, 1, 0), (41, '93740362', '新通知', '新通知内容，点击进行编辑', 1387939402153, 0, 0), (42, '93740362', '新通知', '新通知内容，点击进行编辑', 1388414938693, 1, 0), (43, '93740362', '新通知', '新通知内容，点击进行编辑', 1388414951321, 1, 0), (44, '93740362', '新通知', '新通知内容，点击进行编辑', 1387939403135, 0, 0), (45, '93740362', '新通知', '新通知内容，点击进行编辑', 1387939403321, 0, 0), (46, '93740362', '新通知', '新通知内容，点击进行编辑', 1387939403510, 0, 0), (47, '93740362', '新通知', '新通知内容，点击进行编辑', 1387939403678, 0, 0), (48, '93740362', '新通知', '林玄你好！', 1387953171793, 1, 0), (49, '93740362', '新通知', 'testing新通知内容，点击进行编辑', 1387939430955, 1, 0), (50, '93740362', '新通知', '新通知内容，点击进行编辑', 1387939404256, 0, 0), (51, '93740362', '新通知', '袋鼠同学，赶快回来！第三方斯蒂芬广东省公司', 1387983747657, 1, 0), (52, '93740362', '新通知', '索鸟同学，旅途愉快！', 1387983716869, 1, 0), (53, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985063026, 0, 0), (54, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985064158, 0, 0), (55, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985064848, 0, 0), (56, '93740362', '新通知', '通知索鸟，可携带一名人妖回国', 1388332124127, 1, 0), (57, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985065308, 0, 0), (58, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985065571, 0, 0), (59, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985065720, 0, 0), (60, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985065885, 0, 0), (61, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985066128, 0, 0), (62, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985066251, 0, 0), (63, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985066445, 0, 0), (64, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985066641, 0, 0), (65, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985066782, 0, 0), (66, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985066963, 0, 0), (67, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985067125, 0, 0), (68, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985067302, 0, 0), (69, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985067472, 0, 0), (70, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985067627, 0, 0), (71, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985067798, 0, 0), (72, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985068031, 0, 0), (73, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985068195, 0, 0), (74, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985068369, 0, 0), (75, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985068494, 0, 0), (76, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985068809, 0, 0), (77, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985068930, 0, 0), (78, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985069033, 0, 0), (79, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985071683, 0, 0), (80, '93740362', '范德萨的', '上次就犯错误上房揭瓦', 1388395666417, 1, 0), (81, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985072165, 0, 0), (82, '93740362', '新通知', '我们的袋鼠向太阳', 1388395576901, 1, 0), (83, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985072489, 0, 0), (84, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985072646, 0, 0), (85, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985072876, 0, 0), (86, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985073021, 0, 0), (87, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985073182, 0, 0), (88, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985073327, 0, 0), (89, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985084702, 0, 0), (90, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985084894, 0, 0), (91, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985085062, 0, 0), (92, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985085293, 0, 0), (93, '93740362', '新通知', '新通知内容，点击进行编辑', 1387985085414, 0, 0), (94, '93740362', '新通知', '新通知内容，点击进行编辑', 1388027753269, 0, 0), (95, '93740362', '新通知', '新通知内容，点击进行编辑', 1388027778064, 0, 0), (96, '93740362', '新通知', '新通知内容，点击进行编辑', 1388027755007, 0, 0), (97, '93740362', '新通知', '新通知内容，点击进行编辑', 1388027755978, 0, 0), (98, '123', '新通知', 'dasdasd', 1388139811362, 1, 0), (99, '123', '新通知', 'dfsdfdsfsd', 1388139840472, 1, 1), (100, '93740362', '新通知', '新通知内容，点击进行编辑', 1388332053071, 0, 0), (101, '93740362', 'test1', 'test1', 1388454844739, 1, 0), (102, '93740362', 'test2', 'test2', 1388454988652, 1, 0), (103, '93740362', 'test3', 'test3', 1388455040167, 1, 0), (104, '93740362', 'test4', 'test4', 1388455237277, 1, 0), (105, '93740362', 'test5', 'test5', 1388455292162, 1, 0), (106, '93740362', 'test6', 'test6', 1388456057805, 1, 0), (107, '93740362', 'test7', 'test7', 1388456116468, 1, 0), (108, '93740362', 'test8', 'test8', 1388456175111, 1, 0), (109, '93740362', 'test9', 'test9', 1388456207952, 1, 0), (110, '93740362', 'test10', 'test10', 1388456257468, 1, 0), (111, '93740362', 'test11', 'test11', 1388456739517, 1, 0), (112, '93740362', 'test12', 'test12', 1388456759882, 1, 0), (113, '93740362', 'test13', 'test13', 1388456780860, 1, 0), (114, '93740362', 'test14', 'test14', 1388456799070, 1, 0), (115, '93740362', 'test15', 'test15', 1388456816724, 1, 0), (116, '93740362', 'test16', 'test16', 1388457021428, 1, 0), (117, '93740362', 'test17', 'test17', 1388457041701, 1, 0), (118, '93740362', 'test18', 'test18', 1388457060683, 1, 0), (119, '93740362', 'test19', 'test19', 1388457113281, 1, 0), (120, '93740362', 'test20', 'test20', 1388457134792, 1, 0), (121, '93740362', 'test21', 'test21', 1388457194617, 1, 0), (122, '93740362', 'test22', 'test22', 1388457211035, 1, 0), (123, '93740362', 'test23', 'test23', 1388457225313, 1, 0), (124, '93740362', 'test24', 'test24', 1388457238788, 1, 0), (125, '93740362', 'test25', 'test25', 1388457252143, 1, 0), (126, '93740362', 'test26', 'test26', 1388457382292, 1, 0), (127, '93740362', 'test27', 'test27', 1388457397413, 1, 0), (128, '93740362', 'test28', 'test28', 1388457444912, 1, 0), (129, '93740362', 'test29', 'test29', 1388457486186, 1, 0), (130, '93740362', 'test30', 'test30', 1388457503972, 1, 0), (131, '93740362', 'test31', 'test31', 1388458278906, 1, 0), (132, '93740362', 'test32', 'test32', 1388458296996, 1, 0), (133, '93740362', 'test33', 'test33', 1388458319756, 1, 0), (134, '93740362', 'test34', 'test34', 1388458411110, 1, 0), (135, '93740362', 'test35', 'test35', 1388458472857, 1, 0), (136, '93740362', '牛逼哥测试1', '牛逼哥测试1', 1388549586077, 1, 0), (137, '93740362', '牛逼哥测试2', '牛逼哥测试2', 1388549617423, 1, 0), (138, '93740362', '牛逼哥测试3', '牛逼哥测试3', 1388549635301, 1, 0), (139, '93740362', '牛逼哥测试4', '牛逼哥测试4', 1388549651285, 1, 0), (140, '93740362', '20140101', '1', 1388550689042, 1, 0), (141, '93740362', '20140101', '2', 1388550705390, 1, 0), (142, '93740362', '20140101', '3', 1388550728209, 1, 0), (143, '93740362', '20140101', '4', 1388550746908, 1, 0), (144, '93740362', '20140101', '5', 1388550763789, 1, 0), (145, '93740362', '牛逼哥测试5', '牛逼哥测试5', 1388551471547, 1, 0), (146, '93740362', '1', '1', 1388559600902, 1, 0), (147, '93740362', '2', '2', 1388559615736, 1, 0), (148, '93740362', '3', '3', 1388559659558, 1, 0), (149, '93740362', '4', '4', 1388559647582, 1, 0), (150, '93740362', '5', '5', 1388559674213, 1, 0), (151, '93740362', '20140102', '1', 1388651363581, 1, 0), (152, '93740362', '20140102', '2', 1388651381551, 1, 0), (153, '93740362', '20140102', '3', 1388651520943, 1, 0), (154, '93740362', '4', '4', 1388651556059, 1, 0), (155, '93740362', '5', '5', 1388651564817, 1, 0), (156, '93740362', '1', '1', 1388735635228, 1, 0), (157, '93740362', '2', '2', 1388735644674, 1, 0), (158, '93740362', '3', '3', 1388735654937, 1, 0), (159, '93740362', '4', '4', 1388735667440, 1, 0), (160, '93740362', '5', '5', 1388735684016, 1, 0), (161, '93740362', '111', '新通知内容，点击进行编辑', 1388739306742, 1, 0), (162, '93740362', '新通知', '新通知内容，点击进行编辑', 1388864698092, 0, 0), (163, '93740362', '新通知', '新通知内容，点击进行编辑', 1389074448575, 1, 0), (164, '93740362', '本版功能通知', '通知按照创建时间降序排列', 1389064806557, 1, 0), (165, '93740362', '各位哥子好', '哥子好', 1389061412007, 1, 0), (166, '93740362', 'ERWRWER', 'WERWERFWERW CWERFWE', 1389060108984, 1, 0), (167, '93740362', '新通知', '新通知内容，点击进行编辑', 1389060089614, 0, 0), (168, '93740362', '新通知', '新通知内容，点击进行编辑\ndvdsvgdsvgds\ndsvdfvdsfv\ndsvdsvgsd\nvsdvsdddddddddddddd\n\nvdsfvsdsdsdsdsdsdsdsdsdsdsdsdsdsd\nvsdddddddddddddd', 1389065258370, 1, 0), (169, '93740362', '新通知', '新通知内容，点击进行编阿萨德爱的爱的安德顿阿萨德而发生飞辑', 1389065277572, 1, 0), (170, '93740362', '新通知', '放假十年\n新通知内容，点击进行编辑', 1389065299166, 1, 0), (171, '93740362', '新通知', 'hddfsafnapsmffffffffe新通知内容，点击进行编辑', 1389082780676, 0, 0), (172, '93740362', '新通知', '252818185611565515116511515115\n151515', 1389082783457, 1, 0), (173, '93740362', '新通知', '新通知内容，点击进行编辑', 1389082782440, 0, 0), (174, '93740362', '新通知', '新通知内容，点击进行编辑', 1389082772304, 1, 0), (175, '93740362', '新通知', '大大爱的阿道夫爱的新通知内容，点击进行编辑', 1390378650219, 1, 0), (176, '93740362', '111', '111', 1389181103966, 1, 0), (177, '93740362', '2', '新通知内容，点击进行编辑', 1392014241316, 1, 0), (178, '93740362', '新通知', '新通知内容，点击进行编辑', 1390378662045, 1, 0), (179, '93740362', '新通知', '新通知内容，点击进行编辑', 1392014231033, 1, 0), (180, '93740362', '111', 'qqq', 1389196365759, 1, 0), (181, '93740362', '2', '2', 1389632292141, 1, 0), (182, '93740362', '1', '1', 1389632251213, 1, 0), (183, '93740362', '3', '3', 1389633853775, 1, 0), (184, '93740362', '11', '11', 1389634272838, 1, 0), (185, '93740362', '22', '22', 1389634283702, 1, 0), (186, '93740362', '测试新通知', '测试1', 1389680930969, 1, 0), (187, '93740362', '222', '222', 1389684217803, 1, 0), (188, '93740362', '111', '111', 1389684206622, 1, 0), (189, '93740362', '222', '新通知内容，点击进行编辑', 1389685820637, 1, 0), (190, '93740362', '111', '新通知内容，点击进行编辑', 1389685814101, 1, 0), (191, '93740362', '123', '新通知内容，点击进行编辑', 1389686882638, 1, 0), (192, '93740362', '123', '123', 1389718964408, 1, 0), (193, '93740362', '新通知文档千万千万', '问 王企鹅的 新通知内容，点击进行编辑', 1389757621973, 1, 0), (194, '93740362', '缴费10000万', '大富翁按时 阿斯顿', 1389757683058, 1, 0), (195, '93740362', '苦逼的开发人员', '哇呀呀呀~~~~！！', 1389757733450, 1, 0), (196, '93740362', '新通知二大王', '问  请问', 1389757761016, 1, 0), (197, '93740362', '丰富二分钱文峰未发', '而非为巍峨 巍峨 未发温度缝分为服务二恶未发娃儿 全微分王二飞', 1389757823515, 1, 0), (198, '93740362', '服务而飞娃儿娃儿我二套房 娃儿 二', '二娃儿人权问题 荣威人娃儿切问而近思', 1389757854886, 1, 0), (199, '93740362', '【通知】请老师们前来开会', '请老师们到教师开会', 1389757916354, 1, 0), (200, '93740362', '大爷的大秦网', '爱的 的亲卫队典韦热风娃娃', 1389758150409, 1, 0), (201, '93740362', '缴费通知', '全部缴费100万。', 1389796441488, 1, 0), (202, '93740362', '123', '321', 1392010880706, 1, 0), (203, '93740362', '11111', '222222', 1389800154762, 1, 0), (204, '93740362', '12', '12新通知内容，点击进行编辑', 1389800244345, 1, 0), (205, '93740362', '111222', '222111', 1389800856490, 1, 0), (206, '93740362', '321', '321', 1389801005344, 1, 0), (207, '93740362', '111', '新通知内容，点击进行编辑', 1389801322693, 1, 0), (208, '93740362', '12121212', '新通知内容，121212', 1389801735851, 1, 0), (209, '93740362', '135', '246', 1389801938898, 1, 0), (210, '93740362', '4444', '55555', 1389802666655, 1, 0), (211, '93740362', '116', '116', 1389806140164, 1, 0), (212, '93740562', '新通知', '新通知内容，点击进行编辑', 1389885879241, 0, 1), (213, '93740562', '新通知', '新通知内容，点击进行编辑', 1389885879470, 0, 1), (214, '93740562', '新通知', '新通知内容，点击进行编辑', 1389885879644, 0, 1), (215, '93740562', '新通知', '新通知内容，点击进行编辑', 1389885879830, 0, 1), (216, '93740562', '西安的通知1', '西安的通知1西安的通知1西安的通知1西安的通知1西安的通知1西安的通知1西安的通知1西安的通知1西安的通知1西安的通知1西安的通知1西安的通知1西安的通知1西安的通知1西安的通知1西安的通知1西安的通知1西安的通知1西安的通知1西安的通知1西安的通知1西安的通知1西安的通知1西安的通知1', 1389886223665, 0, 1), (217, '93740362', '新通知', '新通知内容，点击进行编辑', 1390047539080, 0, 0), (218, '93740362', '新通知', '新通知内容，点击进行编辑', 1390047540108, 0, 0), (219, '93740362', '新通知', '新通知内容，点击进行编辑', 1390047541141, 0, 0), (220, '93740362', '新通知', '哥哥哥哥哥哥何厚铧接机把 vv 测测vvbjhb不会尴尬痕迹', 1390047566957, 0, 0), (221, '93740362', 'efwefweewrr', 'erewrwerwerwete4t', 1390184668951, 1, 0), (222, '93740362', '1020', '1020', 1390230806099, 1, 0), (223, '93740362', '111', '222', 1390232857320, 1, 0), (224, '93740362', '123', '123', 1390235698279, 1, 0), (225, '93740362', '2014', '2014 个人个', 1390371934314, 1, 0), (226, '93740362', '新通知', '爱的的新通知内容，点击进行编辑国际化的', 1390371984493, 1, 0), (227, '93740362', '新通知', '玩儿新通知内容，点击进行编辑', 1390371993984, 1, 0), (228, '93740362', '新通知', '番茄味新通知内容，点击进行编辑', 1390372001635, 1, 0), (229, '93740362', '新通知', '飞确认人', 1390372009979, 1, 0), (230, '93740362', '新通知', '人无人人人人飞', 1390372018117, 1, 0), (231, '93740362', '新通知', '新通知内容，点击进行编辑 烦人飞', 1390372024730, 1, 0), (232, '93740362', '灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知', '新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌新通知内容，灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌灌', 1390372229512, 1, 0), (233, '93740362', 'dasda', 'asdasd', 1390378680871, 1, 0), (234, '93740362', 'asdasdasd', 'asdczxczc', 1390378965891, 1, 0), (235, '93740362', '123', '321', 1390399809406, 1, 0), (236, '93740362', '123', '333', 1390490889976, 1, 0), (237, '93740362', '111', '2222', 1390491036162, 1, 0), (238, '93740362', '9999', '9999999', 1390547108258, 1, 0), (239, '93740362', '888888', '888888888', 1390547173816, 1, 0), (240, '93740362', '777', '777777', 1390547360592, 1, 0), (241, '93740362', '如图个人各提供未通过', '维尔特4她4她4', 1390722279109, 1, 0), (242, '93740362', 'erfwerfwe', '二外铁饭碗儿童套托维尔太人23问の日4人2', 1390722376588, 1, 0), (243, '93740362', '我的开发访问', '是袋鼠万二分位额外人味儿4她套 二娃', 1390722395734, 1, 0), (244, '93740362', '新通知', '新通知内容，点击进行编辑', 1390734444202, 1, 0), (245, '93740362', '新通知', '新通知内容，点击进行编辑', 1390734468123, 1, 0), (246, '93740362', '其服务范围', '而分为二肉味二夫人巍峨玩儿', 1391517355663, 1, 0), (247, '93740362', '新通知', '新通知内容，点击进行bv xg fg sxg  编辑s gfs gs s gs', 1391826887220, 1, 0), (248, '93740362', '新通知', 'hsfh dtshshfh f新通知内容，点击进行编辑', 1391831784267, 1, 0), (249, '93740362', '新通知', '是啊爱的新通知内容，点击进行编辑', 1391846132844, 1, 0), (250, '93740362', '新通知', '的啊啊啊大啊', 1391846201118, 1, 0), (251, '93740362', '新通知', '的啊的阿萨德的', 1392003884056, 1, 0), (252, '93740362', '新通知', '通知下雪了', 1392003901885, 1, 0), (253, '93740362', '新通知', '下雪新通知内容，点击进行编辑', 1392010897569, 1, 0), (254, '93740362', '新通知1', '新通知内容，点击进行编辑', 1392031309395, 1, 0), (255, '93740362', '新通知2', '新通知内容，点击进行编辑', 1392031319940, 1, 0), (256, '93740362', '新通知3', '新通知内容，点击进行编辑', 1392031326556, 1, 1), (257, '93740362', '新通知4', '新通知内容，点击进行编辑', 1392031332383, 1, 1), (258, '93740362', '新通知', '新通知内容，点击进行编辑', 1392043416415, 1, 1), (259, '93740362', '新通知', '新通知内容，点击进行编辑', 1392043417991, 1, 1), (260, '93740362', '新通知', '新通知内容，点击进行编辑', 1392044255443, 1, 1), (261, '93740362', '新通知', '新通知内容，点击进行编辑', 1392044257973, 1, 1), (262, '93740362', '111', '新通知内容，点击进行编辑', 1392046019354, 1, 1), (263, '93740362', 'xzx', 'sadasd', 1392087524149, 1, 1), (264, '93740362', '1', '新通知内容，点击进行编辑', 1392103367783, 1, 1), (265, '93740362', '2', '新通知内容，点击进行编辑', 1392103375707, 1, 1), (266, '93740362', '3', '新通知内容，点击进行编辑', 1392103384023, 1, 1), (267, '93740362', '4', '新通知内容，点击进行编辑', 1392103389402, 1, 1), (268, '93740362', '5', '新通知内容，点击进行编辑', 1392103394265, 1, 1), (269, '93740362', '6', '新通知内容，点击进行编辑', 1392103399422, 1, 1), (270, '93740362', '7', '新通知内容，点击进行编辑', 1392103407013, 1, 1), (271, '93740362', '8', '新通知内容，点击进行编辑', 1392103412311, 1, 1), (272, '93740362', '9', '新通知内容，点击进行编辑', 1392103417604, 1, 1), (273, '93740362', '10', '新通知内容，点击进行编辑', 1392103640854, 1, 1), (274, '93740362', '我心永恒', '有钱能买高智商，小朋友们', 1392112623407, 1, 1), (275, '93740362', '方法为万二分位', '额外热风温柔范围人王二娃', 1392112823081, 1, 1), (276, '93740362', '热帖风格日托日托全文让他玩儿法', '二哥任天狗\\(^o^)/~254一套 让他', 1392112836069, 1, 1), (277, '93740362', '54因为二胎也同样54套4同254调通天气4提琴4透透气忍痛她4她4提前4图4她4图4她4图4他34提琴4弹钢琴问个太感人跳过去儿童歌铜钱头提琴5也5☼4通用45提琴35提琴35弹钢琴热天气44提前4她4提取额过期5套5①4还挺会过一会忒隔热阴云护腕', '胶东在线网2月10日讯(记者 贾楚航 李婷婷)连日来，曾患白血病的大学生村官张广秀心怀感恩，致信习近平总书记后，竟意外收到习总书记语重心长的回信，这不仅让张广秀激动不已，更让当地干部群众深受鼓舞，也让全国30万名大学生村官更加坚定了扎根基层的信念。而习总书记复信一名村官所释放的民本理念和至爱情怀，也在继续鼓舞、感染着更多的人。\n　　■兑现诺言——\n　　“病好了，我还要回去当村官”\n　　出生于1986年1月的张广秀是山东临沂市罗庄区罗庄街道办事处桥西头村人，2009年7月毕业于鲁东大学政法学院，当年8月考取大学生“村官”，在烟台市福山区福新街道垆上村担任村委会主任助理职务。\n　　踏上“村官”岗位以来，张广秀服务群众，以实际行动赢得了党员干部群众好评。2010年9月，张广秀被确诊为急性白血病住院治疗，在医院仍惦记着工作。\n　　张广秀的事迹和病情经媒体报道后，在全国引起强烈反响。习近平等中央领导人做出重要批示，号召向张广秀同志学习。在习近平等中央领导同志和有关方面的亲切关怀下，2011年初，张广秀接受了手术治疗，身体逐渐康复，并于2013年6月重返阔别了33个月的村官岗位，兑现了自己“病好了，还要回去当村官”的诺言。\n　　张广秀也先后获得“全国三八红旗手”、“山东省三八红旗手”、“山东省十佳大学生村官”、“山东青年五四奖章”等荣誉称号。\n　　重返岗位后的张广秀充满了干劲儿，每天天不亮就骑自行车到村里。回到熟悉的工作环境和同事们中间，在崭新的人生与村官事业面前，她已经迫不及待了。\n　　所以，在1月15日写给习总书记的信中，她除了详细汇报自己的工作生活情况，还重点表示一定不辜负总书记的殷切期望，努力工作，服务群众，勤奋学习，不断进步，为实现中国梦作出自己的贡献。\n　　1月28日，习总书记复信张广秀，对张广秀康复良好、重返工作岗位感到欣慰和高兴，希望大学生村官热爱基层、扎根基层，增长见识、增长才干，促农村发展，让农民受益，让青春无悔。\n　　收到习总书记复信，也让村官张广秀再一次成为焦点。\n　　■收到复信——\n　　“做一个村官，是我不变的信念”\n　　自2009年成为烟台市福山区第一批14名大学生村官中的一员，如今的张广秀已成为村官中的典型代表。\n　　说起收到回信的那一刻，张广秀起初不敢相信，继而内心充满激动。“作为一名普通大学生村官，我并没做出什么轰轰烈烈的大事，但中央领导人这么关心我，我感觉特别荣幸。”张广秀说，读到习总书记信中“希望你仍要注意保重身体”一句时，她感到特别温暖。\n　　从习近平总书记的复信中，张广秀感受到了总书记对她个人的关心，也感受到了总书记对全国大学生村官的关心和肯定，更感受到了习总书记对广大基层干部的一种殷切期望和谆谆教导。“习总书记特别提到要给垆上村的老百姓转达节日的祝福，这虽是很普通的一句话，但恰好说明在习总书记心里，一直装着基层情怀和对老百姓生活的牵挂。”\n　　作为收到总书记复信的“明星村官”，张广秀坦言，习总书记语重心长的回信，让她深感肩上的担子更重了。“我一定坚持自己的选择，在农村基层扎下根，增长知识才干，让我的第二次生命不虚度，让我的青春无悔。”\n　　“第三次全国经济普查中，我光荣地被选为普查员，今年我一定会把这项工作做好，另外，今年修建龙烟铁路要经过我们村，到时村里可能要面临拆迁，这也是一件大事，我也会积极做好自己的工作。”张广秀说。\n　　“现在不光是要干工作，更要把工作干好。”谈起未来的打算和目标，张广秀坦言自己正在酝酿一些新规划，主要是想找一些合适的项目帮村民致富。\n　　■村官群体——\n　　“我们一定扎根基层，让农民受益”\n　　习总书记复信让烟台市186名大学生村官受到极大鼓舞，也让全国30万大学生村官群体再次跃入人们视线。\n　　数据显示，自2008年中央全面启动选聘大学生到村任职工作以来，目前全国在岗大学生村官数量已超过30万，到2020年，中国大学生村官数量将达到60万人，实现一村一名大学生村官的目标。\n　　“我就是受张广秀同志的影响，才在大学毕业后选择做了一名大学生村官。”海阳市辛安镇向阳村党支部书记助理欧阳鹏说，他是2012届大学生村官，张广秀身患重病仍不忘工作的精神打动了他。“张广秀吃苦耐劳，全心全意为百姓服务，是我们学习的榜样。”\n　　出生于1991年、在福山高新区姜刘疃村任书记助理的大学生村官王群说：“作为一名最基层的村官，真没想到习总书记会一直关注着我们这个群体，在日理万机的情况下还会给一个村官回信，这给了我们莫大的鼓舞和信心。今后我一定会在基层中锻炼自己、增长才干，不辜负习总书记对我们的期望、认可与肯定！”\n　　莱阳市城厢街道东赵疃村“第一书记”徐彬说，看到习近平总书记的复信，内心既深受鼓舞，又深感责任重大。“复信中说要推动广大农村全面建成小康，我认为这就是广大农民的‘中国梦’。”徐彬说，作为一名村官，就要为老百姓办实事，努力帮助农民实现自己的“中国梦”。\n　　习总书记的复信引起当地党委政府的高度重视，目前烟台市、福山区均出台系列政策，帮助大学生村官扎根农村，发挥才干。\n　　眼下，学习张广秀精神，扎根基层，促农村发展，让农民受益，让青春无悔，正内化为坚定的信念，在春天的大地上涌动着生生不息的激情与希望。', 1392113767015, 1, 1), (278, '93740362', '111', '新通知内容，点击进行编辑', 1392118446759, 1, 1), (279, '93740362', '222', '新通知内容，点击进行编辑', 1392125240322, 1, 1), (280, '93740362', '22222', '额定范围为二位玩儿他娃儿', 1392189586063, 1, 1), (281, '93740362', '分为服务', '维尔瓦任务人娃儿', 1392189650647, 1, 1), (282, '93740362', '忍挑染4她4他', '让她如图让他加乎据叩u', 1392189663366, 1, 1), (283, '93740362', '测试一条数据', '新的发布规则是，一点发布之后就不能编辑了', 1393428658824, 1, 1), (284, '93740362', '测试第二条', '啊实打实大', 1393428935462, 1, 1), (285, '93740362', 'asdasad', 'asdasdasdasda', 1393428946176, 0, 1), (286, '93740362', 'qwqweqwe', 'qweqweqweqweqew', 1393428975562, 0, 1), (287, '93740362', 'dfsg', 'dfgdsfdsf', 1393432265706, 1, 0), (288, '93740362', 'wefw', 'wefwefwefwef', 1393432289749, 1, 1), (289, '93740362', 'sdfds', 'fwfewefwef', 1393432307508, 1, 0), (290, '93740362', 'wdqqwdqwfwefwe', 'fwfewfewefwfw', 1393432353622, 1, 0), (291, '93740362', 'kjhkhjkh', 'kkhjkhjkhj', 1393433425051, 1, 1), (292, '93740362', '新标题', '新内容', 1393511644614, 1, 1);
/*!40000 ALTER TABLE `news` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `newsread`
--

DROP TABLE IF EXISTS `newsread`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `newsread` (
  `uid`       INT(11)     NOT NULL AUTO_INCREMENT,
  `school_id` VARCHAR(20) NOT NULL,
  `parent_id` VARCHAR(40) NOT NULL,
  `news_id`   INT(11)     NOT NULL,
  `readTime`  BIGINT(20)  NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =13
  DEFAULT CHARSET =utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `newsread`
--

LOCK TABLES `newsread` WRITE;
/*!40000 ALTER TABLE `newsread` DISABLE KEYS */;
INSERT INTO `newsread` VALUES (1, '937403621', '1', 5,
                               1387651072984), (2, '937403621', '1', 5, 1387651073001), (3, '93740362', '1', 48, 1387983409358), (4, '93740362', '1', 52, 1387983415682), (5, '93740362', '1', 52, 1387983415686), (6, '93740362', '1', 51, 1387983533588), (7, '93740362', '1', 3, 1387983567542), (8, '93740362', '1', 33, 1387983588335), (9, '93740362', '1', 33, 1387983588338), (10, '93740362', '1', 80, 1388395694780), (11, '93740362', '1', 161, 1388994589470),
  (12, '93740362', '1', 165, 1389081808995);
/*!40000 ALTER TABLE `newsread` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parentinfo`
--

DROP TABLE IF EXISTS `parentinfo`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `parentinfo` (
  `uid`          INT(11)          NOT NULL AUTO_INCREMENT,
  `name`         VARCHAR(20)
                 COLLATE utf8_bin NOT NULL,
  `parent_id`    VARCHAR(40)
                 COLLATE utf8_bin NOT NULL,
  `relationship` VARCHAR(20)
                 COLLATE utf8_bin NOT NULL,
  `phone`        VARCHAR(16)
                 COLLATE utf8_bin NOT NULL,
  `gender`       TINYINT(4)       NOT NULL DEFAULT '2',
  `company`      VARCHAR(200)
                 COLLATE utf8_bin NOT NULL DEFAULT '',
  `picurl`       VARCHAR(128)
                 COLLATE utf8_bin NOT NULL DEFAULT '',
  `birthday`     DATE             NOT NULL DEFAULT '1800-01-01',
  `school_id`    VARCHAR(20)
                 COLLATE utf8_bin NOT NULL,
  `status`       TINYINT(4)       NOT NULL DEFAULT '1',
  `update_at`    BIGINT(20)       NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `parent_id` (`parent_id`),
  UNIQUE KEY `phone_3` (`phone`),
  KEY `school_id` (`school_id`),
  KEY `phone` (`phone`),
  KEY `birthday` (`birthday`),
  KEY `phone_2` (`phone`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =165
  DEFAULT CHARSET =utf8
  COLLATE =utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parentinfo`
--

LOCK TABLES `parentinfo` WRITE;
/*!40000 ALTER TABLE `parentinfo` DISABLE KEYS */;
INSERT INTO `parentinfo` VALUES (2, '林玄', '2_93740362_456', '爸爸', '18980030903', 1, '',
                                 'http://suoqin-test.u.qiniudn.com/FvX4rSaBmkcTGNJGMfpQVQNnEqh-', '1800-01-01',
                                 '93740362', 1,
                                 1393035671444), (3, '袋鼠', '2_93740362_789', '妈妈', '13408654680', 0, '', '', '1800-01-01', '93740362', 1, 1391831671469), (29, '豫姐', '2_1389413328789', '妈妈', '13408654681', 1, '', '/assets/images/portrait_placeholder.png', '1980-01-01', '93740362', 1, 1389413328789), (31, '啊牛', '2_1389445750151', '爸爸', '13408654688', 0, '', '/assets/images/portrait_placeholder.png', '1980-01-01', '93740362', 1, 1389445820830), (32, '龙套', '2_1389452053171', '爸爸', '13408654689', 0, '', '/assets/images/portrait_placeholder.png', '1980-01-01', '93740362', 1, 1389452526277), (109, '何忍', '2_1389634766817', '妈妈', '13708089040', 1, '', '/assets/images/portrait_placeholder.png', '1984-09-03', '93740362', 1, 1389634766817), (141, '李毅', '2_1389696347532', '爸爸', '13402815317', 0, '', 'http://suoqin-test.u.qiniudn.com/FjcuakeKi5LhH3pwDlGzP1VnZHhP', '1979-10-20', '93740362', 1, 1389795264463), (148, '张洁', '2_1390238560925', '妈妈', '13279491366', 1, '', '/assets/images/portrait_placeholder.png', '1980-01-01', '93740362', 1, 1390238560925), (149, '李老大', '2_1390359054366', '爸爸', '15828178309', 0, '', '/assets/images/portrait_placeholder.png', '1980-01-01', '93740362', 1, 1390359054366), (150, '郭世东', '2_1391836223533', '爸爸', '18611055373', 0, '', 'http://suoqin-test.u.qiniudn.com/FhdoadN7g_dk3CZBaKi2Q-yG6hEI', '1980-01-01', '93740362', 1, 1391836223533), (161, '马大帅', '2_1393346834597', '', '21211222232', 1, '', '/assets/images/portrait_placeholder.png', '1980-01-01', '93740362', 1, 1393346834597),
  (164, '鑫哥', '2_1394011814045', '', '18782242007', 0, '', '/assets/images/portrait_placeholder.png', '1980-01-01',
   '93740362', 1, 1394011814045);
/*!40000 ALTER TABLE `parentinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `play_evolutions`
--

DROP TABLE IF EXISTS `play_evolutions`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `play_evolutions` (
  `id`            INT(11)      NOT NULL,
  `hash`          VARCHAR(255) NOT NULL,
  `applied_at`    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `apply_script`  TEXT,
  `revert_script` TEXT,
  `state`         VARCHAR(255) DEFAULT NULL,
  `last_problem`  TEXT,
  PRIMARY KEY (`id`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `play_evolutions`
--

LOCK TABLES `play_evolutions` WRITE;
/*!40000 ALTER TABLE `play_evolutions` DISABLE KEYS */;
INSERT INTO `play_evolutions` VALUES (1, '84a41a633ffaa7f2c6772b52c788c6ec4aef7bd9', '2013-12-19 15:21:22',
                                      'CREATE TABLE kindergarten (\nid    SERIAL PRIMARY KEY,\nname  VARCHAR(255) NOT NULL,\ntitle VARCHAR(255) NOT NULL\n);\n\nINSERT INTO kindergarten (name, title) VALUES\n(\'school23\', \'成都市第二十三幼儿园\'),\n(\'school1\', \'西安电子科技大学\'),\n(\'school2\', \'清华池\');',
                                      'DROP TABLE IF EXISTS kindergarten;', 'applied', ''),
  (2, '73dac56aef9073a57e231af00729cb43942ef3d5', '2013-12-19 15:21:22',
   'CREATE TABLE news (\nid        SERIAL PRIMARY KEY,\nk_id      LONG         NOT NULL,\ntitle     VARCHAR(255) NOT NULL,\ncontent   TEXT         NOT NULL,\nissueDate DATE         NOT NULL,\npublished INT          NOT NULL DEFAULT 0,\nstatus    INT          NOT NULL DEFAULT 1\n);\n\nINSERT INTO news (k_id, title, content, issueDate, published) VALUES\n(1, \'通知1\',\n\'缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知\',\n\'2013-10-01\', 1),\n(1, \'通知2\',\n\'学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电\',\n\'2013-1-01\', 1),\n(1, \'通知3\',\n\'恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击\',\n\'2012-10-01\', 0);',
   'DROP TABLE IF EXISTS news;', 'applied', ''), (3, 'eb8a5c9a226a6f069a1074d0e74e131c2691ab0d', '2013-12-19 15:21:22',
                                                  'CREATE TABLE parent (\nid       SERIAL PRIMARY KEY,\nk_id     LONG         NOT NULL,\nchild_id INT          NOT NULL,\nname     VARCHAR(255) NOT NULL,\ndesc     VARCHAR(255) NOT NULL\n);\n\nINSERT INTO parent (k_id, child_id, name, desc) VALUES (1, 1, \'你爸\', \'圆圆家长\'),\n(1, 2, \'尼玛\', \'土豆家长\'),\n(1, 3, \'你妹\', \'地瓜家长\');',
                                                  'DROP TABLE IF EXISTS parent;', 'applying_up',
                                                  'You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'desc     VARCHAR(255) NOT NULL\n)\' at line 6 [ERROR:1064, SQLSTATE:42000]');
/*!40000 ALTER TABLE `play_evolutions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `relationmap`
--

DROP TABLE IF EXISTS `relationmap`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `relationmap` (
  `uid`          INT(11)          NOT NULL AUTO_INCREMENT,
  `child_id`     VARCHAR(40)
                 COLLATE utf8_bin NOT NULL,
  `parent_id`    VARCHAR(40)
                 COLLATE utf8_bin NOT NULL,
  `card_num`     VARCHAR(20)
                 COLLATE utf8_bin NOT NULL DEFAULT '''''',
  `relationship` VARCHAR(20)
                 COLLATE utf8_bin NOT NULL DEFAULT '''妈妈''',
  `status`       TINYINT(4)       NOT NULL DEFAULT '1',
  PRIMARY KEY (`uid`),
  KEY `child_id` (`child_id`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =170
  DEFAULT CHARSET =utf8
  COLLATE =utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `relationmap`
--

LOCK TABLES `relationmap` WRITE;
/*!40000 ALTER TABLE `relationmap` DISABLE KEYS */;
INSERT INTO `relationmap` VALUES (154, '1_1389455065144', '2_1393346834597', '2323232323', '妈妈', 1),
  (155, '1_1391836223533', '2_93740362_789', '1231311312', '妈妈', 1),
  (156, '1_93740362_374', '2_1393511812493', '3434343434', '爷爷', 1),
  (157, '1_93740362_374', '2_93740362_456', '1231212312', '妈妈', 1),
  (166, '1_1393768956259', '2_93740362_456', '7777777777', '妈妈', 1),
  (167, '1_1389634766817', '2_1389696347532', '6655656565', '爸爸', 1),
  (168, '1_1393768956259', '2_1394011814045', '2133123232', '爸爸', 1),
  (169, '1_1390359054366', '2_1394011814045', '3333222323', '奶奶', 1);
/*!40000 ALTER TABLE `relationmap` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `scheduleinfo`
--

DROP TABLE IF EXISTS `scheduleinfo`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scheduleinfo` (
  `uid`         INT(11)          NOT NULL AUTO_INCREMENT,
  `school_id`   VARCHAR(20)
                COLLATE utf8_bin NOT NULL,
  `class_id`    INT(11)          NOT NULL,
  `schedule_id` INT(11)          NOT NULL,
  `mon_am`      VARCHAR(40)
                COLLATE utf8_bin DEFAULT '',
  `tue_am`      VARCHAR(40)
                COLLATE utf8_bin DEFAULT '',
  `wed_am`      VARCHAR(40)
                COLLATE utf8_bin DEFAULT '',
  `thu_am`      VARCHAR(40)
                COLLATE utf8_bin DEFAULT '',
  `fri_am`      VARCHAR(40)
                COLLATE utf8_bin DEFAULT '',
  `mon_pm`      VARCHAR(40)
                COLLATE utf8_bin DEFAULT '',
  `tue_pm`      VARCHAR(40)
                COLLATE utf8_bin DEFAULT '',
  `wed_pm`      VARCHAR(40)
                COLLATE utf8_bin DEFAULT '',
  `thu_pm`      VARCHAR(40)
                COLLATE utf8_bin DEFAULT '',
  `fri_pm`      VARCHAR(40)
                COLLATE utf8_bin DEFAULT '',
  `status`      INT(11) DEFAULT '1',
  `timestamp`   BIGINT(20)       NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =14
  DEFAULT CHARSET =utf8
  COLLATE =utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scheduleinfo`
--

LOCK TABLES `scheduleinfo` WRITE;
/*!40000 ALTER TABLE `scheduleinfo` DISABLE KEYS */;
INSERT INTO `scheduleinfo` VALUES
  (2, '93740362', 777666, 121, '手工水蜜桃', '木工', '电工', '钳工', '车工', '语文', '数学', '化学', '政治', '历史', 0,
   0), (3, '93740362', 777888, 123, '手工大闸蟹', '木工', '电工', '钳工', '车工', '语文', '数学', '化学', '政治', '历史', 0, 0), (4, '93740362', 777999, 125, '手工西瓜', '木工', '电工', '钳工', '车工', '语文', '数学', '化学', '政治', '历史', 0, 0), (5, '93740362', 777888, 124, '手工大苹果', '木工', '电工', '钳工', '车工', '语文', '数学', '化学', '政治', '历史', 0, 1389379492680), (6, '93740362', 777999, 126, '手工香蕉', '木工', '电工', '钳工', '车工', '语文', '数学', '化学', '政治', '历史', 0, 1389379502201), (7, '93740362', 777999, 127, '手工香蕉', '木工', '电工', '钳工', '车工', '语文', '数学', '化学', '政治', '历史', 0, 1389379504620), (8, '93740362', 777999, 128, '手工香蕉', '木工', '电工', '钳工', '车工', '语文', '数学', '化学', '政治', '历史', 1, 1389379505698), (9, '93740362', 777666, 122, '手工李耳', '木工', '电工', '钳工', '车工', '语文', '数学', '化学', '政治', '历史', 0, 1389379514751), (10, '93740362', 777666, 123, '手工李耳', '木工丽儿', '电工', '钳工', '车工', '语文', '数学', '化学', '政治', '历史', 1, 1389638903254), (11, '93740362', 777888, 125, '手工', '木工', '电工', '钳工', '车工', '语文', '数学', '化学', '政治', '历史', 0, 1390294652992), (12, '93740362', 777888, 126, '手工', '木工', '电工', '钳工', '实弹射击', '电工', '挖掘机', '汽修', '挖井', '历史', 0, 1391845270329),
  (13, '93740362', 777888, 127, '手工', '木工', '电工', '钳工', '实弹射击', '电工', '挖掘机', '汽修', '打洞', '历史', 1, 1392015120639);
/*!40000 ALTER TABLE `scheduleinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schoolinfo`
--

DROP TABLE IF EXISTS `schoolinfo`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `schoolinfo` (
  `uid`         INT(11)          NOT NULL AUTO_INCREMENT,
  `school_id`   VARCHAR(20)
                COLLATE utf8_bin NOT NULL,
  `province`    VARCHAR(20)
                COLLATE utf8_bin NOT NULL,
  `city`        VARCHAR(20)
                COLLATE utf8_bin NOT NULL,
  `name`        VARCHAR(20)
                COLLATE utf8_bin NOT NULL,
  `description` TEXT
                COLLATE utf8_bin,
  `phone`       VARCHAR(16)
                COLLATE utf8_bin NOT NULL,
  `status`      TINYINT(4)       NOT NULL DEFAULT '1',
  `logo_url`    VARCHAR(256)
                COLLATE utf8_bin DEFAULT NULL,
  `update_at`   BIGINT(20)       NOT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `school_id` (`school_id`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =3
  DEFAULT CHARSET =utf8
  COLLATE =utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;


CREATE TABLE sessionlog (
  uid         INT(11)          NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  session_id varchar(40) NOT NULL,
  content   TEXT NOT NULL,
  media_url TEXT DEFAULT '',
  media_type VARCHAR(10) DEFAULT 'image',
  sender VARCHAR(40) NOT NULL DEFAULT '',
  sender_type CHAR(1) NOT NULL DEFAULT 't',
  update_at BIGINT(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (uid)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =3
  DEFAULT CHARSET =utf8
  COLLATE =utf8_bin;
--
-- Dumping data for table `schoolinfo`
--

LOCK TABLES `schoolinfo` WRITE;
/*!40000 ALTER TABLE `schoolinfo` DISABLE KEYS */;
INSERT INTO `schoolinfo` VALUES (1, '93740362', '四川省', '成都', '天之骄子幼儿园',
                                 '成都天之骄子幼儿园始建于1998年，是一所全日制幼儿园。幼儿园设有大、中、小班，进行分班教学，实行小班制。幼儿园地处艺术学院南院校区，环境幽雅，文化氛围浓厚，是幼儿健康成长，快乐求知的理想之地。\n  多年来，幼儿园坚持“一切为了孩子”的办园宗旨，把幼儿的健康，安全放在工作的首位，不断改善办园条件，尤其是近两年在各级领导的关心支持下配备了齐全的生活设施，提供了优良的生活条件，为幼儿创设了愉悦、宽松的成长氛围。室外的活动区域铺设了塑胶地板，配有新型滑梯，及游戏、健身等用品。室内全部铺设了安全卫生的木地板，每班配有钢琴、电视、录音机及足量的玩具和幼儿图书。洁净，温馨的寝室，便利适宜的盥洗设施。优化了育人环境，为逐步实现教育的现代化奠定了坚实的物质基础。\n  我园始终坚持“以人为本”的教育理念。拥有一支作风硬，业务精，素质高，爱心浓的教师队伍。教师以幼儿为本，以幼儿的需要为出发点，每位教师都把做这项工作当作是一种兴趣，一种幸福，一种满足，为培养幼儿健全的人格，促进其身心健康的发展倾注了无私的爱，孩子们在爱的氛围中快乐、健康的成长。\n  “一切为了孩子，让孩子们接受更好的学前教育，满足家长培养孩子成才的要求”，是我们的办园理念，我们将本着“追求卓越”的精神，把幼儿园办成幼儿最向往、最喜爱的成长乐园。',
                                 '13991855476', 1, 'http://suoqin-test.u.qiniudn.com/FuWaWS1snZMYubUncFtEs3HqL_f3',
                                 1389767835216), (2, '93740562', '陕西省', '西安', '西安市蓝翔技工',
                                                  '\n苦逼幼儿园，成立时间超过100年，其特点有：\n1.价格超贵\n2.硬件超好\n3.教师超屌\n4.绝不打折\n5.入园超难\n6.......\n.......\n.......\n.......\n.......\n\n',
                                                  '13291855476', 1,
                                                  'http://www.houstonisd.org/cms/lib2/TX01001591/Centricity/Domain/16137/crestgif.gif',
                                                  1389886206739);
/*!40000 ALTER TABLE `schoolinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'prod'
--

--
-- Dumping routines for database 'prod'
--
/*!40103 SET TIME_ZONE = @OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE = @OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES = @OLD_SQL_NOTES */;

-- Dump completed on 2014-03-07  1:05:11
