package controllers

import play.api.mvc.{Action, Controller}
import models.{EmployeePassword, Employee}
import play.api.libs.json.{JsError, Json}
import play.Logger

object EmployeeController extends Controller {

  implicit val write = Json.writes[Employee]
  implicit val read1 = Json.reads[Employee]
  implicit val read2 = Json.reads[EmployeePassword]

  def index = Action {
    Ok(Json.toJson(Employee.all))
  }

  def show(phone: String) = Action {
    Ok(Json.toJson(Employee.show(phone)))
  }



  def create = Action(parse.json) {
    request =>
      request.body.validate[Employee].map {
        case (employee) =>
          Ok(Json.toJson(Employee.create(employee)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def indexInSchool(kg: Long) = Action {
    Ok(Json.toJson(Employee.allInSchool(kg)))
  }

  def deleteInSchool(kg: Long, phone: String) = Action {
    Ok(Json.toJson(Employee.deleteInSchool(kg, phone)))
  }

  def createOrUpdateInSchool(kg: Long, phone: String) = Action(parse.json) {
    request =>
      request.body.validate[Employee].map {
        case (existing) if existing.id.nonEmpty =>
          Ok(Json.toJson(Employee.update(existing)))
        case (newOne) =>
          Ok(Json.toJson(Employee.create(newOne)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def createPrincipal(kg: Long) = Action(parse.json) {
    request =>
      request.body.validate[Employee].map {
        case (employee) =>
          Ok(Json.toJson(Employee.createPrincipal(employee)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def updateOrCreate(phone: String) = createOrUpdateInSchool(0, phone)

  def changePassword(kg: Long, phone: String) = Action(parse.json) {
    request =>
      request.body.validate[EmployeePassword].map {
        case (employee) =>
          Logger.info(employee.toString)
          Ok(Json.toJson(Employee.changPassword(kg, phone, employee)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }
}
