package controllers.V5

import helper.TestSupport
import models.Parent
import models.V5.{NewParent, Invitation}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.{JsArray, Json, JsValue}
import play.api.test.FakeRequest
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class InvitationControllerSpec extends Specification with TestSupport {
  def loggedOneChildParent(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "13408654680", "token" -> "1386849160798")
  }
  
  def loggedTwoChildrenParent(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "13408654681", "token" -> "1386849160798")
  }

  "InvitationController" should {
    "create the same relationships as it's host" in new WithApplication {

      val invitationRes = route(loggedOneChildParent(POST, "/api/v5/kindergarten/93740362/invitation").withBody(theHost())).get

      status(invitationRes) must equalTo(OK)
      contentType(invitationRes) must beSome.which(_ == "application/json")
      val response: JsArray = Json.parse(contentAsString(invitationRes)).as[JsArray]
      (response(0) \ "card").as[String] must not be empty
      (response(0) \ "parent" \ "phone").as[String] must equalTo(newPhone)
      (response(0) \ "child" \ "child_id").as[String] must equalTo("1_1391836223533")

    }

    "create as many relationships as it's host" in new WithApplication {

      val invitationRes = route(loggedTwoChildrenParent(POST, "/api/v5/kindergarten/93740362/invitation").withBody(twoRelationshipsHost)).get

      status(invitationRes) must equalTo(OK)
      contentType(invitationRes) must beSome.which(_ == "application/json")
      val response: JsArray = Json.parse(contentAsString(invitationRes)).as[JsArray]
      response.value.size must equalTo(2)
      (response(0) \ "child" \ "child_id").as[String] must equalTo("1_93740362_9982")
      (response(1) \ "child" \ "child_id").as[String] must equalTo("1_93740362_778")

    }
    
    "check phone number before accept the invitation" in new WithApplication {

      val invitationRes = route(loggedOneChildParent(POST, "/api/v5/kindergarten/93740362/invitation").withBody(twoRelationshipsHost)).get

      status(invitationRes) must equalTo(INTERNAL_SERVER_ERROR)
      contentType(invitationRes) must beSome.which(_ == "application/json")
    }

    "check invitee phone number is not existing" in new WithApplication {

      val invitationRes = route(loggedOneChildParent(POST, "/api/v5/kindergarten/93740362/invitation").withBody(theHost(aExistingPhone))).get

      status(invitationRes) must equalTo(INTERNAL_SERVER_ERROR)
      contentType(invitationRes) must beSome.which(_ == "application/json")
    }
  }
  val newPhone: String = "13321147894"
  val aExistingPhone: String = "13408654681"
  def theHost(phone: String = newPhone): JsValue  = {
    Json.toJson(Invitation(Parent.findById(93740362, "2_93740362_789").get, NewParent(phone, "搜索", "妈妈")))
  }

  def twoRelationshipsHost: JsValue  = {
    Json.toJson(Invitation(Parent.findById(93740362, "2_93740362_792").get, NewParent(newPhone, "搜索", "妈妈")))
  }

}
