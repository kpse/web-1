package controllers

import _root_.helper.TestSupport
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.libs.json.{JsArray, JsValue, Json}
import models.{PhoneCheck, Parent}

@RunWith(classOf[JUnitRunner])
class SessionControllerSpec extends Specification with TestSupport {
  implicit val writes = Json.writes[Parent]
  implicit val read = Json.reads[PhoneCheck]
  implicit val writes2 = Json.writes[PhoneCheck]

  def principalLoggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("id" -> "3_93740362_1122", "username" -> "e0001")
  }

  def superAdminLoggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("id" -> "3_93740362_9972", "username" -> "operator")
  }

  def ownerTeacherRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("id" -> "3_93740362_3344", "username" -> "e0002")
  }

  def noAccessLoggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("id" -> "3_93740362_9977", "username" -> "e0003")
  }

  def ownerParentRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "13408654681", "token" -> "1386849160798")
  }
  
  def otherParentRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "13408654680", "token" -> "1386849160798")
  }

  "Session" should {
    "check authentication first" in new WithApplication {

      val response = route(FakeRequest(DELETE, "/kindergarten/93740362/session/1_93740362_9982/record/4")).get

      status(response) must equalTo(SEE_OTHER)

    }

    "be able to delete by owner teacher" in new WithApplication {

      val response = route(ownerTeacherRequest(DELETE, "/kindergarten/93740362/session/1_93740362_9982/record/5")).get

      status(response) must equalTo(OK)
    }
    "be able to delete by owner parent" in new WithApplication {

      val response = route(ownerParentRequest(DELETE, "/kindergarten/93740362/session/1_93740362_9982/record/2")).get

      status(response) must equalTo(OK)
    }

    "be able to delete by principal" in new WithApplication {

      val response = route(principalLoggedRequest(DELETE, "/kindergarten/93740362/session/1_93740362_9982/record/12")).get

      status(response) must equalTo(OK)
    }

    "be able to delete by super admin" in new WithApplication {

      val response = route(superAdminLoggedRequest(DELETE, "/kindergarten/93740362/session/1_93740362_9982/record/3")).get
      status(response) must equalTo(OK)
    }

    "not be able to delete by other teacher" in new WithApplication {

      val response = route(noAccessLoggedRequest(DELETE, "/kindergarten/93740362/session/1_93740362_9982/record/4")).get
      status(response) must equalTo(FORBIDDEN)
    }

    "not be able to delete by other parent" in new WithApplication {

      val response = route(otherParentRequest(DELETE, "/kindergarten/93740362/session/1_93740362_9982/record/4")).get
      status(response) must equalTo(FORBIDDEN)
    }


  }
}
