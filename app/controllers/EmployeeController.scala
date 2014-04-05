package controllers

import play.api.mvc.{Action, Controller}
import models.{EmployeeResetPassword, ErrorResponse, EmployeePassword, Employee}
import play.api.libs.json.{JsError, Json}
import play.Logger
import helper.JsonLogger._
import play.cache.Cache

object EmployeeController extends Controller with Secured {

  implicit val write = Json.writes[Employee]
  implicit val write1 = Json.writes[ErrorResponse]
  implicit val read1 = Json.reads[Employee]
  implicit val read2 = Json.reads[EmployeePassword]
  implicit val read3 = Json.reads[EmployeeResetPassword]

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
            BadRequest(loggedJson(ErrorResponse("老师的电话重复，请检查输入。")))
          case (employee) if Employee.loginNameExists(employee.login_name) =>
            BadRequest(loggedJson(ErrorResponse(employee.login_name + "已占用，建议用学校拼音缩写加数字来组织登录名。")))
          case (employee) =>
            Ok(loggedJson(Employee.create(employee)))
        }.recoverTotal {
          e => BadRequest("Detected error:" + loggedErrorJson(e))
        }
  }

  def indexInSchool(kg: Long, phone: Option[String]) = IsLoggedIn {
    u =>
      _ =>
        Ok(Json.toJson(Employee.allInSchool(kg, phone)))
  }

  def deleteInSchool(kg: Long, phone: String) = IsOperator {
    u => _ =>
      Ok(Json.toJson(Employee.deleteInSchool(kg, phone)))
  }

  def createOrUpdateInSchool(kg: Long, phone: String) = IsLoggedIn(parse.json) {
    u =>
      request =>
        Logger.info(request.body.toString)
        request.body.validate[Employee].map {
          case (existing) if Employee.idExists(existing.id) =>
            Ok(loggedJson(Employee.update(existing)))
          case (employee) if Employee.phoneExists(employee.phone) && employee.id.isEmpty =>
            BadRequest(loggedJson(ErrorResponse("老师的电话重复，请检查输入。")))
          case (employee) if Employee.loginNameExists(employee.login_name) && employee.id.isEmpty =>
            BadRequest(loggedJson(ErrorResponse(employee.login_name + "已占用，建议用学校拼音缩写加数字来组织登录名。")))
          case (newOne) =>
            Ok(loggedJson(Employee.create(newOne)))
        }.recoverTotal {
          e => BadRequest("Detected error:" + loggedErrorJson(e))
        }
  }

  def createPrincipal(kg: Long) = IsOperator(parse.json) {
    u =>
      request =>
        request.body.validate[Employee].map {
          case (employee) =>
            Ok(loggedJson(Employee.createPrincipal(employee)))
        }.recoverTotal {
          e => BadRequest("Detected error:" + loggedErrorJson(e))
        }
  }

  def updateOrCreate(phone: String) = createOrUpdateInSchool(0, phone)

  def changePassword(kg: Long, phone: String) = IsLoggedIn(parse.json) {
    u =>
      request =>
        Logger.info(request.body.toString)
        request.body.validate[EmployeePassword].map {
          case (employee) if !Employee.oldPasswordMatch(employee) =>
            BadRequest(loggedJson(new ErrorResponse("旧密码错误。")))
          case (employee) =>
            Logger.info(employee.toString)
            Ok(loggedJson(Employee.changPassword(kg, phone, employee)))
        }.recoverTotal {
          e => BadRequest("Detected error:" + loggedErrorJson(e))
        }
  }

  def showInSchool(kg: Long, phone: String) = IsLoggedIn {
    u => _ =>
      Ok(loggedJson(Employee.show(phone)))
  }

  def check(phone: String) = Action {
    Employee.show(phone).map {
      e =>
        Ok(loggedJson(e))
    }.getOrElse(NotFound(Json.toJson(new ErrorResponse("我们的系统没有记录您的手机，请重新检查输入。"))))
  }

  def resetPassword(phone: String) = Action(parse.json) {
    request =>
      Logger.info(request.body.toString())
      request.body.validate[EmployeeResetPassword].map {
        case (password) if Employee.isMatched(password) =>
          Cache.remove(phone)
          Ok(loggedJson(Employee.resetPassword(password)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + loggedErrorJson(e))
      }
  }
}
