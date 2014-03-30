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


  def index(kg: Long, classId: Option[Long], member: Option[Boolean], connected: Option[Boolean]) = IsLoggedIn {
    u => _ =>
      classId match {
        case Some(id) =>
          val jsons = Parent.indexInClass(kg, id, member)
          Logger.info(jsons.toString)
          Ok(Json.toJson(jsons))
        case None =>
          val jsons = Parent.simpleIndex(kg, member, connected)
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
          case (error) if Parent.existsInOtherSchool(error) =>
            BadRequest(Json.toJson(new ErrorResponse("此号码已经在别的学校注册，目前幼乐宝不支持同一家长在多家幼儿园注册，请联系幼乐宝技术支持4009984998")))
          case (parent) if Parent.idExists(parent.parent_id) =>
            Ok(Json.toJson(Parent.update(parent)))
          case (parent) if Parent.phoneExists(kg, parent.phone) =>
            Ok(Json.toJson(Parent.updateWithPhone(kg, parent)))
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
          case (error) if Parent.existsInOtherSchool(error) =>
            BadRequest(Json.toJson(new ErrorResponse("此号码已经在别的学校注册，目前幼乐宝不支持同一家长在多家幼儿园注册，请联系幼乐宝技术支持4009984998")))
          case (parent) if Parent.idExists(parent.parent_id) =>
            Ok(Json.toJson(Parent.update(parent)))
          case (parent) if Parent.phoneExists(kg, phone) =>
            Ok(Json.toJson(Parent.updateWithPhone(kg, parent)))
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
      val parent = Parent.show(kg, phone)
      parent.map {
        p => Ok(Json.toJson(p))
      }.getOrElse(NotFound)

  }
}
