package controllers.V2

import _root_.helper.TestSupport
import models.V2.SchoolBus
import models.json_models.CheckingMessage._
import models.json_models.{CheckChildInfo, CheckInfo}
import models.{Employee, BusLocation, Parent, ParentPhoneCheck}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.Logger
import play.api.libs.json.{JsArray, JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class SchoolBusControllerSpec extends Specification with TestSupport {
  implicit val writes = Json.writes[Parent]
  implicit val read = Json.reads[ParentPhoneCheck]
  implicit val writes2 = Json.writes[ParentPhoneCheck]

  def driverRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("id" -> "3_93740362_3344", "username" -> "e0002")
  }

  def parentRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "13408654680", "token" -> "1386849160798")
  }

  def principalRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "login_name", "id" -> "3_93740362_1122")
  }

  "SchoolBus" should {
    "check authentication first" in new WithApplication {

      val response1 = route(FakeRequest(GET, "/api/v2/kindergarten/93740362/bus")).get
      val response2 = route(FakeRequest(GET, "/api/v2/kindergarten/93740362/bus/1")).get
      val response3 = route(FakeRequest(DELETE, "/api/v2/kindergarten/93740362/bus/1")).get

      private val json1: JsValue = Json.toJson(SchoolBus(Some(1), "bus1", Some(someDriver), 93740362, "A-B-C", "A-B-C", "", "", "", "", None, None))
      val response4 = route(FakeRequest(POST, "/api/v2/kindergarten/93740362/bus").withBody(json1)).get

      status(response1) must equalTo(UNAUTHORIZED)
      status(response2) must equalTo(UNAUTHORIZED)
      status(response3) must equalTo(UNAUTHORIZED)
      status(response4) must equalTo(UNAUTHORIZED)
    }

    "should be created by teacher" in new WithApplication {

      private val json1: JsValue = Json.toJson(SchoolBus(Some(1), "bus1", Some(someDriver), 93740362, "A-B-C", "A-B-C", "", "", "", "", None, None))
      val response1 = route(principalRequest(POST, "/api/v2/kindergarten/93740362/bus").withBody(json1)).get
      status(response1) must equalTo(OK)
      private val res: JsValue = Json.parse(contentAsString(response1))
      (res \ "id").as[Long] must greaterThanOrEqualTo(1L)
      (res \ "name").as[String] must beEqualTo("bus1")
      (res \ "driver" \ "id").as[String] must beEqualTo("3_93740362_3344")
    }

    "should be deleted by teacher" in new WithApplication {

      val all = route(principalRequest(GET, "/api/v2/kindergarten/93740362/bus")).get
      status(all) must equalTo(OK)

      private val beforeDeleting: JsArray = Json.parse(contentAsString(all)).as[JsArray]

      val response1 = route(principalRequest(DELETE, "/api/v2/kindergarten/93740362/bus/1")).get
      status(response1) must equalTo(OK)

      val allAfter = route(principalRequest(GET, "/api/v2/kindergarten/93740362/bus")).get
      status(allAfter) must equalTo(OK)

      private val afterDeleting: JsArray = Json.parse(contentAsString(allAfter)).as[JsArray]
      afterDeleting.value.length must beEqualTo(beforeDeleting.value.length - 1)

    }

  }

  def someDriver: Employee = {
    Employee(Some("3_93740362_3344"), "driver1", "13708089040", 1, "", "", None, "", 93740362, "login1", None, None)
  }
}
