# --- !Ups
--
-- 表的结构 appinfo
--

CREATE TABLE appinfo (
  uid          INT(11)          NOT NULL AUTO_INCREMENT,
  version_code INT(11)          NOT NULL,
  version_name VARCHAR(20) NOT NULL,
  url          VARCHAR(128) NOT NULL,
  summary      VARCHAR(256) NOT NULL,
  package_type VARCHAR(20) NOT NULL default 'parent',
  file_size    BIGINT(20)       NOT NULL,
  release_time  BIGINT(20)        NOT NULL,
  PRIMARY KEY (uid),
  UNIQUE (package_type, version_code)
);

--
-- 转存表中的数据 appinfo
--

INSERT INTO appinfo (uid, version_code, version_name, url, summary, file_size, release_time) VALUES
  (1, 4, 'V1.1', '/version/LoginTest_4.apk', '1.修正错误', 12344, 1387649057933),
  (9, 7, 'v1.2', '/version/LoginTest_7.apk', '1.新增功能\\n2.该版本号是7', 22345, 1387649057933),
  (10, 11, 'v2.0', '/version/release-11.apk', '1.新增园内通知功能\\n2.该版本号为11', 324709, 1387649057933),
  (12, 12, 'v2.1', 'http://kulebao-prod.qiniudn.com/%252F3_0_2%252Fcocobabys.apk', '1.新增学习内容和每日育情\\n2.该版本号为12', 324877, 1387649057933),
  (13, 13, '0002', 'http://kulebao-prod.qiniudn.com/%252F3_0_2%252Fcocobabys.apk', 'PC安装包', 324877, 1387649057933),
  (112, 112, 'v1.0', 'http://kulebao-prod.qiniudn.com/%252F3_0_2%252Fcocobabys.apk', '1.初次创建老师包', 324877, 1388649057933);

UPDATE appinfo set package_type='teacher' where uid=112;
UPDATE appinfo set package_type='pc' where uid=13;

# --- !Downs

DROP TABLE IF EXISTS appinfo;