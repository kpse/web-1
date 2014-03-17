package controllers

import play.api.mvc._
import play.api.libs.json.{JsError, Json}
import models.json_models.{CheckInfo, SchoolIntro, SchoolIntroDetail, SchoolIntroPreviewResponse}
import play.Logger

object SchoolSummaryController extends Controller with Secured {
  implicit val writes1 = Json.writes[SchoolIntroPreviewResponse]
  implicit val writes2 = Json.writes[SchoolIntro]
  implicit val writes3 = Json.writes[SchoolIntroDetail]
  implicit val read1 = Json.reads[SchoolIntro]
  implicit val read2 = Json.reads[SchoolIntroDetail]

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
}
