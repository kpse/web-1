package controllers

import _root_.helper.TestSupport
import models.ChargeInfo
import models.json_models.{CreatingSchool, PrincipalOfSchool}
import models.json_models.SchoolIntro._
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class SchoolSummaryControllerSpec extends Specification with TestSupport {
  def requestWithSession(method: String, url: String) = FakeRequest(method, url).withSession("id" -> "3_93740362_9971", "username" -> "admin")
  def requestByOperator(method: String, url: String) = FakeRequest(method, url).withSession("id" -> "3_93740362_9972", "username" -> "operator")

  "SchoolSummary" should {
    "check authentication first" in new WithApplication {

      val previewResponse = route(FakeRequest(GET, "/kindergarten/93740362/preview")).get

      status(previewResponse) must equalTo(UNAUTHORIZED)

    }
    "report preview" in new WithApplication {

      val previewResponse = route(requestWithSession(GET, "/kindergarten/93740362/preview")).get

      status(previewResponse) must equalTo(OK)
      contentType(previewResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(previewResponse))
      val first = response(0)
      (first \ "error_code").as[Int] must equalTo(0)
      (first \ "school_id").as[Long] must equalTo(93740362)
      (first \ "timestamp").as[Long] must greaterThan(0L)
    }

    "report detail" in new WithApplication {

      val detailResponse = route(requestWithSession(GET, "/kindergarten/93740362/detail")).get

      status(detailResponse) must equalTo(OK)
      contentType(detailResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(detailResponse))
      (response \ "error_code").as[Int] must equalTo(0)
      (response \ "school_id").as[Long] must equalTo(93740362)
      (response \ "school_info" \ "timestamp").as[Long] must greaterThan(0L)
      (response \ "school_info" \ "phone").as[String] must startingWith("13")
      (response \ "school_info" \ "school_logo_url").as[String] must startingWith("http")

    }
    "SchoolSummary" should {
      "be deleted by operator" in new WithApplication {

        val deleteResponse = route(requestByOperator(DELETE, "/kindergarten/93740362")).get

        status(deleteResponse) must equalTo(OK)

        val detailResponse = route(requestByOperator(GET, "/kindergarten/93740362/detail")).get

        status(detailResponse) must equalTo(NOT_FOUND)

        private val charge: ChargeInfo = ChargeInfo(93740362, 10, "2016-01-01", 1, 1)
        private val json: JsValue = Json.toJson(CreatingSchool(93740362, "100", "full name", "", PrincipalOfSchool("admin_this", "admin_this"), charge, "", Some("93740362")))
        val createResponse = route(requestByOperator(POST, "/kindergarten").withBody(json)).get

        status(createResponse) must equalTo(OK)
        val response: JsValue = Json.parse(contentAsString(createResponse))
        (response \ "school_id").as[Long] must equalTo(93740362)

        val detailResponse2 = route(requestByOperator(GET, "/kindergarten/93740362/detail")).get

        status(detailResponse2) must equalTo(OK)
        val r2: JsValue = Json.parse(contentAsString(detailResponse2))
        (r2 \ "school_id").as[Long] must equalTo(93740362)

      }

      "not be deleted by teacher" in new WithApplication {

        val deleteResponse = route(requestWithSession(DELETE, "/kindergarten/93740362")).get

        status(deleteResponse) must equalTo(UNAUTHORIZED)
      }

      "delete all teachers as well" in new WithApplication {

        val deleteResponse = route(requestByOperator(DELETE, "/kindergarten/93740362")).get

        status(deleteResponse) must equalTo(OK)

        val notFoundResponse = route(requestWithSession(GET, "/kindergarten/93740362/employee/13060003722")).get

        status(notFoundResponse) must equalTo(UNAUTHORIZED)
      }
    }

  }
}
