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
      private val jsBody = createRelationship("0001234567", "13408654680", "1_1391836223533", 1)
      val res = route(loggedRequest(POST, "/kindergarten/93740362/relationship/0001234567").withBody(jsBody)).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "card").as[String] must equalTo("0001234567")
    }

  }

  implicit val write1 = Json.writes[Parent]
  implicit val write2 = Json.writes[ChildInfo]
  implicit val write3 = Json.writes[Relationship]

  def createRelationship(card: String, phone: String, childId: String, id: Long): JsValue = {
    Json.toJson(Relationship(
      Some(Parent(None, 0, "", phone, None, 0, "", None, None, None, None)),
      Some(ChildInfo(Some(childId), "", "", "", 0, None, 0, None, None, None)),
      card, "妈妈", Some(id)))
  }
}
