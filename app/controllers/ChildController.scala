package controllers

import play.api.libs.json.Json
import play.api.mvc._
import play.api.Logger
import models.{ErrorResponse, Children, ChildInfo, School}

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
      Children.findAllInClass(kg, classIds, connect) match {
        case all: List[ChildInfo] => Ok(Json.toJson(all))
      }
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
        Logger.info(request.body.toString)
        request.body.validate[ChildInfo].map {
          case (info) if !School.classExists(kg, info.class_id) =>
            BadRequest("class " + info.class_id + " does not exists.")
          case (info) if !Children.idExists(info.child_id) && info.status == Some(0)  =>
            Ok(Json.toJson(ErrorResponse("忽略已删除数据。")))
          case (info) if Children.idExists(info.child_id) =>
            Ok(Json.toJson(Children.updateByChildId(kg, info.child_id.get, info)))
          case (info) =>
            Ok(Json.toJson(Children.create(kg, info)))
        }.getOrElse(BadRequest)

  }

  def update2(kg: Long, childId: String) = IsLoggedIn(parse.json) {
    u =>
      implicit request =>
        Logger.info(request.body.toString)
        request.body.validate[ChildInfo].map {
          case (info) if !School.classExists(kg, info.class_id) =>
            BadRequest("class " + info.class_id + " does not exists.")
          case (info) if !Children.idExists(info.child_id) && info.status == Some(0)  =>
            Ok(Json.toJson(ErrorResponse("忽略已删除数据。")))
          case (info) if Children.idExists(Some(childId)) =>
            Ok(Json.toJson(Children.updateByChildId(kg, childId, info)))
          case (info) =>
            Ok(Json.toJson(Children.create(kg, info)))
        }.getOrElse(BadRequest)

  }

  def delete(kg: Long, childId: String) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(Children.delete(kg, childId)))
  }

}
