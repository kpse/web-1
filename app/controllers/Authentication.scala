package controllers

import controllers.helper.JsonLogger._
import models.{AppUpgradeResponse, ErrorResponse, _}
import models.helper.PasswordHelper
import models.json_models.{BindingNumber, ChangePassword, CheckPhone, CheckPhoneResponse, MobileLogin, ResetPassword, _}
import models.BindingV1.writeBindingHistory
import play.api.Logger
import play.api.libs.json._
import play.api.mvc.{SimpleResult, _}

object Authentication extends Controller with Secured {

  implicit val r1 = Json.reads[ChangePassword]
  implicit val r2 = Json.reads[ResetPassword]
  implicit val r3 = Json.reads[BindingNumber]
  implicit val r4 = Json.reads[MobileLogin]
  implicit val r5 = Json.reads[CheckPhone]

  implicit val w1 = Json.writes[MobileLoginResponse]
  implicit val w2 = Json.writes[CheckPhoneResponse]
  implicit val w3 = Json.writes[BindNumberResponse]
  implicit val w12 = Json.writes[BindingResponseV1]
  implicit val w4 = Json.writes[ChangePasswordResponse]
  implicit val w5 = Json.writes[MobileLogin]
  implicit val w6 = Json.writes[CheckPhone]
  implicit val w7 = Json.writes[BindingNumber]
  implicit val w8 = Json.writes[ChangePassword]
  implicit val w9 = Json.writes[ResetPassword]
  implicit val w10 = Json.writes[Employee]
  implicit val w11 = Json.writes[ErrorResponse]

  def login = Action(parse.json) {
    request =>
      request.body.validate[MobileLogin].map {
        case (login) =>
          loggedJson(login)
          val result = LoginCheck(login)
          Ok(loggedJson(result))
      }.recoverTotal {
        e => BadRequest("Detected error:" + loggedErrorJson(e))
      }
  }


