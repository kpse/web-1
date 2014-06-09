package controllers

import play.api.mvc._
import play.api.libs.iteratee._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import play.{Logger, Play}
import java.io.{InputStream, File}
import play.api.templates.Html
import models.helper.Tail
import play.api.libs.EventSource
import play.api.mvc.ResponseHeader
import play.api.mvc.SimpleResult
import play.cache.Cache
import java.util.concurrent.Callable

object Application extends Controller with Secured {

  def index = Action {
    Ok(views.html.index())
  }

  def admin = IsAuthenticated {
    username =>
      _ =>
        Ok(views.html.admin())
  }

  def operation = OperatorPage {
    username =>
      _ =>
        Ok(views.html.operation())
  }

  def forgotten = Action {
    Ok(views.html.forgotten())
  }

  def logging = OperatorPage {
    u => _ =>
      val file: File = new java.io.File("%s/logs/application.log".format(Play.application.path))
      serveFile(file, ResponseHeader(200, Map(CONTENT_LENGTH -> file.length.toString)))
  }

  def downloadLog = OperatorPage {
    u => _ =>
      val file: File = new java.io.File("%s/logs/application.log".format(Play.application.path))
      serveFile(file, ResponseHeader(200, Map(CONTENT_DISPOSITION -> "attachment; filename=\"application.txt\"", CONTENT_TYPE -> "application/force-download")))
  }

  def serveFile(file: java.io.File, header: ResponseHeader): SimpleResult = {
    val fileContent: Enumerator[Array[Byte]] = Enumerator.fromFile(file)
    SimpleResult(
      header = header,
      body = fileContent
    )
  }

  implicit def functionToCallable[R](f: () => R): Callable[R] = new Callable[R] {
    def call: R = f()
  }

  def continuousLogging = OperatorPage {
    u => _ =>
      val e = Cache.getOrElse("logging", () => LogTracker.enumerator, 3600)
      Ok.chunked(e &> EventSource()).as(EVENT_STREAM)
  }

  def pureIntegerArray = OperatorPage {
    u => _ =>
      Ok.chunked(LogTracker.e2 &> EventSource()).as(EVENT_STREAM)
  }
}

object LogTracker {
  def enumerator: Enumerator[String] = {
    val file: File = new java.io.File("%s/logs/application.log".format(Play.application.path))
    lazy val fileContent: Enumerator[Array[Byte]] = Enumerator.fromStream(Tail.follow(file))
    val e = fileContent through Enumeratee.map[Array[Byte]] { data =>
      new String(data.map(_.toChar))
    }
    Cache.set("logging", e)
    e
  }

  def e2 = {
    val s: Stream[String] = "A" #:: "B" #:: "C" #:: "D" #:: Stream.empty[String]
    val s2 = s #::: s
    Enumerator.unfold(s2) { s => Some(s.tail, s.head)}
  }
}