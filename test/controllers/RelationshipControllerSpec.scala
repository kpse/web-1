package controllers

import _root_.helper.TestSupport
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import play.Logger
import play.api.test.FakeRequest
import play.api.libs.json.{JsArray, JsValue, Json}
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
      private val jsBody = createAnExistingRelationship("0001234567", "13408654680", "1_1391836223533", 1)
      val res = route(loggedRequest(POST, "/kindergarten/93740362/relationship/0001234567").withBody(jsBody)).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")

      val head: JsValue = Json.parse(contentAsString(res))
      (head \ "card").as[String] must equalTo("0001234567")
      (head \ "relationship").as[String] must equalTo("妈妈")

      private val parent: JsValue = head \ "parent"
      (parent \ "parent_id").as[String] must equalTo("2_93740362_789")
      (parent \ "phone").as[String] must equalTo("13408654680")
      (parent \ "gender").as[Int] must equalTo(0)
      (parent \ "school_id").as[Long] must equalTo(93740362)
      (parent \ "birthday").as[String] must equalTo("1999-01-01")
      (parent \ "portrait").as[String] must equalTo("")
      (parent \ "name").as[String] must equalTo("袋鼠")
      (parent \ "company").as[String] must equalTo("某公司")
      (parent \ "timestamp").as[Long] must equalTo(0)
      (parent \ "member_status").as[Int] must equalTo(1)
      (parent \ "status").as[Int] must equalTo(1)
      (parent \ "id").as[Long] must equalTo(3)
      (parent \ "created_at").as[Long] must equalTo(0)

      private val child: JsValue = head \ "child"
      (child \ "child_id").as[String] must equalTo("1_1391836223533")
      (child \ "birthday").as[String] must equalTo("2007-06-04")
      (child \ "portrait").as[String] must equalTo("http://www.qqw21.com/article/uploadpic/2013-1/201311101516591.jpg")
      (child \ "class_id").as[Int] must equalTo(777888)
      (child \ "class_id").as[Int] must equalTo(777888)
      (child \ "class_name").as[String] must equalTo("苹果班")
      (child \ "school_id").as[Long] must equalTo(93740362)
      (child \ "id").as[Long] must equalTo(1)
      (child \ "timestamp").as[Long] must equalTo(1387360036)
      (child \ "address").as[String] must equalTo("")
      (child \ "status").as[Int] must equalTo(1)
      (child \ "created_at").as[Long] must equalTo(0)
    }

    "check if card is good to use" in new WithApplication {
      val res = route(loggedRequest(POST, "/api/v1/card_check").withBody(newCard)).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")
      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "error_code").as[Int] must equalTo(0)
    }

    "be invalid card if it does not exist in cardrecord table" in new WithApplication {

      private val card = (invalidCard \ "card").as[String]
      val res = route(loggedRequest(POST, "/api/v1/card_check").withBody(invalidCard)).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")
      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "error_code").as[Int] must equalTo(2)
      (response \ "error_msg").as[String] must equalTo(s"卡号${card}未授权，请联系库贝人员。(Invalid card number)")
    }

    "not be created by invalid card" in new WithApplication {

      private val jsBody = invalidCard
      private val card = (invalidCard \ "card").as[String]
      val res = route(loggedRequest(POST, s"/kindergarten/93740362/relationship/$card").withBody(jsBody)).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")
      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "error_code").as[Int] must equalTo(4)
      (response \ "error_msg").as[String] must equalTo(s"卡号${card}未授权，请联系库贝人员。(Invalid card number)")
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

      private val jsBody = createAnExistingRelationship("0001234567", "13408654680", "1_1391836223533", 1)
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

      private val jsBody = createAnNewRelationship(cardNumber, parentPhone, child_id)
      val res = route(loggedRequest(POST, "/kindergarten/93740362/relationship/" + cardNumber).withBody(jsBody)).get

      status(res) must equalTo(OK)
      contentType(res) must beSome("application/json")

      val oldCard: JsValue = Json.parse(contentAsString(res))
      (oldCard \ "card").as[String] must equalTo(cardNumber)
      (oldCard \ "parent" \ "parent_id").as[String] must equalTo("2_93740362_789")
      (oldCard \ "child" \ "child_id").as[String] must equalTo(child_id)

      val res2 = route(loggedRequest(DELETE, "/kindergarten/93740362/relationship/" + cardNumber)).get
      status(res2) must equalTo(OK)

      private val newRelationship = createAnNewRelationship(cardNumber, parentPhone, "1_93740362_778")
      val res3 = route(loggedRequest(POST, "/kindergarten/93740362/relationship/" + cardNumber).withBody(newRelationship)).get

      status(res3) must equalTo(OK)
      contentType(res3) must beSome("application/json")
      val newCard: JsValue = Json.parse(contentAsString(res3))
      (newCard \ "card").as[String] must equalTo(cardNumber)
      (newCard \ "parent" \ "parent_id").as[String] must equalTo("2_93740362_789")
      (newCard \ "child"  \ "child_id").as[String] must equalTo("1_93740362_778")
    }

    "return all relationship in whole school" in new WithApplication {

      val res = route(loggedRequest(GET, "/kindergarten/93740362/relationship")).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")

      val responseArr: JsArray = Json.parse(contentAsString(res)).as[JsArray]
      responseArr.value.size must equalTo(7)
    }

    "reject existing relationship replacement" in new WithApplication {
      val cardNumber: String = ""
      val parentPhone: String = "13408654680"
      val child_id: String = "1_1391836223533"

      private val jsBody = createAnExistingRelationship(cardNumber, parentPhone, child_id, 10)
      val res = route(loggedRequest(POST, "/kindergarten/93740362/relationship").withBody(jsBody)).get

      status(res) must equalTo(BAD_REQUEST)
      contentType(res) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "error_code").as[Int] must equalTo(3)
    }

    "reject employee card updating attempt" in new WithApplication {
      val cardNumber: String = "0001112221"
      val parentPhone: String = "13408654681"
      val child_id: String = "1_1391836223533"

      private val jsBody = createAnNewRelationship(cardNumber, parentPhone, child_id)
      val res = route(loggedRequest(POST, "/kindergarten/93740362/relationship/" + "0001112221").withBody(jsBody)).get

      status(res) must equalTo(BAD_REQUEST)
      contentType(res) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "error_code").as[Int] must equalTo(7)
    }

    "reject employee card checking" in new WithApplication {
      val cardNumber: String = "0001112221"
      val parentPhone: String = "13408654681"
      val child_id: String = "1_1391836223533"

      private val jsBody = createAnNewRelationship(cardNumber, parentPhone, child_id)
      val res = route(loggedRequest(POST, "/api/v1/card_check").withBody(jsBody)).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "error_code").as[Int] must equalTo(3)
    }

    "allow fake card to update relationship" in new WithApplication {
      val parentPhone: String = "13402815317"
      private val childId: String = "1_93740362_778"
      private val newRelationship = createAnNewRelationship("", parentPhone, childId)
      val res = route(loggedRequest(POST, "/kindergarten/93740362/relationship").withBody(newRelationship)).get

      status(res) must equalTo(OK)
      contentType(res) must beSome("application/json")

      val newCard: JsValue = Json.parse(contentAsString(res))
      private val fakeCardNumber: String = (newCard \ "card").as[String]
      fakeCardNumber must startingWith("f")
      private val newCreated: Long = (newCard \ "id").as[Long]

      private val newRelationshipName: String = "新关系"
      private val updateRelationship = createAnExistingRelationship(fakeCardNumber, parentPhone, childId, newCreated, newRelationshipName)
      val res2 = route(loggedRequest(POST, "/kindergarten/93740362/relationship/" + fakeCardNumber).withBody(updateRelationship)).get

      status(res2) must equalTo(OK)
      contentType(res2) must beSome("application/json")

      val updatedCard: JsValue = Json.parse(contentAsString(res2))
      (updatedCard \ "relationship").as[String] must equalTo(newRelationshipName)
    }
  }

  implicit val write1 = Json.writes[Parent]
  implicit val write2 = Json.writes[ChildInfo]
  implicit val write3 = Json.writes[Relationship]

  def createAnExistingRelationship(card: String, phone: String, childId: String, id: Long, relationship: String = "妈妈"): JsValue = {
    Json.toJson(Relationship(
      Some(Parent(None, 0, "", phone, None, 0, "", None, None, None, None)),
      Some(ChildInfo(Some(childId), "", "", None, 0, None, 0, None, None, None)),
      card, relationship, Some(id)))
  }

  def createAnNewRelationship(card: String, phone: String, childId: String): JsValue = {
    Json.toJson(Relationship(
      Some(Parent(None, 0, "", phone, None, 0, "", None, None, None, None)),
      Some(ChildInfo(Some(childId), "", "", None, 0, None, 0, None, None, None)),
      card, "妈妈", None))
  }

  def createCard(card: String) = Json.toJson(Relationship(
    Some(Parent(None, 0, "", "", None, 0, "", None, None, None, None)),
    Some(ChildInfo(Some(""), "", "", None, 0, None, 0, None, None, None)),
    card, "", None))

  def newCard: JsValue = {
    createCard("9991234567")
  }

  def invalidCard: JsValue = {
    createAnNewRelationship("3331234567", "13408654681", "1_93740362_456")
  }

  def deletedCard: JsValue = {
    createCard("0091234567")
  }

  def existingCard: JsValue = {
    createCard("0001234567")
  }
}
