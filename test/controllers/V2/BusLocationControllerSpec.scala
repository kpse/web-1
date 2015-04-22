package controllers.V2

import _root_.helper.TestSupport
import models.json_models.{CheckChildInfo, CheckInfo}
import models.{BusLocation, Parent, ParentPhoneCheck}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import models.json_models.CheckingMessage._

@RunWith(classOf[JUnitRunner])
class BusLocationControllerSpec extends Specification with TestSupport {
  implicit val writes = Json.writes[Parent]
  implicit val read = Json.reads[ParentPhoneCheck]
  implicit val writes2 = Json.writes[ParentPhoneCheck]

  def driverRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("id" -> "3_93740362_3344", "username" -> "e0002")
  }

  def parentRequest(method: String, url: String) = {
    FakeRequest(method, url).withSession("username" -> "13408654680", "token" -> "1386849160798")
  }

  "BusLocation" should {
    "check authentication first" in new WithApplication {

      val response1 = route(FakeRequest(GET, "/api/v2/kindergarten/93740362/bus_driver/3_93740362_11322/location")).get
      private val json1: JsValue = Json.toJson(BusLocation(93740362, "3_93740362_11322", 1, 2, 3, 4, Some("address"), None, None))
      val response2 = route(FakeRequest(POST, "/api/v2/kindergarten/93740362/bus_driver/3_93740362_11322/location").withBody(json1)).get
      val response3 = route(FakeRequest(GET, "/api/v2/kindergarten/93740362/last_bus_location/:childId")).get
      private val json2: JsValue = Json.toJson(CheckInfo(93740362, "0001234567", 1, 2, "", 0))
      val response4 = route(FakeRequest(POST, "/api/v2/kindergarten/93740362/bus_driver/3_93740362_11322/check_in").withBody(json2)).get
      val response5 = route(FakeRequest(POST, "/api/v2/kindergarten/93740362/bus_driver/3_93740362_11322/check_out").withBody(json2)).get
      private val json3: JsValue = Json.toJson(List[CheckInfo]())
      val response6 = route(FakeRequest(POST, "/api/v2/kindergarten/93740362/bus_driver/3_93740362_11322/batch_check_out").withBody(json3)).get
      private val json4: JsValue = Json.toJson(CheckChildInfo(93740362, "0001234567", 1, 2))
      val response7 = route(FakeRequest(POST, "/api/v2/kindergarten/93740362/bus_driver/3_93740362_11322/child_off_bus").withBody(json4)).get
      val response8 = route(FakeRequest(POST, "/api/v2/kindergarten/93740362/bus_driver/3_93740362_11322/child_on_bus").withBody(json4)).get
      status(response1) must equalTo(UNAUTHORIZED)
      status(response2) must equalTo(UNAUTHORIZED)
      status(response3) must equalTo(UNAUTHORIZED)
      status(response4) must equalTo(UNAUTHORIZED)
      status(response5) must equalTo(UNAUTHORIZED)
      status(response6) must equalTo(UNAUTHORIZED)
      status(response7) must equalTo(UNAUTHORIZED)
      status(response8) must equalTo(UNAUTHORIZED)

    }

    "be access across drivers and parents" in new WithApplication {
      val childId: String = "1_1391836223533"

      val checkResponse1 = route(parentRequest(GET, s"/api/v2/kindergarten/93740362/last_bus_location/$childId")).get

      status(checkResponse1) must equalTo(NOT_FOUND)
      (Json.parse(contentAsString(checkResponse1)) \ "error_code").as[Int] must equalTo(1)

      private val json2: JsValue = Json.toJson(CheckInfo(93740362, "0001234567", 1, 2, "", 0))
      val checkInResponse = route(driverRequest(POST, "/api/v2/kindergarten/93740362/bus_driver/3_93740362_11322/check_in").withBody(json2)).get

      status(checkInResponse) must equalTo(OK)
      (Json.parse(contentAsString(checkResponse1)) \ "error_code").as[Int] must equalTo(1)

      private val json1: JsValue = Json.toJson(BusLocation(93740362, "3_93740362_11322", 100.11, 2, 3, 4, Some("address"), None, None))
      val locationResponse = route(driverRequest(POST, "/api/v2/kindergarten/93740362/bus_driver/3_93740362_11322/location").withBody(json1)).get

      status(locationResponse) must equalTo(OK)
      (Json.parse(contentAsString(locationResponse)) \ "error_code").as[Int] must equalTo(0)

      val checkResponse2 = route(parentRequest(GET, s"/api/v2/kindergarten/93740362/last_bus_location/$childId")).get

      status(checkResponse2) must equalTo(OK)
      (Json.parse(contentAsString(checkResponse2)) \ "latitude").as[Double] must equalTo(100.11)

      private val json4: JsValue = Json.toJson(CheckChildInfo(93740362, childId, 1, 2))
      val offTheBus = route(driverRequest(POST, "/api/v2/kindergarten/93740362/bus_driver/3_93740362_11322/child_off_bus").withBody(json4)).get

      status(offTheBus) must equalTo(OK)
      (Json.parse(contentAsString(offTheBus)) \ "error_code").as[Int] must equalTo(0)

      private val json5: JsValue = Json.toJson(BusLocation(93740362, "3_93740362_11322", 1, 99.991, 3, 4, Some("address"), None, None))
      val locationResponse2 = route(driverRequest(POST, "/api/v2/kindergarten/93740362/bus_driver/3_93740362_11322/location").withBody(json5)).get

      status(locationResponse2) must equalTo(OK)

      val checkResponse3 = route(parentRequest(GET, s"/api/v2/kindergarten/93740362/last_bus_location/$childId")).get

      status(checkResponse3) must equalTo(NOT_FOUND)
      (Json.parse(contentAsString(checkResponse3)) \ "error_code").as[Int] must equalTo(2)


      private val json6: JsValue = Json.toJson(CheckChildInfo(93740362, childId, 1, 2))
      val backOnTheBus = route(driverRequest(POST, "/api/v2/kindergarten/93740362/bus_driver/3_93740362_11322/child_on_bus").withBody(json6)).get

      status(backOnTheBus) must equalTo(OK)
      (Json.parse(contentAsString(backOnTheBus)) \ "error_code").as[Int] must equalTo(0)


      val checkResponse4 = route(parentRequest(GET, s"/api/v2/kindergarten/93740362/last_bus_location/$childId")).get

      status(checkResponse4) must equalTo(OK)
      (Json.parse(contentAsString(checkResponse4)) \ "longitude").as[Double] must equalTo(99.991)


      private val json7: JsValue = Json.toJson(CheckInfo(93740362, "0001234567", 1, 2, "", 0))
      val arriveHome = route(driverRequest(POST, "/api/v2/kindergarten/93740362/bus_driver/3_93740362_11322/check_out").withBody(json7)).get

      status(backOnTheBus) must equalTo(OK)
      (Json.parse(contentAsString(backOnTheBus)) \ "error_code").as[Int] must equalTo(0)

      val checkResponse5 = route(parentRequest(GET, s"/api/v2/kindergarten/93740362/last_bus_location/$childId")).get

      status(checkResponse5) must equalTo(NOT_FOUND)
      (Json.parse(contentAsString(checkResponse5)) \ "error_code").as[Int] must equalTo(4)

    }

  }
}
