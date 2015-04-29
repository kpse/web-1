# --- !Ups

CREATE TABLE schoolinfo (
  uid         INT(11)     NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  province    VARCHAR(20) NOT NULL,
  city        VARCHAR(20) NOT NULL,
  address     TEXT,
  token     VARCHAR(128) NOT NULL,
  name        VARCHAR(20) NOT NULL,
  full_name        VARCHAR(128) NOT NULL,
  description TEXT,
  phone       VARCHAR(16) NOT NULL,
  logo_url    VARCHAR(256),
  update_at   BIGINT     NOT NULL,
  created_at   BIGINT     DEFAULT 0,
  PRIMARY KEY (uid),
  UNIQUE (full_name),
  UNIQUE (school_id)
);

INSERT INTO schoolinfo (uid, school_id, province, city, full_name, description, logo_url, phone, update_at, token, name) VALUES
  (1, '93740362', '四川省', '成都', '成都市第三军区幼儿园', '\n李刚牌土豪幼儿园，成立时间超过100年，其特点有：\n1.价格超贵\n2.硬件超好\n3.教师超屌\n4.绝不打折\n5.入园超难\n6.......\n.......\n.......\n.......\n.......\n\n',
   'http://www.jslfgz.com.cn/UploadFiles/xxgl/2013/4/201342395834.jpg', '13991855476', 1387649057933, '123', '第三军区幼儿园'),
  (2, '93740562', '陕西省', '西安', '西安市高新一幼', '\n苦逼幼儿园，成立时间超过100年，其特点有：\n1.价格超贵\n2.硬件超好\n3.教师超屌\n4.绝不打折\n5.入园超难\n6.......\n.......\n.......\n.......\n.......\n\n',
   'http://www.houstonisd.org/cms/lib2/TX01001591/Centricity/Domain/16137/crestgif.gif', '13291855476', 1387649057933, '124', '高新一幼'),
  (3, '93740662', '陕西省', '西安', '未激活学校', '\n苦逼幼儿园，成立时间超过100年，其特点有：\n1.价格超贵\n2.硬件超好\n3.教师超屌\n4.绝不打折\n5.入园超难\n6.......\n.......\n.......\n.......\n.......\n\n',
   'http://www.houstonisd.org/cms/lib2/TX01001591/Centricity/Domain/16137/crestgif.gif', '13291855476', 1387649057933, '125', '未激活');

# --- !Downs

DROP TABLE IF EXISTS schoolinfo;
