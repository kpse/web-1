SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS  `accountinfo`;
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
) ENGINE=InnoDB AUTO_INCREMENT=12051 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `advertisement`;
CREATE TABLE `advertisement` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `school_id` varchar(20) NOT NULL,
  `position_id` int(11) DEFAULT '0',
  `link` text NOT NULL,
  `image` text NOT NULL,
  `name` varchar(20) NOT NULL DEFAULT '',
  `status` int(4) NOT NULL DEFAULT '1',
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

DROP TABLE IF EXISTS  `appinfo`;
CREATE TABLE `appinfo` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `version_code` int(11) NOT NULL,
  `version_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `url` varchar(128) COLLATE utf8_bin NOT NULL,
  `package_type` varchar(20) COLLATE utf8_bin DEFAULT 'parent',
  `summary` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `file_size` bigint(20) NOT NULL,
  `release_time` bigint(20) NOT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `version_code` (`version_code`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `assess`;
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
  `publisher_id` varchar(40) COLLATE utf8_bin DEFAULT NULL,
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`),
  KEY `child_id` (`child_id`),
  KEY `school_id` (`school_id`)
) ENGINE=InnoDB AUTO_INCREMENT=283 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `assignment`;
CREATE TABLE `assignment` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `school_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `class_id` int(11) NOT NULL,
  `title` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `image` text COLLATE utf8_bin,
  `publisher` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT '',
  `publisher_id` varchar(40) COLLATE utf8_bin DEFAULT NULL,
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `cardinfo`;
CREATE TABLE `cardinfo` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `cardnum` varchar(20) COLLATE utf8_bin NOT NULL,
  `userid` varchar(40) COLLATE utf8_bin NOT NULL,
  `expiredate` date NOT NULL DEFAULT '2200-01-01',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `cardnum` (`cardnum`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `chargeinfo`;
CREATE TABLE `chargeinfo` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `school_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `total_video_account` int(11) DEFAULT '0',
  `total_phone_number` int(11) DEFAULT '0',
  `expire_date` date NOT NULL DEFAULT '2200-01-01',
  `status` int(11) DEFAULT '1',
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `childinfo`;
CREATE TABLE `childinfo` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `child_id` varchar(40) COLLATE utf8_bin NOT NULL,
  `student_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `gender` tinyint(4) NOT NULL DEFAULT '2',
  `classname` varchar(40) COLLATE utf8_bin NOT NULL DEFAULT '',
  `picurl` varchar(512) COLLATE utf8_bin DEFAULT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=9179 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `classinfo`;
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
) ENGINE=InnoDB AUTO_INCREMENT=750 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `conversation`;
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

DROP TABLE IF EXISTS  `cookbookinfo`;
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
) ENGINE=InnoDB AUTO_INCREMENT=130 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `dailylog`;
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
) ENGINE=InnoDB AUTO_INCREMENT=56505 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `employeeinfo`;
CREATE TABLE `employeeinfo` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `employee_id` varchar(40) COLLATE utf8_bin NOT NULL,
  `phone` varchar(16) COLLATE utf8_bin NOT NULL,
  `gender` tinyint(4) NOT NULL DEFAULT '2',
  `workgroup` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `workduty` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `picurl` varchar(512) COLLATE utf8_bin DEFAULT '',
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
) ENGINE=InnoDB AUTO_INCREMENT=1548 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `feedback`;
CREATE TABLE `feedback` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `phone` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci,
  `comment` text COLLATE utf8mb4_unicode_ci,
  `source` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'android_parent',
  `status` tinyint(4) NOT NULL DEFAULT '1',
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS  `macwhitelist`;
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

DROP TABLE IF EXISTS  `news`;
CREATE TABLE `news` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `school_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `class_id` int(11) NOT NULL DEFAULT '0',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `image` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  `published` tinyint(4) NOT NULL DEFAULT '0',
  `publisher_id` varchar(40) COLLATE utf8_bin DEFAULT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=444 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `newsread`;
CREATE TABLE `newsread` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `school_id` varchar(20) NOT NULL,
  `parent_id` varchar(40) NOT NULL,
  `news_id` int(11) NOT NULL,
  `readTime` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `parentinfo`;
CREATE TABLE `parentinfo` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) COLLATE utf8_bin NOT NULL,
  `parent_id` varchar(40) COLLATE utf8_bin NOT NULL,
  `relationship` varchar(20) COLLATE utf8_bin NOT NULL,
  `phone` varchar(16) COLLATE utf8_bin NOT NULL,
  `gender` tinyint(4) NOT NULL DEFAULT '2',
  `company` varchar(200) COLLATE utf8_bin DEFAULT '''''',
  `picurl` varchar(512) COLLATE utf8_bin DEFAULT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=12082 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `play_evolutions`;
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

DROP TABLE IF EXISTS  `privilege`;
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
) ENGINE=InnoDB AUTO_INCREMENT=445 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `relationmap`;
CREATE TABLE `relationmap` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `child_id` varchar(40) COLLATE utf8_bin NOT NULL,
  `parent_id` varchar(40) COLLATE utf8_bin NOT NULL,
  `card_num` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT '''''',
  `relationship` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT '''ย่ย่''',
  `reference_id` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `card_num` (`card_num`),
  UNIQUE KEY `parent_child_unique` (`child_id`,`parent_id`) USING BTREE,
  UNIQUE KEY `reference_id` (`reference_id`) USING BTREE,
  KEY `child_id` (`child_id`),
  KEY `parent_id` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12345 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `scheduleinfo`;
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
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `schoolinfo`;
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
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `sessionlog`;
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
) ENGINE=InnoDB AUTO_INCREMENT=2631 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `sessionread`;
CREATE TABLE `sessionread` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `school_id` varchar(20) COLLATE utf8_bin NOT NULL,
  `reader_id` varchar(40) COLLATE utf8_bin NOT NULL,
  `topic` varchar(40) COLLATE utf8_bin NOT NULL,
  `last_read_session_id` int(11) NOT NULL,
  `read_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=267 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS  `videomembers`;
CREATE TABLE `videomembers` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `school_id` varchar(20) NOT NULL,
  `parent_id` varchar(40) NOT NULL,
  `account` char(32) NOT NULL,
  `status` int(11) DEFAULT '1',
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `account` (`account`,`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `videoprovidertoken`;
CREATE TABLE `videoprovidertoken` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `school_id` varchar(20) NOT NULL,
  `token` char(32) NOT NULL,
  `status` int(11) DEFAULT '1',
  `update_at` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `school_id` (`school_id`,`token`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;

