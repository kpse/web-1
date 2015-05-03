package controllers.V3

import controllers.Secured
import models.SuccessResponse
import models.json_models.CheckInfo
import models.json_models.CheckingMessage.checkInfoReads
import models.json_models.CheckingMessage.checkInfoWrites
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

case class WorkCheck(id: Option[Long], user_id: Option[Long], user_type: Option[Int], check_name: Option[String], start_hour: Option[Int],
                     start_minute: Option[Int], end_hour: Option[Int], end_minute: Option[Int], is_same_day: Option[Int],
                      check_date: Option[String], status: Option[Int])

object WorkCheckController extends Controller with Secured {

  implicit val writeWorkCheck = Json.writes[WorkCheck]
  implicit val readWorkCheck = Json.reads[WorkCheck]

  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(WorkCheck(Some(1), Some(1), Some(1), Some("老宋"), Some(8), Some(59), Some(17), Some(0), Some(1), Some("2015-04-01"), Some(1)))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(WorkCheck(Some(1), Some(1), Some(1), Some("老宋"), Some(8), Some(59), Some(17), Some(0), Some(1), Some("2015-04-01"), Some(1))))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[WorkCheck].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[WorkCheck].map {
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
* create table TrackingInfo.dbo.WorkCheckMaster(
SysNO int identity(1,1),系统编号
UserType int,考勤人员类型（1学生2家长3员工）
CheckName nvarchar(20),考勤项目名称
StartHour int,开始时间小时
StartMinute int,开始时间分钟
EndHour int,结束时间小时
EndMinute int,结束时间分钟
IsSameDay int开始结束时间是否在同一天
)

--考勤设置从表
create table TrackingInfo.dbo.WorkCheckItem(
SysNO int identity(1,1),系统编号
CheckSysNO int,所属考勤项目编号
CheckDate datetime,特殊考勤的日期
CheckStatus int本item的启用方式
)
*/
