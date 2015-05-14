--
-- 表的结构 relationmap
--
# --- !Ups

CREATE TABLE relationmap (
  uid int(11) NOT NULL AUTO_INCREMENT,
  child_id varchar(40) NOT NULL,
  parent_id varchar(40) NOT NULL,
  card_num VARCHAR(20)  NOT NULL DEFAULT '',
  relationship VARCHAR(20)  NOT NULL,
  reference_id VARCHAR(40),
  status       TINYINT      NOT NULL DEFAULT 1,
  UNIQUE (card_num),
  UNIQUE (reference_id),
  UNIQUE (parent_id,child_id),
  PRIMARY KEY (uid)
);

--
-- 转存表中的数据 relationmap
--

INSERT INTO relationmap (uid, child_id, parent_id, card_num, relationship, reference_id) VALUES
(1, '1_1391836223533', '2_93740362_789', '0001234567', '妈妈', '1_1391836223533' ),
(2, '1_93740362_456', '2_93740362_456', '0001234568', '妈妈',  '1_93740362_456'  ),
(3, '1_93740362_9982', '2_93740362_790', '0001234569', '妈妈', '1_93740362_9982' ),
(4, '1_93740362_778', '2_93740362_792', '0001234570', '妈妈',  '1_93740362_778' ),
(5, '1_93740362_374', '2_93740362_888', '0001234580', '妈妈',  '1_93740362_374' ),
(6, '1_93740362_374', '2_93740362_000', '2323211232', '舅舅',  '2_93740362_374' ),
(7, '1_93740362_9982', '2_93740362_792', '2323211233', '舅舅', '2_93740362_9982' ),
(116, '1_93740662_374', '2_93740662_002', '0002234582', '舅舅',  '3_93740662_374');

INSERT INTO relationmap (uid, child_id, parent_id, card_num, relationship, reference_id, status) VALUES
(119, '1_1391836223533', '2_93740362_790', '0091234567', '叔叔', '1_1391836223533_2', 0);

# --- !Downs

DROP TABLE IF EXISTS relationmap;