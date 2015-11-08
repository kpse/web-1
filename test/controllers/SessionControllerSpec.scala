package controllers

import _root_.helper.TestSupport
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import play.Logger
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.libs.json.{JsArray, JsValue, Json}
import models._

@RunWith(classOf[JUnitRunner])
class SessionControllerSpec extends Specification with TestSupport {
  implicit val writes = Json.writes[Parent]
  implicit val read = Json.reads[ParentPhoneCheck]
  implicit val writes2 = Json.writes[ParentPhoneCheck]
  implicit val writes3 = Json.writes[Sender]
  implicit val writes4 = Json.writes[MediaContent]
  implicit val writes5 = Json.writes[ChatSession]

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

      val response = route(ownerTeacherRequest(DELETE, "/kindergarten/93740362/session/1_93740362_9982/record/40")).get

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

    "not trust client timestamp when creating session" in new WithApplication {
      private val clientTimestamp: Long = 1000
      val newSession = ChatSession("1_1391836223533", Some(clientTimestamp), None, "test", None, Sender("3_93740362_3344"))
      val response = route(ownerTeacherRequest(POST, "/kindergarten/93740362/session/1_1391836223533/record").withBody(Json.toJson(newSession))).get
      status(response) must equalTo(OK)

      val creating: JsValue = Json.parse(contentAsString(response))
      (creating \ "timestamp").as[Long] must not equalTo clientTimestamp
    }

    "not trust client timestamp when creating history" in new WithApplication {
      private val clientTimestamp: Long = 1000
      val newHistory = ChatSession("h_1_1391836223533", Some(clientTimestamp), None, "test", None, Sender("3_93740362_3344"))
      val response = route(ownerTeacherRequest(POST, "/kindergarten/93740362/history/1_1391836223533/record").withBody(Json.toJson(newHistory))).get
      status(response) must equalTo(OK)

      val creating: JsValue = Json.parse(contentAsString(response))
      (creating \ "timestamp").as[Long] must not equalTo clientTimestamp
    }

    "sync all timestamps in batch history creating" in new WithApplication {
      val newHistory = ChatSession("", Some(123), None, "test", None, Sender("3_93740362_3344"))
      val response = route(ownerTeacherRequest(POST, "/kindergarten/93740362/history?child_id=1_1391836223533,1_93740362_456").withBody(Json.toJson(newHistory))).get
      status(response) must equalTo(OK)

      val response1 = route(ownerTeacherRequest(GET, "/kindergarten/93740362/history/1_1391836223533/record?most=1")).get
      val response2 = route(ownerTeacherRequest(GET, "/kindergarten/93740362/history/1_93740362_456/record?most=1")).get

      val creating1: Seq[JsValue] = Json.parse(contentAsString(response1)).as[JsArray].value
      val creating2: Seq[JsValue] = Json.parse(contentAsString(response2)).as[JsArray].value
      private val firstTimestamp: Long = (creating1.head \ "timestamp").as[Long]
      private val secondTimestamp: Long = (creating2.head \ "timestamp").as[Long]
      firstTimestamp must equalTo(secondTimestamp)
    }


  }
}
