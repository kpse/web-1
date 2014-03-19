package controllers

import play.api.mvc._
import play.api.libs.json.{JsError, Json}
import play.api.data.Form
import play.api.data.Forms._
import models._
import play.api.Logger
import models.ParentInfo
import scala.Some

object ParentController extends Controller with Secured {

  implicit val write3 = Json.writes[School]
  implicit val write2 = Json.writes[ChildInfo]
  implicit val write1 = Json.writes[ParentInfo]
  implicit val write4 = Json.writes[Parent]


  def index(kg: Long, classId: Option[Long], member: Option[Boolean]) = IsLoggedIn {
    u => _ =>
      classId match {
        case Some(id) =>
          val jsons = Parent.indexInClass(kg, id, member)
          Logger.info(jsons.toString)
          Ok(Json.toJson(jsons))
        case None =>
          val jsons = Parent.simpleIndex(kg, member)
          Logger.info(jsons.toString)
          Ok(Json.toJson(jsons))
      }
  }

  val parentForm = Form(
    tuple(
      "name" -> text,
      "phone" -> text,
      "kg" -> text
    )
  )

  implicit val read1 = Json.reads[School]
  implicit val read2 = Json.reads[ChildInfo]
  implicit val read3 = Json.reads[ParentInfo]
  implicit val read4 = Json.reads[Parent]

  def create(kg: Long) = IsLoggedIn(parse.json) {
    u =>
      request =>
        Logger.info(request.body.toString)
        request.body.validate[Parent].map {
          case (parent) if Parent.idExists(parent.parent_id) =>
            Ok(Json.toJson(Parent.update2(parent)))
          case (parent) =>
            Ok(Json.toJson(Parent.create(kg, parent)))
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }

  def update(kg: Long, phone: String) = IsLoggedIn(parse.json) {
    u =>
      request =>
        Logger.info(request.body.toString)
        request.body.validate[Parent].map {
          case (parent) if Parent.idExists(parent.parent_id) =>
            Ok(Json.toJson(Parent.update2(parent)))
          case (parent) if Parent.phoneExists(kg, phone) =>
            Ok(Json.toJson(Parent.updateWithPhone(kg, parent)))
          case (error) if Parent.existsInOtherSchool(error) =>
            BadRequest(Json.toJson(new ErrorResponse("电话号码在其他学校已存在。")))
          case (newParent) =>
            Ok(Json.toJson(Parent.create(kg, newParent)))
        } getOrElse BadRequest("Detected error:" + request.body)
  }

  implicit val write5 = Json.writes[SuccessResponse]
  implicit val write6 = Json.writes[ErrorResponse]

  def delete(kg: Long, phone: String) = IsLoggedIn {
    u => _ =>
      Parent.delete(kg)(phone)
      Ok(Json.toJson(new SuccessResponse))
  }

  def show(kg: Long, phone: String) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(Parent.show(kg, phone)))
  }
}
