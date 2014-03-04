package controllers

import play.api.mvc.{Action, Controller}
import models.Employee
import play.api.libs.json.Json

object EmployeeController extends Controller {

  implicit val write = Json.writes[Employee]

  def index(kg: Long) = Action {
    Ok(Json.toJson(Employee.all(kg)))
  }

  def show(kg: Long, phone: String) = Action {
    Ok(Json.toJson(Employee.show(kg, phone)))
  }
}
