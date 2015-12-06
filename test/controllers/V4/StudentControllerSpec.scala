package controllers.V4

import helper.TestSupport
import models.json_models.CheckInfo
import org.joda.time.DateTime
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.{JsArray, JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
object StudentControllerSpec extends Specification with TestSupport {
  def loggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "13408654680", "token" -> "1386849160798")
  }

  "Student" should {
    "check authentication first" in new WithApplication {
      val response = route(FakeRequest(GET, "/api/v4/kindergarten/123/student/123")).get

      status(response) must equalTo(UNAUTHORIZED)
    }

    "ignore check before today" in new WithApplication {
      private val busOnBoarding: Long = CheckInfo(93740362, "0001234567", 0, 1, "", DateTime.now.minusDays(7).getMillis).create.size
      busOnBoarding must be greaterThan 1

      val res = route(loggedRequest(GET, "/api/v4/kindergarten/93740362/student/1")).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "check_status").as[Option[String]] must beSome("out")
    }

    "ignore check before today 2" in new WithApplication {
      private val busOnBoarding: Long = CheckInfo(93740362, "0001234567", 0, 1, "", DateTime.now.minusDays(7).getMillis).create.size
      busOnBoarding must be greaterThan 1

      private val busOnBoardingToday: Long = CheckInfo(93740362, "0001234567", 0, 1, "", DateTime.now.getMillis).create.size
      busOnBoardingToday must be greaterThan 1

      val res = route(loggedRequest(GET, "/api/v4/kindergarten/93740362/student/1")).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "check_status").as[Option[String]] must beSome("in")
    }
  }
}

