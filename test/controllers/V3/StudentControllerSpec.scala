package controllers.V3

import helper.TestSupport
import models.json_models.CheckInfo
import org.joda.time.DateTime
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.{JsArray, JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import models.json_models.CheckingMessage.checkInfoWrites

@RunWith(classOf[JUnitRunner])
object StudentControllerSpec extends Specification with TestSupport {
  def loggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "13408654680", "token" -> "1386849160798")
  }

  "Student" should {
    "check authentication first" in new WithApplication {
      val response = route(FakeRequest(GET, "/api/v3/kindergarten/123/student/123")).get

      status(response) must equalTo(UNAUTHORIZED)

    }

    "report check status in multiple retrieval" in new WithApplication {
      val res = route(loggedRequest(GET, "/api/v3/kindergarten/93740362/student")).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")

      val response: Seq[JsValue] = Json.parse(contentAsString(res)).as[JsArray].value
      response.size must be greaterThan 1
      response.foreach (r => (r \ "check_status").as[Option[String]] must beSome("out"))
    }

    "report check status in multiple retrieval" in new WithApplication {
      private val created: Option[Long] = CheckInfo(93740362, "0001234567", 0, 0, "", DateTime.now.withHourOfDay(7).getMillis).create
      created.get must be greaterThan 1
      val res = route(loggedRequest(GET, "/api/v3/kindergarten/93740362/student")).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")

      val response: Seq[JsValue] = Json.parse(contentAsString(res)).as[JsArray].value
      response.size must be greaterThan 1
      response.exists(r => (r \ "check_status").as[Option[String]] == Some("in")) must beTrue
    }

    "report check status in single retrieval" in new WithApplication {
      val res = route(loggedRequest(GET, "/api/v3/kindergarten/93740362/student/1")).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "check_status").as[Option[String]] must beSome("out")
    }

    "update check status according to checking times in current day" in new WithApplication {
      private val created: Option[Long] = CheckInfo(93740362, "0001234567", 0, 0, "", DateTime.now.withHourOfDay(7).getMillis).create
      created.get must be greaterThan 1

      val res = route(loggedRequest(GET, "/api/v3/kindergarten/93740362/student/1")).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "check_status").as[Option[String]] must beSome("in")
    }

    "check twice means leaving school" in new WithApplication {
      private val first: Option[Long] = CheckInfo(93740362, "0001234567", 0, 0, "", DateTime.now.withHourOfDay(7).getMillis).create
      first.get must be greaterThan 1
      private val second: Option[Long] = CheckInfo(93740362, "0001234567", 0, 0, "", DateTime.now.withHourOfDay(7).getMillis).create
      second.get must be greaterThan first.get

      val res = route(loggedRequest(GET, "/api/v3/kindergarten/93740362/student/1")).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "check_status").as[Option[String]] must beSome("out")
    }

    "ignore bus on boarding check" in new WithApplication {
      private val busOnBoarding: Option[Long] = CheckInfo(93740362, "0001234567", 0, 10, "", DateTime.now.withHourOfDay(7).getMillis).create
      busOnBoarding.get must be greaterThan 1

      val res = route(loggedRequest(GET, "/api/v3/kindergarten/93740362/student/1")).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "check_status").as[Option[String]] must beSome("out")
    }

    "ignore bus leaving check" in new WithApplication {
      private val busOnBoarding: Option[Long] = CheckInfo(93740362, "0001234567", 0, 13, "", DateTime.now.withHourOfDay(7).getMillis).create
      busOnBoarding.get must be greaterThan 1

      val res = route(loggedRequest(GET, "/api/v3/kindergarten/93740362/student/1")).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "check_status").as[Option[String]] must beSome("out")
    }
  }
}

