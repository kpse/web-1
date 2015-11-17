package controllers.V3

import controllers.Secured
import models.V3.CardV3
import models.{ErrorResponse, SuccessResponse}
import play.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object CardController extends Controller with Secured {
  def search(kg: Long, q: String) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(CardV3.search(kg, q)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    CardV3.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的卡。(No such card record)")))
    }
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    Logger.info(s"CardController create: ${request.body}")
    request.body.validate[CardV3].map {
      case (s) if s.originExists =>
        InternalServerError(Json.toJson(ErrorResponse("卡号重复(duplicated card number)", 2)))
      case (s) =>
        Ok(Json.toJson(s.create(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[CardV3].map {
      case (s) =>
        Ok(Json.toJson(s.update(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    CardV3.deleteById(kg, id)
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