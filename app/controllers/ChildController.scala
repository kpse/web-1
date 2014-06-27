package controllers

import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import play.api.Logger
import models._
import scala.Some
import models.ErrorResponse
import models.ChildInfo

object ChildController extends Controller with Secured {

  implicit val write1 = Json.writes[ChildInfo]
  implicit val write2 = Json.writes[ErrorResponse]
  implicit val read1 = Json.reads[ChildInfo]

  def show(kg: Long, phone: String, childId: String) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(Children.show(kg.toLong, phone, childId)))
  }


  def index(kg: Long, phone: String) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(Children.findAll(kg, phone)))
  }

  //=============================================================================================

  def indexInSchool(kg: Long, classIds: Option[String], connect: Option[Boolean]) = IsLoggedIn {
    u => _ =>
      val accesses: List[UserAccess] = UserAccess.queryByUsername(u, kg)
      val all = UserAccess.filterClassIds(accesses)(classIds) match {
        case ids if ids.length > 0 =>
          Logger.info(ids)
          Children.findAllInClass(kg, Some(ids), connect)
        case wrong if wrong.isEmpty && classIds.nonEmpty =>
          List[ChildInfo]()
        case empty =>
          UserAccess.isSupervisor(accesses) match {
            case true =>
              Children.findAllInClass(kg, None, connect)
            case false =>
              Children.findAllInClass(kg, Some(UserAccess.allClasses(accesses)), connect)
          }

      }
      Ok(Json.toJson(all))
  }

  def showInfo(kg: Long, childId: String) = IsLoggedIn {
    u => _ =>
      Children.info(kg.toLong, childId) match {
        case Some(one: ChildInfo) => Ok(Json.toJson(one))
        case None => BadRequest
      }
  }

  def createOrUpdate(kg: Long) = IsLoggedIn(parse.json) {
    u =>
      implicit request =>
        Logger.info(request.body.toString())
        request.body.validate[ChildInfo].map {
          case (info) if info.school_id.isDefined && info.school_id.get != kg =>
            BadRequest(Json.toJson(ErrorResponse("学校不匹配。")))
          case (info) if !School.classExists(kg, info.class_id) =>
            BadRequest("class " + info.class_id + " does not exists.")
          case (info) if !Children.idExists(info.child_id) && info.status == Some(0) =>
            Ok(Json.toJson(ErrorResponse("忽略已删除数据。")))
          case (info) if Children.idExists(info.child_id) =>
            Ok(Json.toJson(Children.update(info)))
          case (info) =>
            Ok(Json.toJson(Children.create(kg, info)))
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }

  }

  def update2(kg: Long, childId: String) = IsLoggedIn(parse.json) {
    u =>
      implicit request =>
        Logger.info(request.body.toString())
        request.body.validate[ChildInfo].map {
          case (info) if !info.child_id.getOrElse("").equals(childId) =>
            BadRequest(Json.toJson(ErrorResponse("小孩id不匹配。")))
          case (info) if info.school_id.isDefined && info.school_id.get != kg =>
            BadRequest(Json.toJson(ErrorResponse("学校不匹配。")))
          case (info) if !School.classExists(kg, info.class_id) =>
            BadRequest("class " + info.class_id + " does not exists.")
          case (info) if !Children.idExists(info.child_id) && info.status == Some(0) =>
            Ok(Json.toJson(ErrorResponse("忽略已删除数据。")))
          case (info) if Children.idExists(Some(childId)) =>
            Ok(Json.toJson(Children.update(info)))
          case (info) =>
            Ok(Json.toJson(Children.create(kg, info)))
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }

  }

  def delete(kg: Long, childId: String) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(Children.delete(kg, childId)))
  }

}
