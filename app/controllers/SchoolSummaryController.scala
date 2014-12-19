package controllers

import play.api.mvc._
import play.api.libs.json.{JsError, Json}
import models.json_models._
import models.json_models.SchoolIntro._
import play.Logger
import models._
import models.School._

object SchoolSummaryController extends Controller with Secured {
  implicit val writes1 = Json.writes[SchoolIntroPreviewResponse]

  implicit val writes3 = Json.writes[SchoolIntroDetail]
  implicit val writes4 = Json.writes[ErrorResponse]
  implicit val writes5 = Json.writes[SuccessResponse]

  implicit val read2 = Json.reads[SchoolIntroDetail]
  implicit val read4 = Json.reads[PrincipalOfSchool]
  implicit val read5 = Json.reads[ChargeInfo]
  implicit val read6 = Json.reads[CreatingSchool]

  def preview(kg: Long) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(SchoolIntro.preview(kg)))
  }

  def detail(kg: Long) = IsLoggedIn {
    u => _ =>
      SchoolIntro.detail(kg).fold(NotFound(""))({
        case school =>
          Ok(Json.toJson(SchoolIntroDetail(Some(0), kg, Some(school))))
      })
  }

  def update(kg: Long) = IsLoggedIn(parse.json) {
    u =>
      request =>
        Logger.info(request.body.toString())
        request.body.validate[SchoolIntro].map {
          case (detail) if SchoolIntro.schoolExists(detail.school_id) =>
            SchoolIntro.updateExists(detail)
            Ok(Json.toJson(new SuccessResponse))
          case _ => NotFound
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }

  def index = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(SchoolIntro.index))
  }

  def create = IsOperator(parse.json) {
    u =>
      request =>
        Logger.info(request.body.toString())
        request.body.validate[CreatingSchool].map {
          case (detail) if Employee.phoneExists(detail.phone) =>
            BadRequest(Json.toJson(ErrorResponse("校长手机号已经存在。")))
          case (detail) if SchoolIntro.idExists(detail.school_id) =>
            BadRequest(Json.toJson(ErrorResponse("学校ID已经存在。")))
          case (detail) if SchoolIntro.adminExists(detail.principal.admin_login) =>
            BadRequest(Json.toJson(ErrorResponse("校长登录名已经存在。")))
          case (detail) =>
            Ok(Json.toJson(SchoolIntro.create(detail)))
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }

  def show(kg: Long) = IsLoggedIn {
    u => _ =>
      SchoolIntro.detail(kg).fold(NotFound(""))({
        case school =>
          Ok(Json.toJson(school))
      })
  }

  def delete(kg: Long) = IsOperator {
    u => request =>
      School.delete(kg)
      Ok(Json.toJson(new SuccessResponse))
  }

  def config(kg: Long) = IsAuthenticated {
    u => request =>
      Ok(Json.toJson(School.config(kg)))
  }

  def addConfig(kg: Long) = IsOperator(parse.json) {
    u => request =>
      Logger.info(request.body.toString())
      request.body.validate[SchoolConfig].map {
        case (current) if current.config.nonEmpty =>
          current.config.map(School.addConfig(kg, _))
          Ok(Json.toJson(new SuccessResponse))
        case _ => Ok(Json.toJson(ErrorResponse("no config added.")))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }
}
