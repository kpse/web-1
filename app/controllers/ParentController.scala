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

  implicit val write2 = Json.writes[ChildInfo]
  implicit val write3 = Json.writes[School]
  implicit val write1 = Json.writes[ParentInfo]
  implicit val write4 = Json.writes[Parent]
  implicit val write5 = Json.writes[SuccessResponse]
  implicit val write6 = Json.writes[ErrorResponse]

  def index(kg: Long, classId: Option[Long], member: Option[Boolean], connected: Option[Boolean]) = IsLoggedIn {
    u => _ =>
      classId match {
        case Some(id) =>
          Ok(Json.toJson(Parent.indexInClass(kg, id, member)))
        case None =>
          Ok(Json.toJson(Parent.simpleIndex(kg, member, connected)))
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
        Logger.info(request.body.toString())
        request.body.validate[Parent].map {
          case (phone) =>
            handleCreateOrUpdate(kg, phone)
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }


  def newParentIsAMember(parent: Parent): Boolean = {
    parent.member_status.getOrElse(0) == 1
  }

  def update(kg: Long, phone: String) = IsLoggedIn(parse.json) {
    u =>
      request =>
        Logger.info(request.body.toString())
        request.body.validate[Parent].map {
          case (error) if !phone.equals(error.phone) =>
            BadRequest(Json.toJson(ErrorResponse("与url中电话号码不匹配。")))
          case (parent) =>
            handleCreateOrUpdate(kg, parent)
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }

  def handleCreateOrUpdate(kg: Long, parent: Parent) = parent match {
    case (error) if kg != error.school_id =>
      BadRequest(Json.toJson(ErrorResponse("请求的学校不正确。")))
    case (error) if newParentIsAMember(error) && Charge.limitExceed(kg) =>
      BadRequest(Json.toJson(ErrorResponse("已达到学校授权人数上限，无法再开通新号码，请联系幼乐宝技术支持4009984998")))
    case (error) if Parent.existsInOtherSchool(kg, error) =>
      BadRequest(Json.toJson(ErrorResponse("手机号码‘%s’已经在别的学校注册，目前幼乐宝不支持同一家长在多家幼儿园注册，请联系幼乐宝技术支持4009984998".format(error.phone))))
    case (error) if Parent.hasDuplicatedPhoneWithOtherParent(error) =>
      BadRequest(Json.toJson(ErrorResponse("手机号码‘%s’已经存在，请检查输入信息是否正确".format(error.phone))))
    case (error) if Parent.phoneExists(kg, error.phone) && error.parent_id.isDefined && !Parent.idExists(error.parent_id) =>
      BadRequest(Json.toJson(ErrorResponse("手机号码‘%s’已经存在，请检查输入信息是否正确".format(error.phone))))
    case (parent) if !Parent.idExists(parent.parent_id) && parent.status == Some(0) =>
      Ok(Json.toJson(ErrorResponse("忽略已删除数据。")))
    case (parent) if Parent.idExists(parent.parent_id) =>
      Ok(Json.toJson(Parent.update(parent)))
    case (parent) if Parent.phoneExists(kg, parent.phone) =>
      Ok(Json.toJson(Parent.updateWithPhone(kg, parent)))
    case (newParent) =>
      Ok(Json.toJson(Parent.create(kg, newParent)))
  }

  def delete(kg: Long, phone: String) = IsLoggedIn {
    u => _ =>
      Parent.delete(kg)(phone)
      Ok(Json.toJson(new SuccessResponse))
  }

  def show(kg: Long, phone: String) = IsLoggedIn {
    u => _ =>
      Parent.show(kg, phone).fold(NotFound(""))({
        case p => Ok(Json.toJson(p))
      })
  }
}
