package controllers

import _root_.helper.TestSupport
import models.{ScheduleDetail, DaySchedule, Schedule, WeekSchedule}
import models.Schedule._
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class ScheduleControllerSpec extends Specification with TestSupport {
  def requestWithSession(method: String, url: String) = FakeRequest(method, url).withSession("id" -> "3_93740362_9971", "username" -> "admin")

  def requestByOperator(method: String, url: String) = FakeRequest(method, url).withSession("id" -> "3_93740362_9972", "username" -> "operator")

  "Schedule" should {
    "check authentication first" in new WithApplication {

      val unauthRequest = route(FakeRequest(GET, "/kindergarten/93740362/class/777666/schedule/1")).get

      status(unauthRequest) must equalTo(UNAUTHORIZED)

    }
    "be updated by increasing schedule_id like a new creation" in new WithApplication {

      val originalDetail = route(requestWithSession(GET, "/kindergarten/93740362/class/777666/schedule/121")).get

      status(originalDetail) must equalTo(OK)
      contentType(originalDetail) must beSome.which(_ == "application/json")

      Logger.info(contentAsString(originalDetail))
      val response: JsValue = Json.parse(contentAsString(originalDetail))
      (response \ "schedule_id").as[Long] must equalTo(121)

      val request = Json.toJson(ScheduleDetail(0, 93740362, 777666, 121, 0, WeekSchedule(Some(DaySchedule(Some("some schedule"), None)), Some(DaySchedule(None, None)),
        Some(DaySchedule(None, None)), Some(DaySchedule(None, None)), Some(DaySchedule(None, None)))))

      val updated = route(requestWithSession(POST, "/kindergarten/93740362/class/777666/schedule/121").withBody(request)).get

      status(updated) must equalTo(OK)
      contentType(updated) must beSome.which(_ == "application/json")
      Logger.info(contentAsString(updated))
      val updatedResponse: JsValue = Json.parse(contentAsString(updated))
      (updatedResponse \ "schedule_id").as[Long] must equalTo(122)
      (updatedResponse \ "week" \ "mon" \ "am").as[String] must equalTo("some schedule")


    }


  }
}
