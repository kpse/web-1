package controllers

import _root_.helper.TestSupport
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import play.api.test.FakeRequest
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers._
import models.{Parent, Relationship, ChildInfo}

@RunWith(classOf[JUnitRunner])
class RelationshipControllerSpec extends Specification with TestSupport {
  def loggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "13408654680", "token" -> "1386849160798")
  }

  "Relationship" should {
    "check authentication first" in new WithApplication {
      val response = route(FakeRequest(GET, "/kindergarten/93740362/relationship")).get

      status(response) must equalTo(UNAUTHORIZED)

    }

    "be retrieved by card number" in new WithApplication {

      val res = route(loggedRequest(GET, "/kindergarten/93740362/relationship/0001234567")).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "card").as[String] must equalTo("0001234567")
    }

    "be updated with same information" in new WithApplication {
      private val jsBody = createAExistingRelationship("0001234567", "13408654680", "1_1391836223533", 1)
      val res = route(loggedRequest(POST, "/kindergarten/93740362/relationship/0001234567").withBody(jsBody)).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "card").as[String] must equalTo("0001234567")
    }

    "check card is good to use" in new WithApplication {

      private val jsBody = newCard
      val res = route(loggedRequest(POST, "/api/v1/card_check").withBody(jsBody)).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")
      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "error_code").as[Int] must equalTo(0)
    }

    "complain about card is used" in new WithApplication {

      private val jsBody = existingCard
      val res = route(loggedRequest(POST, "/api/v1/card_check").withBody(jsBody)).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")
      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "error_code").as[Int] must equalTo(1)
    }

    "allow relationship with card and corresponding id" in new WithApplication {

      private val jsBody = createAExistingRelationship("0001234567", "13408654680", "1_1391836223533", 1)
      val res = route(loggedRequest(POST, "/api/v1/card_check").withBody(jsBody)).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")
      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "error_code").as[Int] must equalTo(0)
    }

    "allow to reuse deleted card" in new WithApplication {

      private val jsBody = deletedCard
      val res = route(loggedRequest(POST, "/api/v1/card_check").withBody(jsBody)).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")
      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "error_code").as[Int] must equalTo(0)
    }

    "reuse deleted card" in new WithApplication {
      val cardNumber: String = "0001234999"
      val parentPhone: String = "13408654680"
      val child_id: String = "1_93740362_9982"

      private val jsBody = createANewRelationship(cardNumber, parentPhone, child_id)
      val res = route(loggedRequest(POST, "/kindergarten/93740362/relationship/" + cardNumber).withBody(jsBody)).get

      status(res) must equalTo(OK)
      contentType(res) must beSome("application/json")

      val oldCard: JsValue = Json.parse(contentAsString(res))
      (oldCard \ "card").as[String] must equalTo(cardNumber)
      (oldCard \ "parent" \ "parent_id").as[String] must equalTo("2_93740362_789")
      (oldCard \ "child" \ "child_id").as[String] must equalTo(child_id)

      val res2 = route(loggedRequest(DELETE, "/kindergarten/93740362/relationship/" + cardNumber)).get
      status(res2) must equalTo(OK)

      private val newRelationship = createANewRelationship(cardNumber, parentPhone, "1_93740362_778")
      val res3 = route(loggedRequest(POST, "/kindergarten/93740362/relationship/" + cardNumber).withBody(newRelationship)).get

      status(res3) must equalTo(OK)
      contentType(res3) must beSome("application/json")
      val newCard: JsValue = Json.parse(contentAsString(res3))
      (newCard \ "card").as[String] must equalTo(cardNumber)
      (newCard \ "parent" \ "parent_id").as[String] must equalTo("2_93740362_789")
      (newCard \ "child"  \ "child_id").as[String] must equalTo("1_93740362_778")
    }

  }

  implicit val write1 = Json.writes[Parent]
  implicit val write2 = Json.writes[ChildInfo]
  implicit val write3 = Json.writes[Relationship]

  def createAExistingRelationship(card: String, phone: String, childId: String, id: Long): JsValue = {
    Json.toJson(Relationship(
      Some(Parent(None, 0, "", phone, None, 0, "", None, None, None, None)),
      Some(ChildInfo(Some(childId), "", "", "", 0, None, 0, None, None, None)),
      card, "妈妈", Some(id)))
  }

  def createANewRelationship(card: String, phone: String, childId: String): JsValue = {
    Json.toJson(Relationship(
      Some(Parent(None, 0, "", phone, None, 0, "", None, None, None, None)),
      Some(ChildInfo(Some(childId), "", "", "", 0, None, 0, None, None, None)),
      card, "妈妈", None))
  }

  def createCard(card: String) = Json.toJson(Relationship(
    Some(Parent(None, 0, "", "", None, 0, "", None, None, None, None)),
    Some(ChildInfo(Some(""), "", "", "", 0, None, 0, None, None, None)),
    card, "", None))

  def newCard: JsValue = {
    createCard("9991234567")
  }

  def deletedCard: JsValue = {
    createCard("0091234567")
  }

  def existingCard: JsValue = {
    createCard("0001234567")
  }
}
