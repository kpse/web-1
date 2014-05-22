package models

import _root_.helper.TestSupport
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.libs.ws.{Response, WS}
import play.api.Play
import play.api.libs.json.Json
import scala.io.Source
import scala.concurrent.{Future, Await, ExecutionContext}
import ExecutionContext.Implicits.global
import play.api.Play.current
import play.api.libs.ws.WS.WSRequestHolder

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

  implicit val read = Json.reads[ChildInfo]
  implicit val read1 = Json.reads[SchoolClass]
  implicit val read2 = Json.reads[Parent]
  implicit val read3 = Json.reads[Relationship]


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
      val relationshipCheckingUrl = "http://localhost:19001/kindergarten/%d/relationship".format(schoolId)
      val relationshipSingleCheckingUrl = "http://localhost:19001/kindergarten/%d/relationship/0000000011".format(schoolId)


      waitForWSCall(allRequests("/data/school.txt").filter(_.trim.nonEmpty).map(createOnServer(schoolCreatingUrl)))

      waitForWSCall(allRequests("/data/class.txt").filter(_.trim.nonEmpty).map(createOnServer(classCreatingUrl)), Some(30))

      waitForWSCall(allRequests("/data/student.txt").filter(_.trim.nonEmpty).map(createOnServer(childCreatingUrl)), Some(60))

      waitForWSCall(allRequests("/data/relative.txt").filter(_.trim.nonEmpty).map(createOnServer(parentCreatingUrl)), Some(60))

      waitForWSCall(allRequests("/data/relationship.txt").filter(_.trim.nonEmpty).foldLeft(List[Future[Response]]()) {
        (arr: List[Future[Response]], line: String) =>
          arr ::: List(createOnServer(relationshipCreatingUrl.format(schoolId, arr.size))(line))
      }.toIterator, Some(60))


      private val schoolResponse: Response = waitForSingleWSCall(wsCall(schoolCheckingUrl).get())

      schoolResponse.status must equalTo(200)
      (Json.parse(schoolResponse.body) \ "school_id").as[Long] must beEqualTo(schoolId)

      private val classResponse: Response = waitForSingleWSCall(wsCall(classCheckingUrl).get())

      classResponse.status must equalTo(200)
      Json.parse(classResponse.body).as[List[SchoolClass]].size must beEqualTo(27)
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


      def createOnServer(url: String): (String) => Future[Response] = {
        line =>
          println("%s <<<>>> %s".format(url, line))
          wsCall(url).withHeaders("Content-Type" -> "application/json").post(line.format(schoolId, schoolId))
      }

    }

  }

  def waitForWSCall(futures: Iterator[Future[Response]], timeout: Option[Long] = Some(1)) = {
    Await.result(Future.sequence(futures), scala.concurrent.duration.Duration.apply(timeout.getOrElse(1L), "second"))
  }

  def waitForSingleWSCall(future: Future[Response], timeout: Option[Long] = Some(1)) = {
    Await.result(future, scala.concurrent.duration.Duration.apply(timeout.getOrElse(1L), "second"))
  }

  def wsCall(url: String): WSRequestHolder = {
    WS.url(url).withHeaders("Cookie" -> "PLAY_SESSION=\"7c635912fbbe211b27e4fe5ede182e2ed01d97d5-username=operator&phone=13060003723&name=%E8%B6%85%E4%BA%BA&id=3_93740362_9972\"")
  }
}
