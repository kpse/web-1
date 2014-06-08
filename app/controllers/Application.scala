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
  
  def continuousLogging = Action {
    val e = LogTracker.enumerator
    Ok.chunked(e &> EventSource()).as(EVENT_STREAM)
  }
}

object LogTracker {
  def enumerator: Enumerator[String] = {
    val file: File = new java.io.File("%s/logs/application.log".format(Play.application.path))
    lazy val fileContent: Enumerator[Array[Byte]] = Enumerator.fromStream(Tail.follow(file))
    fileContent through Enumeratee.map[Array[Byte]] { data =>
      Html(new String(data.map(_.toChar))).toString()
    }
  }
}
