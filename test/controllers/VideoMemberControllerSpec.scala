package controllers

import _root_.helper.TestSupport
import models.{Parent, ParentPhoneCheck, Relationship, VideoMember}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class VideoMemberControllerSpec extends Specification with TestSupport {
  implicit val writes = Json.writes[Parent]
  implicit val read = Json.reads[ParentPhoneCheck]
  implicit val writes2 = Json.writes[ParentPhoneCheck]

  def ownerTeacherRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("id" -> "3_93740362_3344", "username" -> "e0002")
  }

  def superAdminRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("id" -> "3_93740362_9972", "username" -> "operator")
  }


  "VideoMember" should {
    "check authentication first" in new WithApplication {

      val response = route(FakeRequest(GET, "/api/v1/kindergarten/93740362/video_member")).get

      status(response) must equalTo(UNAUTHORIZED)

    }

    "list for teacher" in new WithApplication {

      val response = route(ownerTeacherRequest(GET, "/api/v1/kindergarten/93740362/video_member")).get

      status(response) must equalTo(OK)
    }

    "be created by teacher" in new WithApplication {

      private val json: JsValue = Json.toJson(VideoMember("100", None, None, Some(93740362)))
      val createResponse = route(ownerTeacherRequest(POST, "/api/v1/kindergarten/93740362/video_member").withBody(json)).get

      status(createResponse) must equalTo(OK)

      val response = route(ownerTeacherRequest(GET, "/api/v1/kindergarten/93740362/video_member/100")).get

      status(response) must equalTo(OK)
    }

    "be updated by teacher" in new WithApplication {

      private val json: JsValue = Json.toJson(VideoMember("13", Some("account_name"), None, Some(93740362)))

      val createResponse = route(ownerTeacherRequest(POST, "/api/v1/kindergarten/93740362/video_member/13").withBody(json)).get

      status(createResponse) must equalTo(OK)

      val response = route(ownerTeacherRequest(GET, "/api/v1/kindergarten/93740362/video_member/13")).get

      status(response) must equalTo(OK)
      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      (jsonResponse \ "account").as[String] must equalTo("account_name")
    }

    "not be created if account duplicated" in new WithApplication {

      private val json: JsValue = Json.toJson(VideoMember("1993", Some("699A1A35755AF4940F2726AA291028E5"), None, Some(93740362)))

      val createResponse = route(ownerTeacherRequest(POST, "/api/v1/kindergarten/93740362/video_member").withBody(json)).get

      status(createResponse) must equalTo(BAD_REQUEST)
    }

    "not be created if parent id duplicated" in new WithApplication {

      private val json: JsValue = Json.toJson(VideoMember("2_93740362_123", None, None, Some(93740562)))

      val createResponse = route(superAdminRequest(POST, "/api/v1/kindergarten/93740562/video_member").withBody(json)).get

      status(createResponse) must equalTo(BAD_REQUEST)

      val jsonResponse: JsValue = Json.parse(contentAsString(createResponse))
      (jsonResponse \ "error_code").as[Int] must equalTo(12)
    }

    "not be updated if account duplicated" in new WithApplication {

      private val json: JsValue = Json.toJson(VideoMember("14", Some("699A1A35755AF4940F2726AA291028E5"), None, Some(93740362)))

      val createResponse = route(ownerTeacherRequest(POST, "/api/v1/kindergarten/93740362/video_member/14").withBody(json)).get

      status(createResponse) must equalTo(BAD_REQUEST)

    }

    "pass checking with existing account" in new WithApplication {

      val checkResponse = route(ownerTeacherRequest(GET, "/api/v1/kindergarten/93740362/check_video_member?account=699A1A35755AF4940F2726AA291028E5")).get

      status(checkResponse) must equalTo(OK)
      private val parsed: JsValue = Json.parse(contentAsString(checkResponse))
      (parsed \ "error_code").as[Int] must equalTo(0)

    }

    "not pass checking with non-existing account" in new WithApplication {

      val checkResponse = route(ownerTeacherRequest(GET, "/api/v1/kindergarten/93740362/check_video_member?account=123")).get

      status(checkResponse) must equalTo(OK)
      private val parsed: JsValue = Json.parse(contentAsString(checkResponse))
      (parsed \ "error_code").as[Int] must equalTo(1)

    }

    "not list without token" in new WithApplication {

      val response = route(FakeRequest(GET, "/api/v1/video_member")).get

      status(response) must equalTo(BAD_REQUEST)
    }

    "list with token" in new WithApplication {

      val response = route(FakeRequest(GET, "/api/v1/video_member?token=3FDE6BB0541387E4EBDADF7C2FF31123")).get

      status(response) must equalTo(OK)
    }

    "not list with wrong token" in new WithApplication {

      val response = route(FakeRequest(GET, "/api/v1/video_member?token=3")).get

      status(response) must equalTo(FORBIDDEN)
    }


    "be toggled by teacher" in new WithApplication {

      private val json: JsValue = Json.toJson(VideoMember("100", None, None, Some(93740362)))
      val createResponse = route(ownerTeacherRequest(POST, "/api/v1/kindergarten/93740362/video_member").withBody(json)).get

      status(createResponse) must equalTo(OK)

      val response = route(ownerTeacherRequest(GET, "/api/v1/kindergarten/93740362/video_member/100")).get

      status(response) must equalTo(OK)

      val deleteResponse = route(ownerTeacherRequest(DELETE, "/api/v1/kindergarten/93740362/video_member/100").withBody(json)).get

      status(deleteResponse) must equalTo(OK)

      val notFoundResponse = route(ownerTeacherRequest(GET, "/api/v1/kindergarten/93740362/video_member/100")).get

      status(notFoundResponse) must equalTo(NOT_FOUND)

      val createResponse2 = route(ownerTeacherRequest(POST, "/api/v1/kindergarten/93740362/video_member").withBody(json)).get

      status(createResponse2) must equalTo(OK)

      val response2 = route(ownerTeacherRequest(GET, "/api/v1/kindergarten/93740362/video_member/100")).get

      status(response2) must equalTo(OK)

    }

  }
}
