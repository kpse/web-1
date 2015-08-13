package controllers.V3

import helper.TestSupport
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.{Json, JsValue}
import play.api.test.FakeRequest
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
object EmployeeCardControllerSpec extends Specification with TestSupport {
  def loggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "13408654680", "token" -> "1386849160798")
  }

  "EmployeeCard" should {
    "check authentication first" in new WithApplication {
      val response = route(FakeRequest(GET, "/api/v3/kindergarten/123/card_check/123")).get

      status(response) must equalTo(UNAUTHORIZED)

    }

    "check card is occupied by employees" in new WithApplication {

      val res = route(loggedRequest(GET, "/api/v3/kindergarten/93740362/card_check/0001112221")).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "error_code").as[Long] must equalTo(2)
    }

    "check card is occupied by relatives" in new WithApplication {

      val res = route(loggedRequest(GET, "/api/v3/kindergarten/93740362/card_check/0001234567")).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "error_code").as[Long] must equalTo(1)
    }

    "identify invalid card" in new WithApplication {

      val res = route(loggedRequest(GET, "/api/v3/kindergarten/93740362/card_check/0009999991")).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "error_code").as[Long] must equalTo(3)
    }

    "treat relative card as free when proper id is given" in new WithApplication {

      val res = route(loggedRequest(GET, "/api/v3/kindergarten/93740362/card_check/0001234567?id=1")).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "error_code").as[Long] must equalTo(0)
    }

    "treat relative card as occupied when wrong id is given" in new WithApplication {

      val res = route(loggedRequest(GET, "/api/v3/kindergarten/93740362/card_check/0001234567?id=2")).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "error_code").as[Long] must equalTo(1)
    }

    "treat employee card as occupied when wrong id is given" in new WithApplication {

      val res = route(loggedRequest(GET, "/api/v3/kindergarten/93740362/card_check/0001112221?id=2")).get

      status(res) must equalTo(OK)
      contentType(res) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(res))
      (response \ "error_code").as[Long] must equalTo(2)
    }
  }
}