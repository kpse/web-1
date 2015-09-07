package controllers.V4

import helper.TestSupport
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.{JsArray, JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class AgentControllerSpec extends Specification with TestSupport {
  def requestWithSession(method: String, url: String) = FakeRequest(method, url).withSession("id" -> "3_93740362_9971", "username" -> "admin")

  "AgentController" should {
    "report summary" in new WithApplication {

      val summaryResponse = route(requestWithSession(GET, "/api/v4/kindergarten/93740362/commercial_summary")).get

      status(summaryResponse) must equalTo(OK)
      contentType(summaryResponse) must beSome.which(_ == "application/json")

      val response: JsValue = Json.parse(contentAsString(summaryResponse))

      (response \ "agent_id").as[Long] must equalTo(1)
      (response \ "contractor" \ "current").as[Long] must equalTo(5)
      (response \ "contractor" \ "threshold").as[Long] must equalTo(5L)
      (response \ "activity" \ "current").as[Long] must equalTo(1L)
      (response \ "activity" \ "threshold").as[Long] must equalTo(5L)
      (response \ "school_id").as[Long] must equalTo(93740362L)
    }
  }

}
