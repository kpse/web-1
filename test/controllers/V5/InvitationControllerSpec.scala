package controllers.V5

import helper.TestSupport
import models.{CheatCode, Verification, Parent}
import models.V5.{InvitationCode, InvitationPhoneKey, NewParent, Invitation}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.Logger
import play.api.libs.json.{JsArray, Json, JsValue}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.cache.Cache

@RunWith(classOf[JUnitRunner])
class InvitationControllerSpec extends Specification with TestSupport {
  def loggedOneChildParent(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "13408654680", "token" -> "1386849160798")
  }
  
  def loggedTwoChildrenParent(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "13408654681", "token" -> "1386849160798")
  }

  "InvitationController" should {
    "check authentication first" in new WithApplication {
      val response = route(FakeRequest(POST, "/api/v5/kindergarten/93740362/invitation").withBody(theHost())).get

      status(response) must equalTo(UNAUTHORIZED)
    }

    "create the same relationships as it's host" in new WithApplication {
      setUpVerification()
      val invitationRes = route(loggedOneChildParent(POST, "/api/v5/kindergarten/93740362/invitation").withBody(theHost())).get

      status(invitationRes) must equalTo(OK)
      contentType(invitationRes) must beSome.which(_ == "application/json")
      val response: JsArray = Json.parse(contentAsString(invitationRes)).as[JsArray]
      private val fakeCard: String = (response(0) \ "card").as[String]
      fakeCard must not be empty
      (response(0) \ "parent" \ "phone").as[String] must equalTo(newPhone)
      (response(0) \ "child" \ "child_id").as[String] must equalTo("1_1391836223533")

      val parentRes = route(loggedOneChildParent(GET, "/kindergarten/93740362/parent/" + newPhone)).get
      status(parentRes) must equalTo(OK)
      contentType(parentRes) must beSome.which(_ == "application/json")

      val parent: JsValue = Json.parse(contentAsString(parentRes))

      (parent \ "phone").as[String] must equalTo(newPhone)
      (parent \ "name").as[String] must equalTo(newName)

      val relationRes = route(loggedOneChildParent(GET, "/kindergarten/93740362/relationship/" + fakeCard)).get
      status(relationRes) must equalTo(OK)
      contentType(relationRes) must beSome.which(_ == "application/json")

      val relationship: JsValue = Json.parse(contentAsString(relationRes))

      (relationship \ "card").as[String] must equalTo(fakeCard)
      (relationship \ "parent" \ "phone").as[String] must equalTo(newPhone)
      (relationship \ "parent" \ "name").as[String] must equalTo(newName)
      (relationship \ "child" \ "child_id").as[String] must equalTo("1_1391836223533")
      (relationship \ "relationship").as[String] must equalTo(newRelationship)

    }

    "create as many relationships as it's host" in new WithApplication {
      setUpVerification()
      val invitationRes = route(loggedTwoChildrenParent(POST, "/api/v5/kindergarten/93740362/invitation").withBody(twoRelationshipsHost)).get

      status(invitationRes) must equalTo(OK)
      contentType(invitationRes) must beSome.which(_ == "application/json")
      val response: JsArray = Json.parse(contentAsString(invitationRes)).as[JsArray]
      response.value.size must equalTo(2)
      (response(0) \ "child" \ "child_id").as[String] must equalTo("1_93740362_9982")
      (response(1) \ "child" \ "child_id").as[String] must equalTo("1_93740362_778")

    }
    
    "check phone number before accept the invitation" in new WithApplication {
      setUpVerification()
      val error = route(loggedOneChildParent(POST, "/api/v5/kindergarten/93740362/invitation").withBody(twoRelationshipsHost)).get

      status(error) must equalTo(INTERNAL_SERVER_ERROR)
      contentType(error) must beSome.which(_ == "application/json")
    }

    "check invitee phone number is not existing" in new WithApplication {
      setUpVerification()
      val error = route(loggedOneChildParent(POST, "/api/v5/kindergarten/93740362/invitation").withBody(theHost(aExistingPhone))).get

      status(error) must equalTo(INTERNAL_SERVER_ERROR)
      contentType(error) must beSome.which(_ == "application/json")
    }

    "accept deleted phone number as a new parent" in new WithApplication {
      setUpVerification(correctCode.copy(phone = aDeletedPhone))
      val success = route(loggedOneChildParent(POST, "/api/v5/kindergarten/93740362/invitation").withBody(theHost(aDeletedPhone, correctCode.copy(phone = aDeletedPhone)))).get

      status(success) must equalTo(OK)
      contentType(success) must beSome.which(_ == "application/json")

      val response: JsArray = Json.parse(contentAsString(success)).as[JsArray]
      response.value.size must equalTo(1)
      (response(0) \ "child" \ "child_id").as[String] must equalTo("1_1391836223533")
    }

    "reject while wrong code" in new WithApplication {
      setUpVerification(wrongCode)
      val error = route(loggedOneChildParent(POST, "/api/v5/kindergarten/93740362/invitation").withBody(theHost())).get

      status(error) must equalTo(INTERNAL_SERVER_ERROR)
      contentType(error) must beSome.which(_ == "application/json")
    }

    "reject while no code" in new WithApplication {

      val error = route(loggedOneChildParent(POST, "/api/v5/kindergarten/93740362/invitation").withBody(noCodeRequest)).get

      status(error) must equalTo(INTERNAL_SERVER_ERROR)
      contentType(error) must beSome.which(_ == "application/json")
    }

    "reject while no code in app cache" in new WithApplication {

      val error = route(loggedOneChildParent(POST, "/api/v5/kindergarten/93740362/invitation").withBody(theHost())).get

      status(error) must equalTo(INTERNAL_SERVER_ERROR)
      contentType(error) must beSome.which(_ == "application/json")
    }

    "reject while code is for other host" in new WithApplication {
      setUpVerification()
      val error = route(loggedOneChildParent(POST, "/api/v5/kindergarten/93740362/invitation").withBody(wrongCodeRequest)).get

      status(error) must equalTo(INTERNAL_SERVER_ERROR)
      contentType(error) must beSome.which(_ == "application/json")
    }

  }
  val newPhone: String = "13321147894"
  val aExistingPhone: String = "13408654681"
  val aDeletedPhone: String = "22222222226"
  private val newName: String = "搜索"

  private val newRelationship: String = "妈妈"

  def theHost(phone: String = newPhone, code: Verification = correctCode): JsValue  = {
    Json.toJson(Invitation(Parent.findById(93740362, "2_93740362_789").get, NewParent(phone, newName, newRelationship), Some(code)))
  }

  def noCodeRequest: JsValue  = {
    theHost(newPhone, Verification("", ""))
  }

  def wrongCodeRequest: JsValue  = {
    theHost(newPhone, wrongCode)
  }

  def twoRelationshipsHost: JsValue  = {
    Json.toJson(Invitation(Parent.findById(93740362, "2_93740362_792").get, NewParent(newPhone, newName, newRelationship), Some(correctCode)))
  }

  val correctCode = Verification(newPhone, "123456")
  val wrongCode = Verification("13408654680", "123456")

  implicit def stringToInvitationPhoneKey(phone: String): InvitationPhoneKey = InvitationPhoneKey(phone)

  def setUpVerification(v: Verification = correctCode) = {
    Cache.set(v.phone.iKey, InvitationCode(v.phone, v.code, System.currentTimeMillis, None))
  }

}
