package functional

import scala.concurrent.{ExecutionContext, Await, Future}
import play.api.libs.ws.{WS, Response}
import play.api.libs.ws.WS.WSRequestHolder
import ExecutionContext.Implicits.global

object WSHelper {
  def waitForWSCall(futures: Iterator[Future[Response]], timeout: Option[Long] = Some(1)) = {
    Await.result(Future.sequence(futures), scala.concurrent.duration.Duration.apply(timeout.getOrElse(1L), "second"))
  }

  def waitForSingleWSCall(future: Future[Response], timeout: Option[Long] = Some(5)) = {
    Await.result(future, scala.concurrent.duration.Duration.apply(timeout.getOrElse(5L), "second"))
  }

  def wsCall(url: String): WSRequestHolder = {
    WS.url(url).withHeaders("Cookie" -> "PLAY_SESSION=\"7c635912fbbe211b27e4fe5ede182e2ed01d97d5-username=operator&phone=13060003723&name=%E8%B6%85%E4%BA%BA&id=3_93740362_9972\"")
  }
}
