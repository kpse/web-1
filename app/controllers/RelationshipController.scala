package controllers

import controllers.V3.EmployeeCardController._
import controllers.helper.JsonLogger._
import models.V3.{EmployeeCard, CardV3}
import models._
import play.Logger
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc.Controller

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
            BadRequest(loggedJson(ErrorResponse("本校记录中找不到对应的家长信息。(Parents info is not found in current school)")))
          case exists if exists && !Children.idExists(Some(childId)) =>
            BadRequest(loggedJson(ErrorResponse("本校记录中找不到该小孩信息。(Child info is not found in current school)", 2)))
          case exists if exists && uid.isEmpty && existingCard.nonEmpty && !existingCard.equals(Some(card)) =>
            BadRequest(loggedJson(ErrorResponse("此对家长和小孩已经创建过关系了。(Duplicated relationship)", 3)))
          case c if !CardV3.valid(card) =>
            Ok(Json.toJson(new ErrorResponse(s"卡号${card}未授权，请联系库贝人员。(Invalid card number)", 4)))
          case exists if exists && uid.isDefined =>
            Logger.info("update existing 1, reusing existing card")
            Ok(Json.toJson(Relationship.update(kg, card, relationship, phone, childId, uid.get)))
          case exists if exists && uid.isEmpty =>
            BadRequest(loggedJson(ErrorResponse(s"创建关系失败，${card}号卡已经关联过家长。(Card is connected to parent before)", 5)))
          case exists if !exists && uid.isDefined && Relationship.cardExists(card, None) =>
            BadRequest(loggedJson(ErrorResponse(s"修改关系失败，${card}号卡已经关联过家长。(Card is connected to parent before)", 6)))
          case exists if !exists && uid.isDefined =>
            Logger.info("update existing 2")
            Ok(Json.toJson(Relationship.update(kg, card, relationship, phone, childId, uid.get)))
          case exists if !exists && uid.isEmpty && Relationship.deleted(card) =>
            Logger.info("update existing 3, reusing deleted card")
            Ok(Json.toJson(Relationship.reuseDeletedCard(kg, card, relationship, phone, childId)))
          case exists if !exists && uid.isEmpty =>
            Logger.info("create new")
            Ok(Json.toJson(Relationship.create(kg, card, relationship, phone, childId)))
        }

  }

  def create(kg: Long) = IsAuthenticated(parse.json) {
    u =>
      implicit request =>
        Logger.info(request.body.toString())
        val body: JsValue = request.body
        val relationship: String = (body \ "relationship").as[String]
        val phone: String = (body \ "parent" \ "phone").as[String]
        val childId: String = (body \ "child" \ "child_id").as[String]

        (phone, childId) match {
          case (p, _) if !Parent.phoneExists(kg, p) =>
            BadRequest(loggedJson(ErrorResponse("本校记录中找不到对应的家长信息。(Parents info is not found in current school)")))
          case (_, c) if !Children.idExists(Some(c)) =>
            BadRequest(loggedJson(ErrorResponse("本校记录中找不到该小孩信息。(Child info is not found in current school)", 2)))
          case (p, c) if Relationship.getCard(p, c).nonEmpty =>
            BadRequest(loggedJson(ErrorResponse("此对家长和小孩已经创建过关系了。(Duplicated relationship)", 3)))
          case (p, c) =>
            Logger.info("create new")
            Ok(Json.toJson(Relationship.fakeCardCreate(kg, relationship, p, c)))
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
        case (relationship) if Relationship.deleted(relationship.card) =>
          Ok(Json.toJson(new SuccessResponse("已删除卡片，可重用。(Reuse deleted card)")))
        case (relationship) =>
          Relationship.search(relationship.card) match {
            case Some(x) if relationship.id.nonEmpty && x.id == relationship.id =>
              Ok(Json.toJson(new SuccessResponse(s"卡号${relationship.card}与id${relationship.id}关系未改变。(Current card unchanged)")))
            case Some(x) =>
              Ok(Json.toJson(new ErrorResponse(s"卡号${relationship.card}已使用，不允许覆盖。(Current card is in using)")))
            case None if !CardV3.valid(relationship.card) =>
              Ok(Json.toJson(new ErrorResponse(s"卡号${relationship.card}未授权，请联系库贝人员。(Invalid card number)", 2)))
            case None =>
              Ok(Json.toJson(new SuccessResponse("已授权卡号未使用。(Available card number)")))
          }
      }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def search(card: String) = IsOperator {
    u => _ =>
      Relationship.search(card) match {
        case Some(r) =>
          Ok(Json.toJson(Relationship.search(card)))
        case None =>
          NotFound
      }

  }

  def permanentRemove(card: String) = IsOperator {
    u => _ =>
      Relationship.search(card) match {
        case Some(r) =>
          Ok(Json.toJson(Relationship.delete(0, card)))
        case None =>
          NotFound
      }
  }
}
