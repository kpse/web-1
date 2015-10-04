# --- !Ups

CREATE TABLE parentinfo (
  uid          INT(11)      NOT NULL AUTO_INCREMENT,
  name         VARCHAR(20)  NOT NULL,
  parent_id    VARCHAR(40)  NOT NULL,
  relationship VARCHAR(20)  NOT NULL,
  phone        VARCHAR(16)  NOT NULL DEFAULT '',
  gender       INT   NOT NULL DEFAULT 2,
  company      VARCHAR(200) NOT NULL DEFAULT '',
  picurl       VARCHAR(512) NOT NULL DEFAULT '',
  birthday     DATE         NOT NULL DEFAULT '1800-01-01',
  school_id    VARCHAR(20)  NOT NULL,
  member_status       INT      NOT NULL DEFAULT 1,
  status       INT      NOT NULL DEFAULT 1,
  update_at       BIGINT      NOT NULL DEFAULT 0,
  created_at   BIGINT     DEFAULT 0,
  UNIQUE (phone),
  PRIMARY KEY (uid),
  INDEX (SCHOOL_ID,PHONE,PARENT_ID)
);

--
-- 转存表中的数据 parentinfo
--

INSERT INTO parentinfo (uid, name, parent_id, relationship, phone, gender, company, picurl, birthday, school_id) VALUES
  (1, '李毅', '2_93740362_123', '', '13402815317', 1, 'abcdef', '', '1800-01-01', '93740362'),
  (2, '林玄', '2_93740362_456', '', '14880498549', 1, '4455hhyh', '', '1800-01-01', '93740362'),
  (3, '袋鼠', '2_93740362_789', '', '13408654680', 0, '某公司', '', '1999-01-01', '93740362'),
  (6, '老虎', '2_93740362_790', '', '13408654683', 0, '某公司', '', '1800-01-01', '93740362'),
  (4, '大象', '2_93740362_792', '', '13408654681', 0, '某公司', '', '1800-01-01', '93740362'),
  (7, '洁师', '2_93740362_888', '', '13279491366', 0, '某公司', '', '1800-01-01', '93740362'),
  (5, '测试', '2_93740362_999', '', '13333333333', 0, '某公司', '', '1800-01-01', '93740362'),
  (9, 'IOS测试', '2_93740362_000', '', '13227882591', 0, '某公司', '', '1800-01-01', '93740362'),
  (10, '号码重复', '2_93740562_001', '', '11111111111', 0, '某公司', '', '1800-01-01', '93740562'),
  (11, '号码过期', '2_93740562_002', '', '22222222222', 0, '某公司', '', '1800-01-01', '93740562'),
  (117, '未激活学校的家长', '2_93740662_002', '', '22222222223', 0, '某公司', '', '1800-01-01', '93740662'),
  (118, '试用家长', '2_93740362_101', '', '22222222224', 0, '某公司', '', '1800-01-01', '93740362'),
  (119, '软删除家长', '2_93740662_102', '', '22222222225', 0, '某公司', '', '1800-01-01', '93740662'),
  (129, '软删除家长362', '2_93740362_1023', '', '22222222226', 0, '某公司', '', '1800-01-01', '93740362');

update parentinfo set member_status=0 where phone='22222222222';
update parentinfo set member_status=2 where phone='22222222224';
update parentinfo set status=0 where phone in ('22222222225', '22222222226');

# --- !Downs

DROP TABLE IF EXISTS parentinfo;