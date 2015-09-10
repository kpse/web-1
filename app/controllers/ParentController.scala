package controllers

import controllers.V3.RelativeController
import play.api.mvc._
import play.api.libs.json.{JsError, Json}
import play.api.data.Form
import play.api.data.Forms._
import models._
import play.api.Logger
import models.ParentInfo

object ParentController extends Controller with Secured {

  implicit val write2 = Json.writes[ChildInfo]
  implicit val write3 = Json.writes[School]
  implicit val write1 = Json.writes[ParentInfo]
  implicit val write4 = Json.writes[Parent]
  implicit val write5 = Json.writes[SuccessResponse]
  implicit val write6 = Json.writes[ErrorResponse]

  def index(kg: Long, classId: Option[Long], member: Option[Boolean], connected: Option[Boolean]) = IsLoggedIn {
    u => _ =>
      val accesses: List[UserAccess] = UserAccess.queryByUsername(u, kg)
      UserAccess.filterClassId(accesses)(classId) match {
        case Some(id) =>
          Ok(Json.toJson(Parent.indexInClasses(kg, id.toString, member, connected)))
        case None if classId.isDefined =>
          Ok(Json.toJson(List[Parent]()))
        case None if connected.isDefined && !connected.get =>
          Ok(Json.toJson(Parent.simpleIndex(kg, member, connected)))
        case None =>
          UserAccess.isSupervisor(accesses) match {
            case true =>
              Ok(Json.toJson(Parent.simpleIndex(kg, member, connected)))
            case false =>
              connected match {
                case None =>
                  val parents: List[Parent] = Parent.indexInClasses(kg, UserAccess.allClasses(accesses), member, None)
                  val unConnected = Parent.indexInClasses(kg, UserAccess.allClasses(accesses), member, Some(false))
                  Ok(Json.toJson(parents ++ unConnected))
                case _ =>
                  Ok(Json.toJson(Parent.indexInClasses(kg, UserAccess.allClasses(accesses), member, connected)))
              }

          }

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

  def clearCurrentCache() = {
    RelativeController.clearCurrentCache()
    RelationshipController.clearCurrentCache()
  }

  def handleCreateOrUpdate(kg: Long, parent: Parent) = parent match {
    case (error) if kg != error.school_id =>
      BadRequest(Json.toJson(ErrorResponse("请求的学校不正确。")))
    case (error) if error.isAMember && Charge.limitExceed(kg)  =>
      BadRequest(Json.toJson(ErrorResponse("已达到学校授权人数上限，无法再开通新号码，请联系幼乐宝技术支持4009984998")))
    case (error) if error.isAVideoMember && VideoMember.limitExceed(kg)  =>
      BadRequest(Json.toJson(ErrorResponse("已达到学校视频授权人数上限，无法再开通新视频账号，请联系幼乐宝技术支持4009984998")))
    case (error) if Parent.existsInOtherSchool(kg, error) =>
      BadRequest(Json.toJson(ErrorResponse("手机号码‘%s’已经在别的学校注册，目前幼乐宝不支持同一家长在多家幼儿园注册，请联系幼乐宝技术支持4009984998".format(error.phone))))
    case (error) if error.parent_id.isDefined && error.duplicatedPhoneWithOthers =>
      BadRequest(Json.toJson(ErrorResponse(s"手机号码‘${error.phone}’已经存在，请检查输入号码是否正确")))
    case (deletedParent) if !Parent.idExists(deletedParent.parent_id) && deletedParent.status.equals(Some(0)) =>
      Ok(Json.toJson(ErrorResponse("忽略已删除数据。")))
    case (phoneTransfer) if Parent.idExists(phoneTransfer.parent_id) && Parent.phoneDeleted(kg, phoneTransfer.phone) =>
      clearCurrentCache()
      phoneTransfer.transfer match {
        case Some(p) =>
          Ok(Json.toJson(p))
        case None =>
          InternalServerError(Json.toJson(ErrorResponse(s"交换已有号码失败。(failed exchanging existing phone number to parent ${phoneTransfer.parent_id})")))
      }
    case (update) if Parent.idExists(update.parent_id) =>
      clearCurrentCache()
      Ok(Json.toJson(Parent.update(update)))
    case (phoneReuse) if phoneReuse.parent_id.nonEmpty && Parent.phoneDeleted(kg, phoneReuse.phone) =>
      clearCurrentCache()
      phoneReuse.reusePhone match {
        case Some(p) =>
          Ok(Json.toJson(p))
        case None =>
          InternalServerError(Json.toJson(ErrorResponse("覆盖已有号码失败。(failed overriding existing phone number)")))
      }
    case (phoneUpdate) if Parent.phoneExists(kg, phoneUpdate.phone) =>
      clearCurrentCache()
      Parent.updateWithPhone(kg, phoneUpdate) match {
        case Some(p) =>
          Ok(Json.toJson(p))
        case None =>
          InternalServerError(Json.toJson(ErrorResponse("按号码更新失败。(failed updating by existing phone number)")))
      }
    case (newParent) =>
      clearCurrentCache()
      Ok(Json.toJson(Parent.create(kg, newParent)))
  }

  def delete(kg: Long, phone: String) = IsLoggedIn {
    u => _ =>
      val parent: Option[Parent] = Parent.delete(kg)(phone)
      parent map {case p => VideoMember.delete(kg, p.parent_id.getOrElse("null"))}
      clearCurrentCache()
      Ok(Json.toJson(new SuccessResponse))
  }

  def show(kg: Long, phone: String) = IsLoggedIn {
    u => _ =>
      Parent.show(kg, phone).fold(NotFound(""))({
        case p => Ok(Json.toJson(p))
      })
  }

  implicit val read6 = Json.reads[ParentPhoneCheck]
  implicit val read7 = Json.reads[EmployeePhoneCheck]
  def isGoodToUse(phone: String, isEmployee: Option[String]) = IsAuthenticated(parse.json) {
    u => request =>
      isEmployee match {
        case Some(s) =>
          request.body.validate[EmployeePhoneCheck].map {
            case (employee) =>
              numberCheck(employee, Employee.phoneSearch(employee.phone))
          }.recoverTotal {
            e => BadRequest("Detected error:" + JsError.toFlatJson(e))
          }
        case None =>
          request.body.validate[ParentPhoneCheck].map {
            case (parent) =>
              numberCheck(parent, Parent.phoneSearch(parent.phone))
          }.recoverTotal {
            e => BadRequest("Detected error:" + JsError.toFlatJson(e))
          }
      }

  }
  def numberCheck[T](person: PhoneCheck[T], exist: Option[T]) = exist.fold(Ok(Json.toJson(new SuccessResponse("号码不存在。"))))({
    case p if person.isTheSame(p) =>
      Ok(Json.toJson(SuccessResponse("号码已存在，且与id匹配。(phone number matches id)")))
    case p if person.isDeleted(p) =>
      Ok(Json.toJson(SuccessResponse("号码已被删除，可以使用。(number has been deleted)")))
    case _ =>
      Ok(Json.toJson(ErrorResponse("号码与id不匹配。(phone number does not match id)")))
  })

  def isGoodToUseInSchool(kg: Long, phone: String, isEmployee: Option[String]) = IsAuthenticated(parse.json) {
    u => request =>
      isEmployee match {
        case Some(s) =>
          request.body.validate[EmployeePhoneCheck].map {
            case (employee) if employee.existsInSchool(kg) =>
              Ok(Json.toJson(SuccessResponse(s"号码#{employee.phone}已存在，且与id匹配。")))
            case (employee) =>
              numberCheck(employee, Employee.phoneSearch(employee.phone))
          }.recoverTotal {
            e => BadRequest("Detected error:" + JsError.toFlatJson(e))
          }
        case None =>
          request.body.validate[ParentPhoneCheck].map {
            case (parent) if parent.existsInSchool(kg) =>
              Ok(Json.toJson(SuccessResponse(s"号码#{parent.phone}已存在，且与id匹配。")))
            case (parent) =>
              numberCheck(parent, Parent.phoneSearch(parent.phone))
          }.recoverTotal {
            e => BadRequest("Detected error:" + JsError.toFlatJson(e))
          }
      }

  }
}
