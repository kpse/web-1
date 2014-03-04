package controllers

import play.api.mvc.{Action, Controller}
import models.Employee
import play.api.libs.json.Json

object EmployeeController extends Controller {

  implicit val write = Json.writes[Employee]

  def index = Action {
    Ok(Json.toJson(Employee.all))
  }

  def show(phone: String) = Action {
    Ok(Json.toJson(Employee.show(phone)))
  }
}
