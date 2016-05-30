package controllers.V8

import controllers.Secured
import models.V8.IMKeyword
import models.V8.IMKeyword.readIMKeyword
import models.V8.IMKeyword.writeIMKeyword
import models.{ErrorResponse, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object IMKeywordController extends Controller with Secured {

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(IMKeyword.index(kg, from, to, most)))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[IMKeyword].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) if !s.exists(kg) =>
        Ok(Json.toJson(s.create(kg)))
      case (s) =>
        Ok(Json.toJson(new SuccessResponse("该关键字已存在。(already exists)")))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[IMKeyword].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.update(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    IMKeyword.deleteById(kg, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}
