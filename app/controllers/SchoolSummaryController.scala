package controllers

import play.api.mvc._
import play.api.libs.json.{JsError, Json}
import models.json_models._
import play.Logger
import models.json_models.SchoolIntroPreviewResponse
import models.json_models.SchoolIntroDetail
import models.{Employee, ChargeInfo, ErrorResponse}

object SchoolSummaryController extends Controller with Secured {
  implicit val writes1 = Json.writes[SchoolIntroPreviewResponse]
  implicit val writes2 = Json.writes[SchoolIntro]
  implicit val writes3 = Json.writes[SchoolIntroDetail]
  implicit val writes4 = Json.writes[ErrorResponse]
  implicit val read1 = Json.reads[SchoolIntro]
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
      SchoolIntro.detail(kg).map {
        case school =>
          Ok(Json.toJson(new SchoolIntroDetail(Some(0), kg, Some(school))))
      }.getOrElse(NotFound)
  }

  def update(kg: Long) = IsLoggedIn(parse.json) {
    u =>
      request =>
        Logger.info(request.body.toString())
        request.body.validate[SchoolIntro].map {
          case (detail) if SchoolIntro.schoolExists(detail.school_id) =>
            Ok(Json.toJson(SchoolIntro.updateExists(detail)))
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
            BadRequest(Json.toJson(new ErrorResponse("校长手机号已经存在。")))
          case (detail) if SchoolIntro.idExists(detail.school_id) =>
            BadRequest(Json.toJson(new ErrorResponse("学校ID已经存在。")))
          case (detail) if SchoolIntro.adminExists(detail.principal.admin_login) =>
            BadRequest(Json.toJson(new ErrorResponse("校长登录名已经存在。")))
          case (detail) =>
            Ok(Json.toJson(SchoolIntro.create(detail)))
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }

  def show(kg: Long) = IsLoggedIn {
    u => _ =>
      SchoolIntro.detail(kg).map {
        case school =>
          Ok(Json.toJson(school))
      }.getOrElse(NotFound)
  }
}
