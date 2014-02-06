# --- !Ups

CREATE TABLE news (
  uid       INT(11)      NOT NULL AUTO_INCREMENT,
  school_id VARCHAR(20)  NOT NULL,
  title     VARCHAR(255) NOT NULL,
  content   TEXT         NOT NULL,
  update_at BIGINT         NOT NULL DEFAULT 0,
  published INT          NOT NULL DEFAULT 0,
  status    INT          NOT NULL DEFAULT 1,
  PRIMARY KEY (uid)
);

INSERT INTO news (uid, school_id, title, content, update_at, published) VALUES
  (1, '93740362', '通知1',
   '缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知缴费通知',
   1387353635, 1),
  (2, '93740362', '通知2',
   '学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电学校停电',
   1387353636, 1),
  (3, '93740362', '通知11',
   '测试信息',
   1387353637, 1),
  (4, '93740362', '通知12',
   '测试信息',
   1387353638, 1),
  (5, '93740362', '通知13',
   '测试信息',
   1387353639, 1),
  (6, '93740362', '通知14',
   '测试信息',
   1387353640, 1),
  (7, '93740362', '通知3',
   '恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击恐怖分子袭击',
   1387353641, 0);

# --- !Downs

DROP TABLE IF EXISTS news;