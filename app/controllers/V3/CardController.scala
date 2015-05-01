package controllers.V3

import controllers.Secured
import models.{Employee, Parent, ChildInfo, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller
import models.Children.readChildInfo
import models.Children.writeChildInfo

case class CardV3(id: Option[Long], number: String, origin: String)

object CardController extends Controller with Secured {
  implicit val writeCardV3 = Json.writes[CardV3]
  implicit val readCardV3 = Json.reads[CardV3]

  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(CardV3(Some(1), "321", "123"))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(CardV3(Some(id), "321", "123")))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[CardV3].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[CardV3].map {
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
* --卡集合（用于控制授权使用的卡号）
create table BaseInfo.dbo.CardCollection(
SysNO int identity(1,1),系统编号
CardNumber varchar(500),加密卡号（加盐后生成的md5）
OriginNumber varchar(100)原10位卡号
)


*/