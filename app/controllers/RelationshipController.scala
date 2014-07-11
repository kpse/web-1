package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.{JsValue, JsError, Json}
import models._
import play.Logger
import models.ErrorResponse
import models.ChildInfo
import helper.JsonLogger._

object RelationshipController extends Controller with Secured {

  implicit val write1 = Json.writes[ChildInfo]
  implicit val write2 = Json.writes[Parent]
  implicit val write3 = Json.writes[Relationship]
  implicit val write4 = Json.writes[ErrorResponse]
  implicit val read1 = Json.reads[ChildInfo]
  implicit val read2 = Json.reads[Parent]
  implicit val read3 = Json.reads[Relationship]

  def index(kg: Long, parent: Option[String], child: Option[String], classId: Option[Long]) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(Relationship.index(kg, parent, child, classId)))
  }


  def createOrUpdate(kg: Long, card: String) = IsAuthenticated(parse.json) {
    u =>
      implicit request =>
        Logger.info(request.body.toString())
        val body: JsValue = request.body
        val relationship: String = (body \ "relationship").as[String]
        val phone: String = (body \ "parent" \ "phone").as[String]
        val childId: String = (body \ "child" \ "child_id").as[String]
        val uid: Option[Long] = (body \ "id").as[Option[Long]]

        val existingCard = Relationship.getCard(phone, childId)

        Relationship.cardExists(card, uid) match {
          case exists if exists && !Parent.phoneExists(kg, phone) =>
            BadRequest(loggedJson(ErrorResponse("本校记录中找不到对应的家长信息。")))
          case exists if exists && !Children.idExists(Some(childId)) =>
            BadRequest(loggedJson(ErrorResponse("本校记录中找不到该小孩信息。")))
          case exists if exists && uid.isEmpty && existingCard.nonEmpty && !existingCard.equals(Some(card)) =>
            BadRequest(loggedJson(ErrorResponse("此对家长和小孩已经创建过关系了。")))
          case exists if exists && uid.isDefined =>
            Logger.info("update existing 1")
            Ok(Json.toJson(Relationship.update(kg, card, relationship, phone, childId, uid.get)))
          case exists if exists && uid.isEmpty =>
            BadRequest(loggedJson(ErrorResponse("卡号已存在，%s号卡已经关联过家长。".format(card))))
          case exists if !exists && uid.isDefined && Relationship.cardExists(card, None) =>
            BadRequest(loggedJson(ErrorResponse("卡号已存在，%s号卡已经关联过家长。".format(card))))
          case exists if !exists && uid.isDefined =>
            Logger.info("update existing 2")
            Ok(Json.toJson(Relationship.update(kg, card, relationship, phone, childId, uid.get)))
          case exists if !exists && uid.isEmpty =>
            Logger.info("create new")
            Ok(Json.toJson(Relationship.create(kg, card, relationship, phone, childId)))
        }

  }

  def show(kg: Long, card: String) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(Relationship.show(kg, card)))
  }

  def delete(kg: Long, card: String) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(Relationship.delete(kg, card)))
  }

  def isGoodToUse = IsAuthenticated(parse.json) {
    u => request =>
      request.body.validate[Relationship].map {
        case (relationship) => NotFound
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }
}
