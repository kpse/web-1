package controllers.V3

import controllers.Secured
import models.SuccessResponse
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

case class SmsRecord(id: Option[Long], phone_list: Option[String], content: Option[String], created_at: Option[Long])

object SmsManagementController extends Controller with Secured {

  implicit val writeSmsRecord = Json.writes[SmsRecord]
  implicit val readSmsRecord = Json.reads[SmsRecord]

  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(SmsRecord(Some(1), Some("13991855476,13991855477,13991855478"), Some("短信内容"), Some(System.currentTimeMillis)))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(SmsRecord(Some(id), Some("13991855476,13991855477,13991855478"), Some("短信内容"), Some(System.currentTimeMillis))))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[SmsRecord].map {
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
