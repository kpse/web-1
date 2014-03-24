package controllers

import play.api.mvc._
import play.api.libs.json.{JsError, Json}
import models.json_models._
import play.Logger
import models.json_models.SchoolIntroPreviewResponse
import models.json_models.SchoolIntroDetail
import models.ErrorResponse

object SchoolSummaryController extends Controller with Secured {
  implicit val writes1 = Json.writes[SchoolIntroPreviewResponse]
  implicit val writes2 = Json.writes[SchoolIntro]
  implicit val writes3 = Json.writes[SchoolIntroDetail]
  implicit val writes4 = Json.writes[ErrorResponse]
  implicit val read1 = Json.reads[SchoolIntro]
  implicit val read2 = Json.reads[SchoolIntroDetail]
  implicit val read3 = Json.reads[CreatingSchool]

  def preview(kg: Long) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(SchoolIntro.preview(kg)))
  }

  def detail(kg: Long) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(SchoolIntro.detail(kg)))
  }

  def update(kg: Long) = IsLoggedIn(parse.json) {
    u =>
      request =>
        Logger.info(request.body.toString())
        request.body.validate[SchoolIntro].map {
          case (detail) =>
            Ok(Json.toJson(SchoolIntro.updateOrCreate(detail)))
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
          case (detail) if !SchoolIntro.idExists(detail.school_id) && !SchoolIntro.adminExists(detail.admin_login) =>
            Ok(Json.toJson(SchoolIntro.adminCreate(detail)))
          case (detail) if SchoolIntro.idExists(detail.school_id)  =>
            BadRequest(Json.toJson(new ErrorResponse("学校ID已经存在。")))
          case (detail) if SchoolIntro.adminExists(detail.admin_login) =>
            BadRequest(Json.toJson(new ErrorResponse("校长登录名已经存在")))
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }
}
