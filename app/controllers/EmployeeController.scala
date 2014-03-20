package controllers

import play.api.mvc.{Action, Controller}
import models.{ErrorResponse, EmployeePassword, Employee}
import play.api.libs.json.{JsError, Json}
import play.Logger

object EmployeeController extends Controller with Secured {

  implicit val write = Json.writes[Employee]
  implicit val write1 = Json.writes[ErrorResponse]
  implicit val read1 = Json.reads[Employee]
  implicit val read2 = Json.reads[EmployeePassword]

  def index = IsOperator {
    u =>
      _ =>
        Ok(Json.toJson(Employee.all))
  }

  def show(phone: String) = IsAuthenticated {
    u => _ =>
      Ok(Json.toJson(Employee.show(phone)))
  }


  def create = IsOperator(parse.json) {
    u =>
      request =>
        request.body.validate[Employee].map {
          case (employee) if Employee.phoneExists(employee.phone) =>
            BadRequest(Json.toJson(ErrorResponse("老师的电话重复，请检查输入。")))
          case (employee) if Employee.loginNameExists(employee.login_name) =>
            BadRequest(Json.toJson(ErrorResponse(employee.login_name + "已占用，建议用学校拼音缩写加数字来组织登录名。")))
          case (employee) =>
            Ok(Json.toJson(Employee.create(employee)))
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }

  def indexInSchool(kg: Long) = IsLoggedIn {
    u =>
      _ =>
        Ok(Json.toJson(Employee.allInSchool(kg)))
  }

  def deleteInSchool(kg: Long, phone: String) = IsOperator {
    u => _ =>
      Ok(Json.toJson(Employee.deleteInSchool(kg, phone)))
  }

  def createOrUpdateInSchool(kg: Long, phone: String) = IsLoggedIn(parse.json) {
    u =>
      request =>
        request.body.validate[Employee].map {
          case (employee) if Employee.phoneExists(employee.phone) && employee.id.isEmpty =>
            BadRequest(Json.toJson(ErrorResponse("老师的电话重复，请检查输入。")))
          case (employee) if Employee.loginNameExists(employee.login_name) && employee.id.isEmpty =>
            BadRequest(Json.toJson(ErrorResponse(employee.login_name + "已占用，建议用学校拼音缩写加数字来组织登录名。")))
          case (existing) if existing.id.nonEmpty =>
            Ok(Json.toJson(Employee.update(existing)))
          case (newOne) =>
            Ok(Json.toJson(Employee.create(newOne)))
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }

  def createPrincipal(kg: Long) = IsOperator(parse.json) {
    u =>
      request =>
        request.body.validate[Employee].map {
          case (employee) =>
            Ok(Json.toJson(Employee.createPrincipal(employee)))
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }

  def updateOrCreate(phone: String) = createOrUpdateInSchool(0, phone)

  def changePassword(kg: Long, phone: String) = IsLoggedIn(parse.json) {
    u =>
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
