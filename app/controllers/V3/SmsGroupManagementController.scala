package controllers.V3

import controllers.Secured
import controllers.V3.CardController._
import models.{ErrorResponse, SuccessResponse}
import models.V3.{CardV3, SmsGroup, SmsGroupMember}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller




object SmsGroupManagementController extends Controller with Secured {

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(SmsGroup.index(kg, from, to, most)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    SmsGroup.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的短信分组。(No such sms group)")))
    }
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[SmsGroup].map {
      case (s) =>
        Ok(Json.toJson(s.create(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[SmsGroup].map {
      case (s) =>
        Ok(Json.toJson(s.update(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    SmsGroup.deleteById(kg, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}

/*
* create table TrackingInfo.dbo.MessageSendBox(
SysNO int identity(1,1),系统编号
MobilePhoneList varchar(max),手机号集合
MessageContent nvarchar(max),短信内容
CreateTime datetime创建时间
)

--短信自定义分组主表
create table TrackingInfo.dbo.MobileGroupMaster(
SysNO int identity(1,1),系统编号
GroupName nvarchar(20)分组名称
)

--短信自定义分组从表
create table TrackingInfo.dbo.MobileGroupItem(
SysNO int identity(1,1),系统编号
GroupSysNO int,分组编号
UserSysNO int,人员编号
UserType int,人员类别
MobilePhone varchar(20),手机号
)
--环境设置
create table TrackingInfo.dbo.BaseSetting(
SysNO int identity(1,1),系统编号
SettingKey varchar(100),    key
SettingValue varchar(500),  value
Memo nvarchar(500)备注
)
*/
