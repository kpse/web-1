package controllers.V3

import controllers.Secured
import models.SuccessResponse
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

case class MultiChannel(id: Option[Long], name: Option[String], class_id: Option[Int])

object MultiChannelController extends Controller with Secured {

  implicit val writeMultiChannel = Json.writes[MultiChannel]
  implicit val readMultiChannel = Json.reads[MultiChannel]

  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(MultiChannel(Some(1), Some("通道A"), Some(1)))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(MultiChannel(Some(id), Some("通道A"), Some(1))))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[MultiChannel].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[MultiChannel].map {
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
*
--多路语音列表
create table BaseInfo.dbo.MultiChannelInfo(
SysNO int identity(1,1),系统编号
ChannelNumber varchar(5),多路语音通道号
ClassSysNO int班级系统编号
)

*/
