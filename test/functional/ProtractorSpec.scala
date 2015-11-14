package functional

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.time.NoTimeConversions
import play.api.libs.ws.WS
import play.api.test.TestServer
import play.api.test.Helpers.running
import scala.concurrent.Await
import scala.io.Source.fromInputStream
import scala.sys.process.{Process, ProcessIO}
import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class ProtractorSpec extends Specification with NoTimeConversions {
  "my app" should {

    "pass the protractor tests" in {
      running(TestServer(9100)) {

        Await.result(WS.url("http://localhost:9100").get, 2 second).status === 200
        startProtractor(getProcessIO) === 0
      }
    } tag "browser"
  }

  private def startProtractor(processIO: ProcessIO): Int = {
    Process("protractor", Seq( """test/protractor.conf.js"""))
      .run(processIO)
      .exitValue()
  }

  private def getProcessIO: ProcessIO = {
    new ProcessIO(_ => (),
      stdout => fromInputStream(stdout).getLines().foreach(println),
      _ => ())
  }
}
