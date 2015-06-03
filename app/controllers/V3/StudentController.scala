package controllers.V3

import controllers.Secured
import models.V3.{Student, StudentExt}
import models.{ErrorResponse, Children, ChildInfo, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller
import models.Children.readChildInfo
import models.Children.writeChildInfo


object StudentController extends Controller with Secured {
  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(Student.index(kg, from, to, most)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Student.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的学生。(No such student)")))
    }

  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Student].map {
      case (s) if s.ext.isEmpty =>
        BadRequest(Json.toJson(ErrorResponse("必须提供完整的信息。(no ext part)", 2)))
      case (s) =>
        Ok(Json.toJson(s.create))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Student].map {
      case (s) if s.ext.isEmpty =>
        BadRequest(Json.toJson(ErrorResponse("必须提供完整的信息。(no ext part)", 2)))
      case (s) =>
        Ok(Json.toJson(s.update))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Student.deleteById(kg, id)
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