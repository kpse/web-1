package controllers.V2

import controllers.Secured
import models.json_models.SchoolIntro._
import models.json_models._
import play.api.libs.json.Json
import play.api.mvc._

object SchoolController extends Controller with Secured {
  implicit val writes1 = Json.writes[SchoolIntroPreviewResponse]

  implicit val writes3 = Json.writes[SchoolIntroDetail]

  def previewIndex(q: Option[String]) = IsOperator {
    u => _ =>
      Ok(Json.toJson(SchoolIntro.previewIndex(q)))
  }

  def pagination(q: Option[String], from: Option[Long], to: Option[Long], most: Option[Int]) = IsOperator {
    u => _ =>
      Ok(Json.toJson(SchoolIntro.pagination(q, from, to, most)))
  }
}
