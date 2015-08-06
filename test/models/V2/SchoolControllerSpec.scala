package models.V2

import helper.TestSupport
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.{JsArray, JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class SchoolControllerSpec extends Specification with TestSupport {
  def requestWithSession(method: String, url: String) = FakeRequest(method, url).withSession("id" -> "3_93740362_9971", "username" -> "admin")

  def requestByOperator(method: String, url: String) = FakeRequest(method, url).withSession("id" -> "3_93740362_9972", "username" -> "operator")

  "preview all ids" in new WithApplication {

    val previewResponse = route(requestByOperator(GET, "/api/v2/kindergarten_preview")).get

    status(previewResponse) must equalTo(OK)
    contentType(previewResponse) must beSome.which(_ == "application/json")

    val response: JsValue = Json.parse(contentAsString(previewResponse))
    val first = response(0)
    (first \ "error_code").as[Int] must equalTo(0)
    (first \ "school_id").as[Long] must equalTo(93740362)
    (first \ "timestamp").as[Long] must greaterThan(0L)
  }

  "should support from in v2" in new WithApplication {

    val pagingResponse = route(requestByOperator(GET, "/api/v2/kindergarten?from=93740361&most=1")).get

    status(pagingResponse) must equalTo(OK)
    contentType(pagingResponse) must beSome.which(_ == "application/json")

    val response: JsValue = Json.parse(contentAsString(pagingResponse))
    val first = response(0)
    (first \ "school_id").as[Long] must equalTo(93740362)
    (first \ "timestamp").as[Long] must greaterThan(0L)
  }

  "should support to in v2" in new WithApplication {

    val pagingResponse = route(requestByOperator(GET, "/api/v2/kindergarten?from=93740362&to=93740563")).get

    status(pagingResponse) must equalTo(OK)
    contentType(pagingResponse) must beSome.which(_ == "application/json")

    val response: JsValue = Json.parse(contentAsString(pagingResponse))
    val first = response(0)
    (first \ "school_id").as[Long] must equalTo(93740562)
    (first \ "timestamp").as[Long] must greaterThan(0L)
  }

  "should support most in v2" in new WithApplication {

    val pagingResponse = route(requestByOperator(GET, "/api/v2/kindergarten?most=3")).get

    status(pagingResponse) must equalTo(OK)
    contentType(pagingResponse) must beSome.which(_ == "application/json")

    val response: JsValue = Json.parse(contentAsString(pagingResponse))
    response.as[JsArray].value.size must equalTo(3)
  }
}
