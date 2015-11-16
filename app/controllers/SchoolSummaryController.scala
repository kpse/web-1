package controllers

import play.api.Logger
import play.api.mvc._
import play.api.libs.json.{JsError, Json}
import models.json_models._
import models.json_models.SchoolIntro._
import models._
import models.School._

object SchoolSummaryController extends Controller with Secured {
  implicit val writes1 = Json.writes[SchoolIntroPreviewResponse]

  implicit val writes3 = Json.writes[SchoolIntroDetail]
  implicit val writes4 = Json.writes[ErrorResponse]
  implicit val writes5 = Json.writes[SuccessResponse]

  private val logger: Logger = Logger(classOf[SchoolIntroDetail])

  def preview(kg: Long, q: Option[String]) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(SchoolIntro.preview(kg, q)))
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
        logger.debug(request.body.toString())
        request.body.validate[SchoolIntro].map {
          case (detail) if SchoolIntro.schoolExists(detail.school_id) =>
            SchoolIntro.updateExists(detail)
            Ok(Json.toJson(new SuccessResponse))
          case _ => NotFound
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }

  def index(q: Option[String]) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(SchoolIntro.index(q)))
  }

  def create = IsOperator(parse.json) {
    u =>
      request =>
        logger.debug(request.body.toString())
        request.body.validate[CreatingSchool].map {
          case (detail) if Employee.phoneExists(detail.principal.phone) =>
            BadRequest(Json.toJson(ErrorResponse(s"校长手机号${detail.principal.phone}已经存在。(phone number already exists)")))
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
      logger.debug(request.body.toString())
      request.body.validate[SchoolConfig].map {
        case (current) if current.config.nonEmpty =>
          current.config.map(Charge.addConfig(kg, _))
          Ok(Json.toJson(new SuccessResponse))
        case _ => Ok(Json.toJson(ErrorResponse("no config added.")))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }
}
