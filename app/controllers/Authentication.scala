package controllers

import play.api.mvc._
import play.api.libs.json._
import models.json_models._
import models.json_models.CheckPhoneResponse
import models.json_models.MobileLogin
import models.json_models.BindingNumber
import models.{AppUpgradeResponse, AppPackage}
import play.api.Logger
import helper.JsonLogger._

object Authentication extends Controller {

  implicit val r1 = Json.reads[ChangePassword]
  implicit val r2 = Json.reads[ResetPassword]
  implicit val r3 = Json.reads[BindingNumber]
  implicit val r4 = Json.reads[MobileLogin]
  implicit val r5 = Json.reads[CheckPhone]

  implicit val w1 = Json.writes[MobileLoginResponse]
  implicit val w2 = Json.writes[CheckPhoneResponse]
  implicit val w3 = Json.writes[BindNumberResponse]
  implicit val w4 = Json.writes[ChangePasswordResponse]

  def login = Action(parse.json) {
    request =>
      request.body.validate[MobileLogin].map {
        case (login) =>
          val result = MobileLoginResponse.handle(login)
          Ok(loggedJson(result))
      }.recoverTotal {
        e => BadRequest("Detected error:" + loggedErrorJson(e))
      }
  }


  def validateNumber = Action(parse.json) {
    request =>
      request.body.validate[CheckPhone].map {
        case (phone) =>
          Ok(loggedJson(CheckPhoneResponse.handle(phone)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + loggedErrorJson(e))
      }

  }


  def bindNumber = Action(parse.json) {
    request =>
      request.body.validate[BindingNumber].map {
        case (login) =>
          Logger.info(login.toString)
          val result = BindNumberResponse.handle(login)
          result match {
            case success if success.error_code == 0 =>
              Ok(loggedJson(success)).withSession("username" -> success.account_name, "token" -> success.access_token)
            case _ =>
              Ok(loggedJson(result))
          }
      }.recoverTotal {
        e => BadRequest("Detected error:" + loggedErrorJson(e))
      }
  }

  implicit val write1 = Json.writes[AppUpgradeResponse]

  def app(version: Long) = Action {
    //    {"summary":"测试版本","error_code":"0","url":"http://cocobabys.oss-cn-hangzhou.aliyuncs.com/app_release/release_2.apk","size":500000,"version":"V1.1"}
    AppPackage.latest map {
      case pkg if version < pkg.version_code =>
        Logger.info("latest version code = %d".format(pkg.version_code))
        Ok(Json.toJson(AppPackage.response(pkg)))
      case _ => Ok(Json.toJson(AppPackage.noUpdate))
    } getOrElse BadRequest
  }


  def resetPassword = Action(parse.json) {
    request =>
      request.body.validate[ResetPassword].map {
        case (request) =>
          val reset = ChangePasswordResponse.handleReset(request)
          reset match {
            case success if success.error_code == 0 =>
              Ok(loggedJson(success)).withSession("username" -> request.account_name, "token" -> success.access_token)
            case _ =>
              Ok(loggedJson(reset))
          }
      }.recoverTotal {
        e => BadRequest("Detected error:" + loggedErrorJson(e))
      }
  }

  def changePassword = Action(parse.json) {
    request =>
      request.body.validate[ChangePassword].map {
        case (request) =>
          val changed = ChangePasswordResponse.handle(request)
          changed match {
            case success if success.error_code == 0 =>
              Ok(loggedJson(success)).withSession("username" -> request.account_name, "token" -> success.access_token)
            case _ =>
              Ok(loggedJson(changed))
          }

      }.recoverTotal {
        e => BadRequest("Detected error:" + loggedErrorJson(e))
      }
  }
}