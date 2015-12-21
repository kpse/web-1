package controllers

import models.V4.AgentWithLoginName
import play.api.mvc.{Action, Controller}
import models._
import play.api.libs.json.{JsError, Json}
import play.Logger
import controllers.helper.JsonLogger._
import models.EmployeePassword
import models.Employee.readLoginNameCheck
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
            BadRequest(loggedJson(ErrorResponse("老师的电话重复，请检查输入。", 3)))
          case (employee) if Employee.loginNameExists(employee.login_name) =>
            BadRequest(loggedJson(ErrorResponse(employee.login_name + "已占用，建议用学校拼音缩写加数字来组织登录名。", 4)))
          case (reCreation) if Employee.hasBeenDeleted(reCreation.phone) =>
            Ok(loggedJson(Employee.reCreateByPhone(reCreation)))
          case (employee) =>
            val created: Option[Employee] = Employee.create(employee)
            created match {
              case Some(x) => Ok(loggedJson(x))
              case None => InternalServerError(loggedJson(ErrorResponse("创建教师失败，请与管理员联系。", 5)))
            }
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
      Logger.info(u)
      Employee.show(phone) match {
        case employee if employee.nonEmpty && employee.get.login_name.equalsIgnoreCase(u) =>
          Unauthorized(Json.toJson(ErrorResponse("您要删除自己的账号吗? 请与管理员联系(Cannot delete him/herself).", 3)))
        case _ =>
          Employee.deleteInSchool(kg, phone) match {
            case Some(x) =>
              Ok(Json.toJson(new SuccessResponse))
            case None =>
              BadRequest(Json.toJson(ErrorResponse("删除失败,请与管理员联系(Cannot be deleted).", 2)))
          }
      }

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
          case (reCreation) if reCreation.uid.isEmpty && Employee.hasBeenDeleted(reCreation.phone) =>
            Ok(loggedJson(Employee.reCreateByPhone(reCreation)))
          case (other) if !(userId.equals(other.id) || Employee.isSuperUser(userId.getOrElse(""), kg)) =>
            Unauthorized(loggedJson(ErrorResponse("您无权修改其他老师的信息。(not authorized to change other teacher's information)", 41)))
          case (phoneDuplicated) if Employee.phoneExists(phoneDuplicated.phone) && !Employee.idExists(phoneDuplicated.id) =>
            BadRequest(loggedJson(ErrorResponse("已有老师使用此电话%s，请检查输入。(phone number is occupied)".format(phoneDuplicated.phone), 42)))
          case (loginNameDuplicated) if Employee.loginNameExists(loginNameDuplicated.login_name) && !Employee.idExists(loginNameDuplicated.id) =>
            BadRequest(loggedJson(ErrorResponse(loginNameDuplicated.login_name + "已占用，建议用学校拼音缩写加数字来组织登录名。(login name is occupied)", 43)))
          case (existing) if Employee.idExists(existing.id) =>
            Ok(loggedJson(Employee.update(existing)))
          case (newOne) =>
            val created: Option[Employee] = Employee.create(newOne)
            created match {
              case Some(x) =>
                Ok(loggedJson(created))
              case None =>
                InternalServerError(loggedJson(ErrorResponse("创建老师失败，请联系管理员。(Error in creating employee, please contact admin for detail)", 44)))
            }

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
            BadRequest(loggedJson(ErrorResponse("新旧密码一样。", 11)))
          case (employee) if !Employee.oldPasswordMatch(employee) =>
            BadRequest(loggedJson(ErrorResponse("旧密码错误。", 12)))
          case (employee) if !PasswordHelper.isValid(employee.new_password) =>
            BadRequest(loggedJson(ErrorResponse(PasswordHelper.ErrorMessage, 13)))
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
      Employee.show(phone) match {
        case Some(employee) =>
          Ok(loggedJson(employee))
        case None =>
          NotFound(s"没有找到对应的老师.(No such employee with cellphone number $phone)")
      }
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
        Employee.show(phone).fold(Ok(Json.toJson(List[SchoolClass]())))({
          case (principal) if Employee.isSuperUser(principal.id.getOrElse(""), kg) =>
            Ok(Json.toJson(School.allClasses(kg)))
          case teacher =>
            Ok(Json.toJson(Employee.managedClass(kg, teacher)))
        })
  }

  def permanentRemove(phone: String) = IsOperator {
    u => _ =>
      Employee.permanentRemove(phone)
      Ok(Json.toJson(new SuccessResponse))
  }

  def isGoodToUse(kg: Long) = IsLoggedIn(parse.json) {
    u => request =>
      request.body.validate[LoginNameCheck].map {
        case (employee) if employee.login_name.nonEmpty =>
          Teacher.unapply(employee.login_name) match {
            case Some(me) if employee.employee_id.isDefined && me.id == employee.employee_id =>
              Ok(Json.toJson(SuccessResponse(s"ID与登录名 ${employee.login_name}匹配。(login name matches the employee id)")))
            case Some(me) if me.status == Some(0) =>
              Ok(Json.toJson(SuccessResponse(s"已删除的登录名 ${employee.login_name}，可以重复使用。(deleted login name can be reused)")))
            case Some(me) =>
              Ok(Json.toJson(ErrorResponse(s"已使用的登录名 ${employee.login_name}。(occupied login name)", 51)))
            case agent =>
              AgentWithLoginName.unapply(employee.login_name) match {
                case Some(me) if employee.id.isDefined && me.id == employee.id =>
                  Ok(Json.toJson(SuccessResponse(s"ID与登录名 ${employee.login_name}匹配。(login name matches the agent id)")))
                case Some(me) =>
                  Ok(Json.toJson(ErrorResponse(s"已使用的登录名 ${employee.login_name}。(occupied agent login name)", 52)))
                case _ =>
                  Ok(Json.toJson(SuccessResponse(s"未占用的登录名 ${employee.login_name}.(unoccupied login name)")))
              }
          }
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }

  }
}
