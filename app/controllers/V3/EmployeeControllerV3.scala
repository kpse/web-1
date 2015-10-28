package controllers.V3

import controllers.Secured
import models.V3.EmployeeV3
import models.{ErrorResponse, SuccessResponse}
import play.api.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object EmployeeControllerV3 extends Controller with Secured {
  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(EmployeeV3.index(kg, from, to, most)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    EmployeeV3.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的老师。(No such employee)")))
    }
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    Logger.info(s"employee create: ${request.body}")
    request.body.validate[EmployeeV3].map {
      case (s) if s.ext.isEmpty =>
        BadRequest(Json.toJson(ErrorResponse("必须提供完整的信息。(no ext part)", 2)))
      case (s) if s.basic.uid.nonEmpty || s.id.nonEmpty =>
        BadRequest(Json.toJson(ErrorResponse("有id的情况请用update接口。(use update when you have ID value)", 4)))
      case (s) if s.existsInOtherSchool(kg) =>
        InternalServerError(Json.toJson(ErrorResponse("该号码，员工id，登录名三者至少有一样出现在另一个学校未删除老师的信息里。(the given employee critical info belongs to another school)", 6)))
      case (s) =>
        EmployeeV3.removeDirtyDataIfExists(s.basic)
        Ok(Json.toJson(s.create(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    Logger.info(s"employee update: ${request.body}")
    request.body.validate[EmployeeV3].map {
      case (s) if s.ext.isEmpty =>
        BadRequest(Json.toJson(ErrorResponse("必须提供完整的信息。(no ext part)", 2)))
      case (s) if s.id != s.basic.uid  =>
        BadRequest(Json.toJson(ErrorResponse("内外id不一致。(ids should be consistent)", 3)))
      case (s) if s.id.isEmpty  =>
        BadRequest(Json.toJson(ErrorResponse("没有id无法更新。(no id for update)", 5)))
      case (s) if s.existsInOtherSchool(kg) =>
        InternalServerError(Json.toJson(ErrorResponse("该号码的老师属于另一个学校。(this employee number belongs to another school)", 6)))
      case (s) =>
        Ok(Json.toJson(s.update(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    EmployeeV3.deleteById(kg, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
  
  def ineligibleClasses(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    EmployeeV3.show(kg, id) match {
      case Some(employee) =>
        Ok(Json.toJson( employee.ineligibleClasses))
      case None =>
        NotFound(Json.toJson(ErrorResponse("没有这个老师的信息。(no such employee)")))
    }
  }
}

/*
* create table IMSInfo.dbo.EmployeeInfo(
SysNO int identity(1,1), 系统自增编号
Name nvarchar(20), 姓名
DisplayName nvarchar(20), 显示名
Gender int default(0), 性别
SocialID nvarchar(20), 身份证号
Birthday datetime,生日
Nationality nvarchar(20), 国籍
OriginPlace nvarchar(20), 籍贯
Ethnic nvarchar(20), 民族
Marriage nvarchar(20), 婚否（未婚，已婚），可考虑int的枚举
Education nvarchar(20),学历（初中，中专，高中，大专，本科，硕士）
MobilePhone varchar(20),手机
TelePhone varchar(20),座机
Address nvarchar(200),地址
Picture varchar(max),照片
Memo nvarchar(500),备注

WorkID nvarchar(20),工号
WorkGroupSysNO int,所属工作组的编号（教师，职工，医务室）
InDate datetime,入园日期
WorkStatus nvarchar(20),在职状态 enum（在职，离职）
WorkDuty nvarchar(20), 职务（老师，园长等）
WorkTitle nvarchar(20),职称 enum（初级职称，中级职称，高级职称）
WorkRank nvarchar(20),等级，enum（三级教师，二级教师，一级教师，高级教师，特技教师）
Certification nvarchar(20) 两证情况enum（幼师专业证，幼师资格证，两证齐全）
)


*/