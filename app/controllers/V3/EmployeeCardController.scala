package controllers.V3

import controllers.Secured
import models.{SuccessResponse, ErrorResponse}
import play.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

case class EmployeeCard(id: Option[Long], school_id: Long, employee_id: Long, card: String, updated_at: Option[Long]) {
  def create = EmployeeCard(Some(1), school_id, employee_id, card, Some(System.currentTimeMillis()))

  def update = EmployeeCard(id, school_id, employee_id, card, Some(System.currentTimeMillis()))
}

object EmployeeCardController extends Controller with Secured {

  implicit val readEmployeeCard = Json.reads[EmployeeCard]
  implicit val writeEmployeeCard = Json.writes[EmployeeCard]

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(EmployeeCard(Some(1), kg, 1, "1234567890", Some(System.currentTimeMillis())))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(EmployeeCard(Some(1), kg, id, "1234567890", Some(System.currentTimeMillis())))))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    Logger.info(s"employee card create: ${request.body}")
    request.body.validate[EmployeeCard].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) =>
        Ok(Json.toJson(s.create))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    Logger.info(s"employee update: ${request.body}")
    request.body.validate[EmployeeCard].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.update))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(new SuccessResponse()))
  }
}