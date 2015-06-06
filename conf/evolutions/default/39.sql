# --- !Ups
---       case class EmployeeExt(display_name: Option[String], social_id: Option[String], nationality: Option[String], residence_place: Option[String],
---                       ethnos: Option[String], marriage: Option[Int], education: Option[String], fix_line: Option[String], memo: Option[String],
---                        work_id: Option[String], InDate: Option[String], work_status: Option[String], work_title: Option[String], work_rank: Option[String], certification: Option[String])

--- create table IMSInfo.dbo.EmployeeInfo(
--- SysNO int identity(1,1), 系统自增编号
--- Name nvarchar(20), 姓名
--- DisplayName nvarchar(20), 显示名
--- Gender int default(0), 性别
--- SocialID nvarchar(20), 身份证号
--- Birthday datetime,生日
--- Nationality nvarchar(20), 国籍
--- OriginPlace nvarchar(20), 籍贯
--- Ethnic nvarchar(20), 民族
--- Marriage nvarchar(20), 婚否（未婚，已婚），可考虑int的枚举
--- Education nvarchar(20),学历（初中，中专，高中，大专，本科，硕士）
--- MobilePhone varchar(20),手机
--- TelePhone varchar(20),座机
--- Address nvarchar(200),地址
--- Picture varchar(max),照片
--- Memo nvarchar(500),备注
---
--- WorkID nvarchar(20),工号
--- WorkGroupSysNO int,所属工作组的编号（教师，职工，医务室）
--- InDate datetime,入园日期
--- WorkStatus nvarchar(20),在职状态 enum（在职，离职）
--- WorkDuty nvarchar(20), 职务（老师，园长等）
--- WorkTitle nvarchar(20),职称 enum（初级职称，中级职称，高级职称）
--- WorkRank nvarchar(20),等级，enum（三级教师，二级教师，一级教师，高级教师，特技教师）
--- Certification nvarchar(20) 两证情况enum（幼师专业证，幼师资格证，两证齐全）


CREATE TABLE employeeext (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  base_id   INT(11) NOT NULL,
  display_name VARCHAR(20),
  social_id VARCHAR(20),
  nationality VARCHAR(20),
  original_place VARCHAR(20),
  ethnos VARCHAR(20),
  marriage VARCHAR(20),
  education VARCHAR(20),
  fixed_line VARCHAR(20),
  address VARCHAR(200),
  memo VARCHAR(512),
  work_id VARCHAR(20),
  work_group INT,
  in_date BIGINT,
  work_status VARCHAR(20),
  work_duty VARCHAR(20),
  work_title VARCHAR(20),
  work_rank VARCHAR(20),
  certification VARCHAR(20),
  PRIMARY KEY (uid),
  UNIQUE (base_id)
);

INSERT INTO employeeext (base_id, display_name, social_id, nationality, original_place, ethnos, marriage, education,
fixed_line, address, memo, work_id, work_group, in_date, work_status, work_duty, work_title, work_rank, certification) VALUES
(1, '小霸王', '510122195812310275', '中国', '双流华阳', '汉', '已婚', '本科', '028-88887777', '魔兽森林', '备注就是懒得很', '2123123', 1, 1387649057933, '在职', '园长', '高级职称', '三级教师', '两证齐全');

# --- !Downs

DROP TABLE IF EXISTS employeeext;