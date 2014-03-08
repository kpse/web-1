package controllers

import play.api.mvc.{Action, Controller}
import models.Employee
import play.api.libs.json.{JsError, Json}

object EmployeeController extends Controller {

  implicit val write = Json.writes[Employee]

  def index = Action {
    Ok(Json.toJson(Employee.all))
  }

  def show(phone: String) = Action {
    Ok(Json.toJson(Employee.show(phone)))
  }

  implicit val read = Json.reads[Employee]

  def create = Action(parse.json) {
    request =>
      request.body.validate[Employee].map {
        case (employee) =>
          Ok(Json.toJson(Employee.create(employee)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }
}
