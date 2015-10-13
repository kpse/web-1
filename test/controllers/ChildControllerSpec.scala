package controllers

import _root_.helper.TestSupport
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import play.api.test.{WithServer, FakeRequest}
import play.api.libs.json.{JsArray, JsValue, Json}
import play.api.test.Helpers._
import play.Logger
import models.ChildInfo

@RunWith(classOf[JUnitRunner])
class ChildControllerSpec extends Specification with TestSupport {
  implicit val writes = Json.writes[ChildInfo]

  def loggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "13408654680", "token" -> "1386849160798")
  }

  def principalLoggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("id" -> "3_93740362_1122", "username" -> "e0001")
  }

  def twoClassesManagerLoggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("id" -> "3_93740362_3344", "username" -> "e0002")
  }

  def noAccessLoggedRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("id" -> "3_93740362_9977", "username" -> "e0003")
  }

  "Child" should {
    "check authentication first" in new WithServer {

      val childResponse = route(FakeRequest(GET, "/kindergarten/93740362/child/1_1394545098158")).get

      status(childResponse) must equalTo(UNAUTHORIZED)

    }

    "be updated with icon_url" in new WithServer {
      private val requestHeader = Json.toJson(ChildInfo(Some("1_1394545098158"), "", "", "1999-01-02", 0, Some("url"), 777888, None, None, Some(93740362)))

      val updateResponse = route(loggedRequest(POST, "/kindergarten/93740362/child/1_1394545098158").withJsonBody(requestHeader)).get

      status(updateResponse) must equalTo(OK)
      contentType(updateResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(updateResponse))
      (response \ "nick").as[String] mustNotEqual empty
      (response \ "portrait").as[String] must equalTo("url")
      (response \ "birthday").as[String] must equalTo("1999-01-02")
    }

    "be updated with nick" in new WithServer {
      private val requestHeader = Json.toJson(ChildInfo(Some("1_1394545098158"), "", "new_nick_name", "1999-01-02", 0, Some("portrait"), 777888, None, None, Some(93740362)))

      val updateResponse = route(loggedRequest(POST, "/kindergarten/93740362/child/1_1394545098158").withJsonBody(requestHeader)).get

      status(updateResponse) must equalTo(OK)
      contentType(updateResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(updateResponse))
      (response \ "nick").as[String] must equalTo("new_nick_name")
      (response \ "portrait").as[String] mustNotEqual empty
      (response \ "birthday").as[String] must equalTo("1999-01-02")
    }

    "be updated with birthday" in new WithServer {
      private val requestHeader = Json.toJson(ChildInfo(Some("1_1394545098158"), "", "new_nick_name", "1999-01-02", 0, Some("portrait"), 777888, None, None, Some(93740362)))

      val updateResponse = route(loggedRequest(POST, "/kindergarten/93740362/child/1_1394545098158").withJsonBody(requestHeader)).get

      status(updateResponse) must equalTo(OK)
      contentType(updateResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(updateResponse))
      Logger.info(response.toString())
      (response \ "nick").as[String] mustNotEqual empty
      (response \ "portrait").as[String] mustNotEqual empty
      (response \ "birthday").as[String] must equalTo("1999-01-02")
    }

    "show all children to principal" in new WithServer {

      val response = route(principalLoggedRequest(GET, "/kindergarten/93740362/child")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must greaterThanOrEqualTo(6)
          (arr(0) \ "child_id").as[String] must equalTo("1_1391836223533")
        case _ => failure
      }
    }

    "show partial children to manager" in new WithServer {

      val response = route(twoClassesManagerLoggedRequest(GET, "/kindergarten/93740362/child")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must lessThan(7)
          (arr.head \ "child_id").as[String] must not beEmpty
        case _ => failure
      }
    }

    "show no children to no access teacher" in new WithServer {

      val response = route(noAccessLoggedRequest(GET, "/kindergarten/93740362/child")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsArray = Json.parse(contentAsString(response)).as[JsArray]
      jsonResponse.value.length must beEqualTo(0)
    }

    "show no children to no access teacher even specific class id" in new WithServer {

      val response = route(noAccessLoggedRequest(GET, "/kindergarten/93740362/child?class_id=777666")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must beEqualTo(0)
        case _ => failure
      }
    }

    "show no children to manager when specific non-managed class" in new WithServer {

      val response = route(twoClassesManagerLoggedRequest(GET, "/kindergarten/93740362/child?class_id=777999")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must beEqualTo(0)
        case _ => failure
      }
    }

    "show all children to manager in specific class" in new WithServer {

      val response = route(twoClassesManagerLoggedRequest(GET, "/kindergarten/93740362/child?class_id=777666,777888")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must beEqualTo(4)
        case _ => failure
      }
    }

    "show all children to manager in both managed and nonManaged class" in new WithServer {

      val response = route(twoClassesManagerLoggedRequest(GET, "/kindergarten/93740362/child?class_id=777666,777999")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must beEqualTo(2)
        case _ => failure
      }
    }

    "show all children to principal in specific class" in new WithServer {

      val response = route(principalLoggedRequest(GET, "/kindergarten/93740362/child?class_id=777666")).get

      status(response) must equalTo(OK)

      val jsonResponse: JsValue = Json.parse(contentAsString(response))
      jsonResponse match {
        case JsArray(arr) =>
          arr.length must equalTo(2)
        case _ => failure
      }
    }

  }
}
