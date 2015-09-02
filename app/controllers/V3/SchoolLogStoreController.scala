package controllers.V3

import controllers.Secured
import models.V3.{SchoolLogStore, SchoolDataDefinition}
import models.{ErrorResponse, SuccessResponse}
import org.joda.time.DateTime
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object SchoolLogStoreController extends Controller with Secured {
  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(SchoolLogStore.index(kg, from, to, most)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    SchoolLogStore.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的日志记录。(No such log file record)")))
    }
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[SchoolLogStore].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) =>
        Ok(Json.toJson(s.create(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[SchoolLogStore].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) if SchoolLogStore.show(kg, id).isEmpty =>
        BadRequest(Json.toJson(ErrorResponse(s"没有ID为${id}的日志记录。(No such school log file record)", 4)))
      case (s) =>
        Ok(Json.toJson(s.update(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    SchoolLogStore.deleteById(kg, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}