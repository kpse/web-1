package controllers

import java.io.{File, InputStream}
import java.util.concurrent.Callable

import models.Employee.writeCrossAppToken
import models.helper.Tail
import models.{Employee, ErrorResponse}
import org.joda.time.DateTime
import play.Play
import play.api.libs.EventSource
import play.api.libs.iteratee._
import play.api.libs.json.Json
import play.api.mvc.{ResponseHeader, SimpleResult, _}
import play.cache.Cache

import scala.concurrent.ExecutionContext.Implicits.global

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
      Cache.get("logging") match {
        case last: InputStream => last.close()
        case null => false
      }
      Ok.chunked(LogTracker.enumerator &> EventSource()).as(EVENT_STREAM)

  }

  def agent() = AgentPage {
    username =>
      _ =>
        Ok(views.html.agent())
  }

  def appOpening(user: String, timestamp: Long, goto: String, token: String) = Action {
    implicit request =>
      Employee.findByLoginName(user) match {
        case Some(employee) if new DateTime(timestamp).plusMinutes(3).isAfter(DateTime.now)
          && employee.crossAppToken(timestamp, goto).exists(_.token == token) =>
          Ok(views.html.admin(goto)).withSession(employee.session())
        case _ =>
          Ok(views.html.index()).withNewSession
      }
  }

  def testTokenGenerate(kg: Long, user: String, goto: String, timestamp: Option[Long]) = IsAuthenticated {
    u => request =>
      Employee.findByLoginName(user) match {
        case Some(employee) =>
          Ok(Json.toJson(employee.crossAppToken(timestamp.getOrElse(System.currentTimeMillis), goto)))
        case _ =>
          InternalServerError(Json.toJson(ErrorResponse("用户不存在.(User is not existing)")))
      }
  }
}

object LogTracker {
  def enumerator: Enumerator[String] = {
    val follow: InputStream = createStream
    Cache.set("logging", follow)
    enumeratorFromStream(follow)
  }

  def createStream: InputStream = {
    val file: File = new java.io.File("%s/logs/application.log".format(Play.application.path))
    Tail.follow(file)
  }

  def enumeratorFromStream(follow: InputStream): Enumerator[String] = {
    lazy val fileContent: Enumerator[Array[Byte]] = Enumerator.fromStream(follow)
    fileContent through Enumeratee.map[Array[Byte]] { data =>
      new String(data.map(_.toChar))
    }
  }

}