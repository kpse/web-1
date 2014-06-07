package controllers

import play.api.mvc._
import play.api.libs.iteratee.{Iteratee, Enumeratee, Enumerator}
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import play.Play
import java.io.File
import play.api.templates.Html

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

  val toCometMessage = Enumeratee.map[Array[Byte]] { data =>
    Html( new String(data.map(_.toChar)))
  }

  def hearBeat = Action {
    val file: File = new java.io.File("%s/logs/application.log".format(Play.application.path))
    val fileContent: Enumerator[Array[Byte]] = Enumerator.fromFile(file)
    Ok.chunked(fileContent >>> Enumerator.eof &> toCometMessage)
  }

}