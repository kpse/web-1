package controllers

import play.api.mvc._
import play.api.libs.json._
import models.json_models._
import models.json_models.CheckPhoneResponse
import models.json_models.MobileLogin
import models.json_models.BindingNumber
import models.{AppUpgradeResponse, AppPackage}
import play.api.Logger

object Authentication extends Controller {

  implicit val loginReads = Json.reads[MobileLogin]
  implicit val resultWrites = Json.writes[MobileLoginResponse]


  def login = Action(parse.json) {
    request =>
      request.body.validate[MobileLogin].map {
        case (login) =>
          val result = MobileLoginResponse.handle(login)
          Ok(Json.toJson(result))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  implicit val checkReads = Json.reads[CheckPhone]
  implicit val checkPhoneResultWrites = Json.writes[CheckPhoneResponse]

  def validateNumber = Action(parse.json) {
    request =>
      request.body.validate[CheckPhone].map {
        case (phone) =>
          Ok(Json.toJson(CheckPhoneResponse.handle(phone)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }

  }

  implicit val bindingRead = Json.reads[BindingNumber]
  implicit val bindResultWrites = Json.writes[BindNumberResponse]


  def bindNumber = Action(parse.json) {
    request =>
      request.body.validate[BindingNumber].map {
        case (login) =>
          Logger.info(login.toString)
          val result = BindNumberResponse.handle(login)
          result match {
            case success if success.error_code == 0 =>
              Ok(Json.toJson(success)).withSession("username" -> success.account_name, "token" -> success.access_token)
            case _ =>
              Ok(Json.toJson(result))
          }
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
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

  implicit val read1 = Json.reads[ChangePassword]
  implicit val read2 = Json.reads[ResetPassword]
  implicit val write3 = Json.writes[ChangePasswordResponse]


  def resetPassword = Action(parse.json) {
    request =>
      request.body.validate[ResetPassword].map {
        case (request) =>
          val reset = ChangePasswordResponse.handleReset(request)
          reset match {
            case success if success.error_code == 0 =>
              Ok(Json.toJson(success)).withSession("username" -> request.account_name, "token" -> success.access_token)
            case _ =>
              Ok(Json.toJson(reset))
          }
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def changePassword = Action(parse.json) {
    request =>
      request.body.validate[ChangePassword].map {
        case (request) =>
          val changed = ChangePasswordResponse.handle(request)
          changed match {
            case success if success.error_code == 0 =>
              Ok(Json.toJson(success)).withSession("username" -> request.account_name, "token" -> success.access_token)
            case _ =>
              Ok(Json.toJson(changed))
          }

      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }
}