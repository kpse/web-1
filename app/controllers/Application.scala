package controllers

import play.api.mvc._
import play.api.libs.iteratee.Enumerator
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import play.{Logger, Play}

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
      val file = new java.io.File("logs/application.log")
      val fileContent: Enumerator[Array[Byte]] = Enumerator.fromFile(file)

      SimpleResult(
        header = ResponseHeader(200, Map(CONTENT_LENGTH -> file.length.toString)),
        body = fileContent
      )
  }
}