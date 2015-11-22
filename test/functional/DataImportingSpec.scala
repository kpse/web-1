package functional

import _root_.helper.TestSupport
import models.V7.IMToken
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.Logger

import play.api.test._
import play.api.Play
import play.api.libs.json.Json
import scala.collection.immutable.IndexedSeq
import scala.io.Source
import scala.concurrent.Future
import play.api.Play.current
import models._
import play.api.libs.ws.Response
import models.SchoolClass
import scala.Some
import models.ChildInfo
import functional.WSHelper._


/**
 * add your integration spec here.
 * An integration test will fire up a whole play application in a real (or headless) browser
 */
@RunWith(classOf[JUnitRunner])
class DataImportingSpec extends Specification with TestSupport {


  def allRequests(file: String) = {
    val is = Play.resourceAsStream(file).get
    Source.fromInputStream(is).getLines()
  }

  def allCardRequests = (0 to 1000) map {
    num =>
      """{"number":"%020d","origin":"%010d"}""".format(num, num)
  }

  implicit val read = Json.reads[ChildInfo]
  implicit val read1 = Json.reads[SchoolClass]
  implicit val read2 = Json.reads[Parent]
  implicit val read3 = Json.reads[Relationship]
  implicit val read4 = Json.reads[IMToken]
  implicit val read5 = Json.reads[Employee]


  "Application" should {

    "create objects on demand" in new WithServer {
      val schoolId = System.currentTimeMillis()

      val schoolCreatingUrl = "http://localhost:19001/kindergarten"
      val schoolCheckingUrl = "http://localhost:19001/kindergarten/%d".format(schoolId)
      val classCreatingUrl = "http://localhost:19001/kindergarten/%d/class".format(schoolId)
      val classCheckingUrl = "http://localhost:19001/kindergarten/%d/class".format(schoolId)

      val childCreatingUrl = "http://localhost:19001/kindergarten/%d/child".format(schoolId)
      val childCheckingUrl = "http://localhost:19001/kindergarten/%d/child".format(schoolId)

      val parentCreatingUrl = "http://localhost:19001/kindergarten/%d/parent".format(schoolId)
      val parentCheckingUrl = "http://localhost:19001/kindergarten/%d/parent".format(schoolId)
      val relationshipCreatingUrl = "http://localhost:19001/kindergarten/%d/relationship/%010d"

      val cardCreatingUrl = "http://localhost:19001/api/v3/kindergarten/%d/card".format(schoolId)

      val relationshipCheckingUrl = "http://localhost:19001/kindergarten/%d/relationship".format(schoolId)
      val relationshipSingleCheckingUrl = "http://localhost:19001/kindergarten/%d/relationship/0000000011".format(schoolId)

      val employeeCreatingUrl = "http://localhost:19001/kindergarten/%d/employee/%s".format(schoolId, schoolId)
      val employeeCheckingUrl = "http://localhost:19001/kindergarten/%d/employee".format(schoolId)

      waitForWSCall(allRequests("/data/school.txt").filter(_.trim.nonEmpty).map(createOnServer(schoolCreatingUrl)))

      waitForWSCall(allRequests("/data/class.txt").filter(_.trim.nonEmpty).map(createOnServer(classCreatingUrl)), Some(30))

      waitForWSCall(allRequests("/data/student.txt").filter(_.trim.nonEmpty).map(createOnServer(childCreatingUrl)), Some(60))

      waitForWSCall(allRequests("/data/relative.txt").filter(_.trim.nonEmpty).map(createOnServer(parentCreatingUrl)), Some(60))

      waitForWSCall(allCardRequests.toIterator.map(createOnServer(cardCreatingUrl)), Some(60))

      waitForWSCall(allRequests("/data/relationship.txt").filter(_.trim.nonEmpty).foldLeft(List[Future[Response]]()) {
        (arr: List[Future[Response]], line: String) =>
          arr ::: List(createOnServer(relationshipCreatingUrl.format(schoolId, arr.size))(line))
      }.toIterator, Some(60))

      waitForWSCall(allRequests("/data/employee.txt").filter(_.trim.nonEmpty).map(createOnServer(employeeCreatingUrl)))

      private val schoolResponse: Response = waitForSingleWSCall(wsCall(schoolCheckingUrl).get())

      schoolResponse.status must equalTo(200)
      (Json.parse(schoolResponse.body) \ "school_id").as[Long] must beEqualTo(schoolId)

      private val classResponse: Response = waitForSingleWSCall(wsCall(classCheckingUrl).get())

      classResponse.status must equalTo(200)
      Json.parse(classResponse.body).as[List[SchoolClass]].size must beEqualTo(28)
      (Json.parse(classResponse.body)(0) \ "school_id").as[Long] must beEqualTo(schoolId)

      private val childrenResponse: Response = waitForSingleWSCall(wsCall(childCheckingUrl).get())

      childrenResponse.status must equalTo(200)
      Json.parse(childrenResponse.body).as[List[ChildInfo]].size must beEqualTo(703)
      (Json.parse(childrenResponse.body)(0) \ "school_id").as[Long] must beEqualTo(schoolId)

      private val parentRes: Response = waitForSingleWSCall(wsCall(parentCheckingUrl).get())

      parentRes.status must equalTo(200)
      Json.parse(parentRes.body).as[List[Parent]].size must beEqualTo(773)
      (Json.parse(parentRes.body)(0) \ "school_id").as[Long] must beEqualTo(schoolId)

      private val relationshipRes: Response = waitForSingleWSCall(wsCall(relationshipCheckingUrl).get())

      relationshipRes.status must equalTo(200)
      Json.parse(relationshipRes.body).as[List[Relationship]].size must beEqualTo(784)

      private val relationshipSingleRes: Response = waitForSingleWSCall(wsCall(relationshipSingleCheckingUrl).get())

      relationshipSingleRes.status must equalTo(200)
      Json.parse(relationshipSingleRes.body).as[Relationship].card must beEqualTo("0000000011")

      private val employeeRes: Response = waitForSingleWSCall(wsCall(employeeCheckingUrl).get())

      employeeRes.status must equalTo(200)
      Json.parse(employeeRes.body).as[List[Employee]].size must beEqualTo(2)
      (Json.parse(employeeRes.body)(1) \ "name").as[String] must beEqualTo("王老师")

      val relationshipCheckingUrl2 = "http://localhost:19001/kindergarten/%d/relationship?parent=18647879092".format(schoolId)

      private val relationshipforOneParent: Response = waitForSingleWSCall(wsCall(relationshipCheckingUrl2).get())

      relationshipforOneParent.status must equalTo(200)
      Json.parse(relationshipforOneParent.body).as[List[Relationship]].size must beEqualTo(1)
      Json.parse(relationshipforOneParent.body)(0).as[Relationship].child.get.name must beEqualTo("李家昊")


      def createOnServer(url: String): (String) => Future[Response] = {
        line =>
          wsCall(url).withHeaders("Content-Type" -> "application/json").post(line.format(schoolId, schoolId))
      }

    } tag "massive_import"

  }
}
