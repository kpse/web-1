package controllers.V3

import controllers.Secured
import models.V3.{Student, StudentExt}
import models.{ChildInfo, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller
import models.Children.readChildInfo
import models.Children.writeChildInfo


object StudentController extends Controller with Secured {

  def info(kg: Long, id: Long): ChildInfo = ChildInfo(Some(s"1_${kg}_${id}"), "小明", "嘟嘟", "2013-01-01", 1,
    Some("http://suoqin-test.u.qiniudn.com/1D8530120BB9780D0A28F8283E117F13"), 1234, Some("苹果班"), Some(1427817610000L), Some(kg), Some("某个胡同"),
    Some(1), Some(1417817610000L))

  val ext: StudentExt = StudentExt(Some("显示名"), Some("曾用名"), Some("3721"), Some("510122201301010274"), Some("四川双流"), Some("农村"),
    Some("中国"), Some("湖北武汉"), Some("傣族"), Some(1), Some("2015-09-01"), Some("骑马，打仗"), Some("1-2-3322"), Some("这家伙很懒，什么也没留下。"), Some(1), Some("青霉素过敏"))

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(Student.index(kg, from, to, most)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(Student.show(kg, id)))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Student].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Student].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(new SuccessResponse()))
  }
}

/*
* create table IMSInfo.dbo.StudentInfo(
SysNO int identity(1,1), --系统自增编号，1,2,3,4…..
Name nvarchar(20), --姓名
DisplayName nvarchar(20), --显示名称，一般跟姓名一样，遇到同名可靠这个区分，类似昵称
FormerName nvarchar(20), --曾用名，一般不填
Gender int default(0),性别，1男0女
StudentID nvarchar(50),学号，可能有带字母的超长学号
SocialID nvarchar(20),身份证号
ResidencePlace nvarchar(50),户口所在地
ResidenceType varchar(10),户口类型，enum（城镇户口，农村户口）
Nationality nvarchar(20),国籍
OriginPlace nvarchar(20),籍贯
Ethnic nvarchar(20),民族
ClassSysNO int,所属班级编号（班级表的SysNO）
Birthday datetime,生日
StudentType int,学生类型 1日托0全托
Address nvarchar(200),家庭地址
InDate datetime,入园日期
Picture varchar(max),照片（base64  string直存数据库）
Interest nvarchar(500),兴趣爱好
BedNumber nvarchar(20),床号
Memo nvarchar(500),备注
BusStatus int是否乘坐校车,
MedicalHistory nvarchar(2000) 病史
)
*/