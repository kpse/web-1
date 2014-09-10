package controllers

import play.api.mvc.{Action, Controller}
import models._
import play.api.libs.json.Json
import play.Logger
import controllers.helper.JsonLogger._
import models.EmployeePassword
import models.EmployeeResetPassword
import models.ErrorResponse
import models.helper.PasswordHelper

object EmployeeController extends Controller with Secured {

  implicit val write = Json.writes[Employee]
  implicit val write1 = Json.writes[ErrorResponse]
  implicit val write2 = Json.writes[SchoolClass]
  implicit val write3 = Json.writes[SuccessResponse]
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
      Employee.show(phone) match {
        case Some(x) =>
          Ok(Json.toJson(x))
        case None =>
          NotFound("没有找到对应老师。")
      }

  }


  def create = IsOperator(parse.json) {
    u =>
      request =>
        request.body.validate[Employee].map {
          case (employee) if Employee.phoneExists(employee.phone) =>
            BadRequest(loggedJson(ErrorResponse("老师的电话重复，请检查输入。")))
          case (reCreation) if Employee.hasBeenDeleted(reCreation.phone) =>
            Ok(loggedJson(Employee.reCreateByPhone(reCreation)))
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

  def deleteInSchool(kg: Long, phone: String) = IsPrincipal {
    u => _ =>
      Ok(Json.toJson(Employee.deleteInSchool(kg, phone)))
  }

  def createOrUpdateInSchool(kg: Long, phone: String) = IsAuthenticated(parse.json) {
    u =>
      request =>
        Logger.info(request.body.toString())
        val userId = request.session.get("id")
        request.body.validate[Employee].map {
          case (me) if userId.equals(me.id) =>
            Logger.info("employee %s changes himself/herself".format(request.session.get("username")))
            Ok(loggedJson(Employee.update(me))).withSession(request.session + ("phone" -> me.phone) + ("username" -> me.login_name) + ("name" -> me.name))
          case (reCreation) if Employee.hasBeenDeleted(reCreation.phone) =>
            Ok(loggedJson(Employee.reCreateByPhone(reCreation)))
          case (other) if !(userId.equals(other.id) || Employee.isSuperUser(userId.getOrElse(""), kg)) =>
            Unauthorized(loggedJson(ErrorResponse("您无权修改其他老师的信息。")))
          case (phoneDuplicated) if Employee.phoneExists(phoneDuplicated.phone) && !Employee.idExists(phoneDuplicated.id) =>
            BadRequest(loggedJson(ErrorResponse("已有老师使用此电话%s，请检查输入。".format(phoneDuplicated.phone))))
          case (loginNameDuplicated) if Employee.loginNameExists(loginNameDuplicated.login_name) && !Employee.idExists(loginNameDuplicated.id) =>
            BadRequest(loggedJson(ErrorResponse(loginNameDuplicated.login_name + "已占用，建议用学校拼音缩写加数字来组织登录名。")))
          case (existing) if Employee.idExists(existing.id) =>
            Ok(loggedJson(Employee.update(existing)))
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
        Logger.info(request.body.toString())
        request.body.validate[EmployeePassword].map {
          case (employee) if employee.old_password.equalsIgnoreCase(employee.new_password) =>
            BadRequest(loggedJson(ErrorResponse("新旧密码一样。")))
          case (employee) if !Employee.oldPasswordMatch(employee) =>
            BadRequest(loggedJson(ErrorResponse("旧密码错误。")))
          case (employee) if !PasswordHelper.isValid(employee.new_password) =>
            BadRequest(loggedJson(ErrorResponse(PasswordHelper.ErrorMessage)))
          case (employee) =>
            Logger.info(employee.toString)
            Employee.changPassword(kg, phone, employee)
            Ok(loggedJson(new SuccessResponse))
        }.recoverTotal {
          e => BadRequest("Detected error:" + loggedErrorJson(e))
        }
  }

  def showInSchool(kg: Long, phone: String) = IsLoggedIn {
    u => _ =>
      Ok(loggedJson(Employee.show(phone)))
  }

  def check(phone: String) = Action {
    Employee.show(phone).fold(NotFound(Json.toJson(ErrorResponse("我们的系统没有记录您的手机，请重新检查输入。"))))(e =>
      Ok(loggedJson(e)))
  }

  def resetPassword(phone: String) = Action(parse.json) {
    request =>
      Logger.info(request.body.toString())
      request.body.validate[EmployeeResetPassword].map {
        case (password) =>
          Ok(loggedJson(Employee.resetPassword(password)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + loggedErrorJson(e))
      }
  }

  def managedClass(kg: Long, phone: String) = IsAuthenticated {
    username =>
      _ =>
        Employee.show(phone).fold(Ok(loggedJson(List[SchoolClass]())))({
          case (principal) if Employee.isSuperUser(principal.id.getOrElse(""), kg) =>
            Ok(loggedJson(School.allClasses(kg)))
          case teacher =>
            Logger.info(teacher.toString)
            Ok(loggedJson(Employee.managedClass(kg, teacher)))
        })
  }

  def permanentRemove(phone: String) = IsOperator {
    u => _ =>
      Employee.permanentRemove(phone)
      Ok(Json.toJson(new SuccessResponse))
  }
}
