package controllers

import play.api.mvc._

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
}