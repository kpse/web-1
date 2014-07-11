package controllers

import _root_.helper.TestSupport
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.libs.json.{JsArray, JsValue, Json}
import models.{PhoneCheck, ChildInfo, Parent}

@RunWith(classOf[JUnitRunner])
class ParentControllerSpec extends Specification with TestSupport {
  implicit val writes = Json.writes[Parent]
  implicit val read = Json.reads[PhoneCheck]
  implicit val writes2 = Json.writes[PhoneCheck]

  def principalLoggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("id" -> "3_93740362_1122", "username" -> "e0001")
  }

  def twoClassesManagerLoggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("id" -> "3_93740362_3344", "username" -> "e0002")
  }

  def noAccessLoggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("id" -> "3_93740362_9977", "username" -> "e0003")
  }

  "Parent" should {
    "check authentication first" in new WithApplication {

      val response = route(FakeRequest(GET, "/kindergarten/93740362/parent")).get

      status(response) must equalTo(UNAUTHORIZED)

    }

    "show all parents to principal" in new WithApplication {

      val response = route(principalLoggedRequest(GET, "/kindergarten/93740362/parent")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must greaterThanOrEqualTo(7)
          (arr(0) \ "parent_id").as[String].isEmpty must beFalse
        case _ => failure
      }
    }

    "show partial parents to manager" in new WithApplication {

      val response = route(twoClassesManagerLoggedRequest(GET, "/kindergarten/93740362/parent")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must lessThan(8)
          (arr(0) \ "parent_id").as[String] must equalTo("2_93740362_789")
        case _ => failure
      }
    }

    "show no parent to no access teacher" in new WithApplication {

      val response = route(noAccessLoggedRequest(GET, "/kindergarten/93740362/parent")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must beEqualTo(0)
        case _ => failure
      }
    }

    "show no parent to no access teacher even specific class id" in new WithApplication {

      val response = route(noAccessLoggedRequest(GET, "/kindergarten/93740362/parent?class_id=777666")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must beEqualTo(0)
        case _ => failure
      }
    }

    "show no parent to manager when specific non-managed class" in new WithApplication {

      val response = route(twoClassesManagerLoggedRequest(GET, "/kindergarten/93740362/parent?class_id=777999")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must beEqualTo(0)
        case _ => failure
      }
    }

    "show all parents to manager in specific class" in new WithApplication {

      val response = route(twoClassesManagerLoggedRequest(GET, "/kindergarten/93740362/parent?class_id=777666")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must beEqualTo(3)
        case _ => failure
      }
    }

    "show all parents to principal in specific class" in new WithApplication {

      val response = route(principalLoggedRequest(GET, "/kindergarten/93740362/parent?class_id=777666")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must equalTo(3)
        case _ => failure
      }
    }

    "show all unconnected parents to no privilege teachers" in new WithApplication {

      val response = route(noAccessLoggedRequest(GET, "/kindergarten/93740362/parent?connected=false")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must equalTo(3)
        case _ => failure
      }
    }

    "show no parent to no privilege teachers if they wanna check connected parants" in new WithApplication {

      val response = route(noAccessLoggedRequest(GET, "/kindergarten/93740362/parent?connected=true")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.isEmpty must beTrue
        case _ => failure
      }
    }

    "show all unconnected parents to all kinds of privilege: manager" in new WithApplication {

      val response = route(twoClassesManagerLoggedRequest(GET, "/kindergarten/93740362/parent?connected=false")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must equalTo(3)
        case _ => failure
      }
    }

    "show all unconnected parents to all kinds of privilege: principal" in new WithApplication {

      val response = route(principalLoggedRequest(GET, "/kindergarten/93740362/parent?connected=false")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must equalTo(3)
        case _ => failure
      }
    }

    "phone check successfully when phone number does not exist" in new WithApplication {
      private val requestBody = Json.toJson(PhoneCheck(None, "1234567"))

      val checkResponse = route(principalLoggedRequest(POST, "/api/v1/phone_check/1234567").withJsonBody(requestBody)).get

      status(checkResponse) must equalTo(OK)
      contentType(checkResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(checkResponse))
      (response \ "error_code").as[Int] must equalTo(0)

    }

    "phone check successfully when phone number and id are matching" in new WithApplication {
      private val requestBody = Json.toJson(PhoneCheck(Some("2_93740362_123"), "13402815317"))

      val checkResponse = route(principalLoggedRequest(POST, "/api/v1/phone_check/13402815317").withJsonBody(requestBody)).get

      status(checkResponse) must equalTo(OK)
      contentType(checkResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(checkResponse))
      (response \ "error_code").as[Int] must equalTo(0)

    }

    "phone check fails when phone number and id are not matching" in new WithApplication {
      private val requestBody = Json.toJson(PhoneCheck(Some("2_93740362_456"), "13402815317"))

      val checkResponse = route(principalLoggedRequest(POST, "/api/v1/phone_check/13402815317").withJsonBody(requestBody)).get

      status(checkResponse) must equalTo(OK)
      contentType(checkResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(checkResponse))
      (response \ "error_code").as[Int] must equalTo(1)

    }

    "phone check fails when phone number exists and id is missing" in new WithApplication {
      private val requestBody = Json.toJson(PhoneCheck(None, "13402815317"))

      val checkResponse = route(principalLoggedRequest(POST, "/api/v1/phone_check/13402815317").withJsonBody(requestBody)).get

      status(checkResponse) must equalTo(OK)
      contentType(checkResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(checkResponse))
      (response \ "error_code").as[Int] must equalTo(1)

    }

  }
}
