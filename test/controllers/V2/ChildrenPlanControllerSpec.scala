package controllers.V2

import _root_.helper.TestSupport
import models.V2.ChildrenPlan
import models.json_models.CheckingMessage._
import models.json_models.{CheckChildInfo, CheckInfo}
import models.{BusLocation, Parent, ParentPhoneCheck}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class ChildrenPlanControllerSpec extends Specification with TestSupport {
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

      val response1 = route(FakeRequest(GET, "/api/v2/kindergarten/93740362/bus_driver/3_93740362_11322/plans")).get
      private val json1: JsValue = Json.toJson(ChildrenPlan(None, "3_93740362_11322", "1_93740362_456", 93740362, None, None))
      val response2 = route(FakeRequest(POST, "/api/v2/kindergarten/93740362/child/1_93740362_456/plan").withBody(json1)).get
      val response3 = route(FakeRequest(GET, "/api/v2/kindergarten/93740362/child/1_93740362_456/plan")).get
      val response4 = route(FakeRequest(DELETE, "/api/v2/kindergarten/93740362/child/1_93740362_456/plan")).get
      private val json2: JsValue = Json.toJson(List(ChildrenPlan(None, "3_93740362_11322", "1_93740362_456", 93740362, None, None)))
      val response5 = route(FakeRequest(POST, "/api/v2/kindergarten/93740362/children_plans").withBody(json2)).get

      status(response1) must equalTo(UNAUTHORIZED)
      status(response2) must equalTo(UNAUTHORIZED)
      status(response3) must equalTo(UNAUTHORIZED)
      status(response4) must equalTo(UNAUTHORIZED)
      status(response5) must equalTo(UNAUTHORIZED)

    }

    "report planned bus location before on boarding" in new WithApplication {
      val childId: String = "1_93740362_778"
      val driver: String = "3_93740362_11322"

      val checkResponse1 = route(parentRequest(GET, s"/api/v2/kindergarten/93740362/last_bus_location/$childId")).get

      status(checkResponse1) must equalTo(NOT_FOUND)
      (Json.parse(contentAsString(checkResponse1)) \ "error_code").as[Int] must equalTo(1)

      private val plan: JsValue = Json.toJson(ChildrenPlan(None, driver, childId, 93740362, None, None))
      val createPlanResponse = route(driverRequest(POST, s"/api/v2/kindergarten/93740362/child/${childId}/plan").withBody(plan)).get

      status(createPlanResponse) must equalTo(OK)
      (Json.parse(contentAsString(createPlanResponse)) \ "id").as[Long] must be greaterThan(1)

      private val json1: JsValue = Json.toJson(BusLocation(93740362, driver, 100.11, 2, 3, 4, Some("address"), None, None))
      val locationResponse = route(driverRequest(POST, s"/api/v2/kindergarten/93740362/bus_driver/${driver}/location").withBody(json1)).get

      val checkAgain = route(parentRequest(GET, s"/api/v2/kindergarten/93740362/last_bus_location/$childId")).get

      status(checkAgain) must equalTo(OK)
      (Json.parse(contentAsString(checkAgain)) \ "latitude").as[Double] must equalTo(100.11)

    }

    "report real on board bus location despite of plan" in new WithApplication {
      val childId: String = "1_93740362_778"
      val driver: String = "3_93740362_11322"
      val driver2: String = "3_93740362_1022"

      val checkResponse1 = route(parentRequest(GET, s"/api/v2/kindergarten/93740362/last_bus_location/$childId")).get

      status(checkResponse1) must equalTo(NOT_FOUND)
      (Json.parse(contentAsString(checkResponse1)) \ "error_code").as[Int] must equalTo(1)

      private val plan: JsValue = Json.toJson(ChildrenPlan(None, driver, childId, 93740362, None, None))
      val createPlanResponse = route(driverRequest(POST, s"/api/v2/kindergarten/93740362/child/${childId}/plan").withBody(plan)).get

      status(createPlanResponse) must equalTo(OK)
      (Json.parse(contentAsString(createPlanResponse)) \ "id").as[Long] must be greaterThan(1)

      private val json1: JsValue = Json.toJson(BusLocation(93740362, driver, 100.11, 2, 3, 4, Some("address"), None, None))
      val locationResponse = route(driverRequest(POST, s"/api/v2/kindergarten/93740362/bus_driver/${driver}/location").withBody(json1)).get

      private val json2: JsValue = Json.toJson(BusLocation(93740362, driver2, 99.11, 2, 3, 4, Some("address"), None, None))
      val locationResponse2 = route(driverRequest(POST, s"/api/v2/kindergarten/93740362/bus_driver/${driver}/location").withBody(json2)).get

      private val onBoarding: JsValue = Json.toJson(CheckInfo(93740362, "0001234570", 1, 2, "", 0))
      val checkInResponse = route(driverRequest(POST, "/api/v2/kindergarten/93740362/bus_driver/${driver2}/check_in").withBody(onBoarding)).get

      val checkResponse2 = route(parentRequest(GET, s"/api/v2/kindergarten/93740362/last_bus_location/$childId")).get

      status(checkResponse2) must equalTo(OK)
      (Json.parse(contentAsString(checkResponse2)) \ "latitude").as[Double] must equalTo(99.11)

    }

  }
}
