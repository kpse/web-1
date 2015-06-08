package controllers.V3

import controllers.Secured
import models.SuccessResponse
import models.json_models.CheckInfo
import models.json_models.CheckingMessage.checkInfoReads
import models.json_models.CheckingMessage.checkInfoWrites
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

case class CardRecordV3(id: Option[Long], check_info: CheckInfo, create_user: Option[Long], memo: Option[String])

object CardRecordController extends Controller with Secured {

  implicit val writeCardRecordV3 = Json.writes[CardRecordV3]
  implicit val readCardRecordV3 = Json.reads[CardRecordV3]

  def index(kg: Long) = IsLoggedIn { u => _ =>
    val time = System.currentTimeMillis
    Ok(Json.toJson(List(CardRecordV3(Some(1), CheckInfo(kg, "1112223334", 1, 1, "https://dn-cocobabys.qbox.me/big_shots.jpg", time), Some(1), Some("没有备注")))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    val time = System.currentTimeMillis
    Ok(Json.toJson(CardRecordV3(Some(id), CheckInfo(kg, "1112223334", 1, 1, "https://dn-cocobabys.qbox.me/big_shots.jpg", time), Some(1), Some("没有备注"))))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[CardRecordV3].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[CardRecordV3].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(new SuccessResponse()))
  }

  def push(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(new SuccessResponse()))
  }
}

/*
* create table TrackingInfo.dbo.CardRecord(
SysNO int identity(1,1),系统编号
EnrollNumber varchar(20),工号（已无使用价值，可弃，以前用于对应打卡人，现应改成卡号cardNumber）
RecordTime datetime,刷卡时间
CreateUserSysNO int,创建者系统编号
Memo nvarchar(500),备注
PicPath varchar(max),实时照片路径
WorkCheck char(1)实时算出的考勤结果，如果不用实时计算的结果，可弃
)


*/

object ErrorCardRecordController extends Controller with Secured {

  implicit val writeCardRecordV3 = Json.writes[CardRecordV3]
  implicit val readCardRecordV3 = Json.reads[CardRecordV3]

  def index(kg: Long) = IsLoggedIn { u => _ =>
    val time = System.currentTimeMillis
    Ok(Json.toJson(List(CardRecordV3(Some(1), CheckInfo(kg, "1112223334", 1, 1, "https://dn-cocobabys.qbox.me/big_shots.jpg", time), Some(1), Some("错卡备注")))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    val time = System.currentTimeMillis
    Ok(Json.toJson(CardRecordV3(Some(id), CheckInfo(kg, "1112223334", 1, 1, "https://dn-cocobabys.qbox.me/big_shots.jpg", time), Some(1), Some("错卡备注"))))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[CardRecordV3].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[CardRecordV3].map {
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