  def validateNumber = Action(parse.json) {
    request =>
      request.body.validate[CheckPhone].map {
        case (phone) =>
          loggedJson(phone)
          Ok(loggedJson(Check(phone)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + loggedErrorJson(e))
      }

  }


  def bindNumber = Action(parse.json) {
    request =>
      request.body.validate[BindingNumber].map {
        case (login) =>
          Logger.info(login.toString)
          val result = Binding(login)
          result match {
            case success if success.error_code == 0 =>
              Ok(loggedJson(success)).withSession("username" -> success.account_name, "token" -> success.access_token)
            case _ =>
              Logger.info("versioncode in request : (%s)".format(request.headers.get("versioncode")))
              Ok(loggedJson(result)).withNewSession
          }
      }.recoverTotal {
        e => BadRequest("Detected error:" + loggedErrorJson(e))
      }
  }

  implicit val write1 = Json.writes[AppUpgradeResponse]

  def parentHasUpdate(version: Long) = Action {
    //    {"summary":"测试版本","error_code":"0","url":"http://cocobabys.oss-cn-hangzhou.aliyuncs.com/app_release/release_2.apk","size":500000,"version":"V1.1"}
    hasPackageBeyond(version)
  }

  def teacherHasUpdate(version: Long) = Action {
    //    {"summary":"测试版本","error_code":"0","url":"http://cocobabys.oss-cn-hangzhou.aliyuncs.com/app_release/release_2.apk","size":500000,"version":"V1.1"}
    hasPackageBeyond(version, Some("teacher"))
  }


  def hasPackageBeyond(version: Long, pkgType: Option[String] = None): SimpleResult = {
    AppPackage.latest(pkgType).fold(BadRequest(""))({
      case pkg if version < pkg.version_code =>
        Logger.info("latest version code = %d".format(pkg.version_code))
        Ok(Json.toJson(AppPackage.response(pkg)))
      case _ => Ok(Json.toJson(AppPackage.noUpdate))
    })
  }

  def resetPassword = Action(parse.json) {
    request =>
      request.body.validate[ResetPassword].map {
        case (r) =>
          loggedJson(r)
          val reset = ChangePasswordResponse.handleReset(r)
          reset match {
            case success if success.error_code == 0 =>
              Ok(loggedJson(success)).withSession("username" -> r.account_name, "token" -> success.access_token)
            case _ =>
              Ok(loggedJson(reset)).withNewSession
          }
      }.recoverTotal {
        e => BadRequest("Detected error:" + loggedErrorJson(e))
      }
  }

  def changePassword = Action(parse.json) {
    request =>
      request.body.validate[ChangePassword].map {
        case (tooSimple) if !PasswordHelper.isValid(tooSimple.new_password) =>
          BadRequest(loggedJson(ErrorResponse(PasswordHelper.ErrorMessage)))
        case (r) =>
          loggedJson(r)
          val changed = ChangePasswordResponse.handle(r)
          changed match {
            case success if success.error_code == 0 =>
              Ok(loggedJson(success)).withSession("username" -> r.account_name, "token" -> success.access_token)
            case _ =>
              Ok(loggedJson(changed)).withNewSession
          }

      }.recoverTotal {
        e => BadRequest("Detected error:" + loggedErrorJson(e))
      }
  }

  def employeeLogin() = Action(parse.json) {
    request =>
      request.body.validate[MobileLogin].map {
        case (teacherLogin) =>
          loggedJson(teacherLogin)
          Employee.authenticate(teacherLogin.account_name, teacherLogin.password).fold(Forbidden("无效的用户名或密码。").withNewSession)({
            case (employee) => Ok(loggedJson(employee)).withSession("username" -> employee.login_name, "phone" -> employee.phone, "name" -> employee.name, "id" -> employee.id.getOrElse(""))
          })
      }.recoverTotal {
        e => BadRequest("Detected error:" + loggedErrorJson(e))
      }
  }

  def employeeResetPassword() = Action(parse.json) {
    request =>
      request.body.validate[ResetPassword].map {
        case (nonExists) if !Employee.loginNameExists(nonExists.account_name) =>
          BadRequest(loggedJson(ErrorResponse("不存在登录名为%s的老师。(Employee not exist)".format(nonExists.account_name))))
        case (nonExists) if !PasswordHelper.isValid(nonExists.new_password) =>
          BadRequest(loggedJson(ErrorResponse(PasswordHelper.ErrorMessage)))
        case (r) =>
          loggedJson(r)
          ChangePasswordResponse.handleEmployeeReset(r) match {
            case success if success.error_code == 0 =>
              Employee.authenticate(r.account_name, r.new_password).fold(Ok(loggedJson(success)).withNewSession)({
                case employee =>
                  Ok(loggedJson(success)).withSession("username" -> employee.login_name, "phone" -> employee.phone, "name" -> employee.name, "id" -> employee.id.getOrElse(""))
              })
            case other => Ok(loggedJson(other)).withNewSession
          }
      }.recoverTotal {
        e => BadRequest("Detected error:" + loggedErrorJson(e))
      }
  }

  def bindNumberV1() = Action(parse.json) {
    request =>
      request.body.validate[BindingNumber].map {
        case (login) =>
          Logger.info(login.toString)

          BindingV1.record(login, request.headers.get("versioncode").getOrElse("unknown"))
          val result = BindingV1(login)
          result match {
            case success if success.error_code == 0 =>
              Ok(loggedJson(success)).withSession("username" -> success.account_name, "token" -> success.access_token)
            case _ =>
              Logger.info("versioncode in request : (%s)".format(request.headers.get("versioncode")))
              Ok(loggedJson(result)).withNewSession
          }
      }.recoverTotal {
        e => BadRequest("Detected error:" + loggedErrorJson(e))
      }
  }

  def adminReset(phone: String) = IsOperator(parse.json) {
    u => request =>
      request.body.validate[ResetPassword].map {
        case (r) if Parent.phoneSearch(r.account_name).nonEmpty =>
          loggedJson(r)
          ChangePasswordResponse.prepareCache(r.account_name, r.authcode)
          Ok(loggedJson(ChangePasswordResponse.handleReset(r)))
        case (r) =>
          Ok(loggedJson(ErrorResponse("没有找到对应号码的家长(Parent with such phone number is not found).")))
      }.recoverTotal {
        e => BadRequest("Detected error:" + loggedErrorJson(e))
      }
  }

  def bindingHistory(phone: String) = IsOperator {
    u => request =>
      BindingV1.history(phone) match {
        case Some(x) =>
          Ok(Json.toJson(x))
        case None =>
          NotFound(s"No binding record for phone $phone.")
      }
  }

}