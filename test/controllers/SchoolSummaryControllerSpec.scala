package controllers

import _root_.helper.TestSupport
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import play.api.test.FakeRequest
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class SchoolSummaryControllerSpec extends Specification with TestSupport {
  def requestWithSession(method: String, url: String) = FakeRequest(method, url).withSession("id" -> "3_93740362_9971", "username" -> "admin")

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

  }
}
