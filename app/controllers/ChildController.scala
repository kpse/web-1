package controllers

import play.api.libs.json.Json
import play.api.mvc._
import models.json_models._
import play.api.Logger
import models.json_models.ChildrenResponse
import models.json_models.ChildDetailResponse
import models.json_models.ChildDetail
import models.School

object ChildController extends Controller with Secured {

  implicit val write1 = Json.writes[ChildDetail]
  implicit val write2 = Json.writes[ChildDetailResponse]
  implicit val write3 = Json.writes[ChildrenResponse]
  implicit val write4 = Json.writes[ChildInfo]

  implicit val read1 = Json.reads[ChildUpdate]
  implicit val read2 = Json.reads[ChildInfo]

  def show(kg: Long, phone: String, childId: String) = Action {
    Children.show(kg.toLong, phone, childId) match {
      case Some(one: ChildDetail) => Ok(Json.toJson(new ChildDetailResponse(0, Some(one))))
      case None => Ok(Json.toJson(new ChildDetailResponse(1, None)))
    }
  }


  def index(kg: Long, phone: String) = IsLoggedIn { u => _=>
    Children.findAll(kg, phone) match {
      case Nil => Ok(Json.toJson(ChildrenResponse(1, List())))
      case all: List[ChildDetail] => Ok(Json.toJson(ChildrenResponse(0, all)))
    }
  }

  def update(kg: Long, phone: String, childId: String) = Action(parse.json) {
    implicit request =>
      Logger.info(request.body.toString)
      request.body.validate[ChildUpdate].map {
        case (update) =>
          Children.update(kg, phone, childId, update) match {
            case Some(one: ChildDetail) => Ok(Json.toJson(new ChildDetailResponse(0, Some(one))))
            case None => Ok(Json.toJson(new ChildDetailResponse(1, None)))
          }
      }.getOrElse(BadRequest)

  }


  //=============================================================================================

  def indexInSchool(kg: Long, classId: Option[Long], connect: Option[Boolean]) = IsLoggedIn { u => _ =>
    Children.findAllInClass(kg, classId, connect) match {
      case all: List[ChildInfo] => Ok(Json.toJson(all))
    }
  }

  def showInfo(kg: Long, childId: String) = Action {
    Children.info(kg.toLong, childId) match {
      case Some(one: ChildInfo) => Ok(Json.toJson(one))
      case None => BadRequest
    }
  }

  def createOrUpdate(kg: Long) = Action(parse.json) {
    implicit request =>
      Logger.info(request.body.toString)
      request.body.validate[ChildInfo].map {
        case (info) if ! School.classExists(kg, info.class_id) =>
          BadRequest("class " + info.class_id + " does not exists.")
        case (info) if Children.idExists(info.child_id) =>
          Ok(Json.toJson(Children.updateByChildId(kg, info.child_id.get, info)))
        case (info) =>
          Ok(Json.toJson(Children.create(kg, info)))
      }.getOrElse(BadRequest)

  }

  def update2(kg: Long, childId: String) = Action(parse.json) {
    implicit request =>
      Logger.info(request.body.toString)
      request.body.validate[ChildInfo].map {
        case (info) if ! School.classExists(kg, info.class_id) =>
          BadRequest("class " + info.class_id + " does not exists.")
        case (info) if Children.idExists(Some(childId)) =>
          Ok(Json.toJson(Children.updateByChildId(kg, childId, info)))
        case (info) =>
          Ok(Json.toJson(Children.create(kg, info)))
      }.getOrElse(BadRequest)

  }
}
