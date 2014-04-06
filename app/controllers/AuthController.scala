package controllers

import play.api.mvc._
import play.api.mvc.BodyParsers.parse
import play.api.data._
import play.api.data.Forms._

import models._
import views._
import play.Logger

object Auth extends Controller {

  // -- Authentication

  val loginForm = Form(
    tuple(
      "username" -> text,
      "password" -> text
    ) verifying("Invalid username or password", result => result match {
      case (username, password) => Employee.authenticate(username, password).isDefined
    })
  )

  /**
   * Login page.
   */
  def login = Action {
    implicit request =>
      Ok(html.login(loginForm))
  }

  /**
   * Handle login form submission.
   */
  def authenticate = Action {
    implicit request =>
      loginForm.bindFromRequest.fold(
        formWithErrors => BadRequest(html.login(formWithErrors)),
        user => {
          val login = Employee.authenticate(user._1, user._2).get
          Logger.info(login.toString)
          if (login.privilege_group.getOrElse("").equals("operator"))
            Redirect("/operation").withSession("username" -> login.login_name, "phone" -> login.phone, "name" -> login.name, "id" -> login.id.getOrElse(""))
          else
            Redirect("/admin#/kindergarten/%d".format(login.school_id)).withSession("username" -> login.name, "phone" -> login.phone, "name" -> login.name, "id" -> login.id.getOrElse(""))
        }

      )
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Auth.login).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }

}

/**
 * Provide security features
 */
trait Secured {

  /**
   * Retrieve the connected user email.
   */
  private def username(request: RequestHeader) = {
    request.session.get("username")
  }

  private def operator(request: RequestHeader) = {
    val id: Option[String] = request.session.get("id")
    id match {
      case Some(op) if Employee.isOperator(op) =>
        request.session.get("username")
      case _ => None
    }

  }

  private def checkSchool(request: RequestHeader) = {
    val user = request.session.get("username")
    val id = request.session.get("id")
    val token = request.session.get("token")
    val Pattern = "/kindergarten/(\\d+).*".r
    request.path match {
      case path if Employee.canAccess(id) => user
      case Pattern(c) if Employee.canAccess(id, c.toLong) => user
      case Pattern(c) if Parent.canAccess(user, token, c.toLong) => user
      case _ => None
    }

  }


  /**
   * Redirect to login if the user in not authorized.
   */
  private def redirectToLogin(request: RequestHeader) = Results.Redirect(routes.Auth.login)

  private def forbidAccess(request: RequestHeader) = Results.Unauthorized

  // --

  /**
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, redirectToLogin) {
    user =>
      Action(request => f(user)(request))
  }
  def IsAuthenticated(b: BodyParser[play.api.libs.json.JsValue] = parse.json)
                     (f: => String => Request[play.api.libs.json.JsValue] => Result) = Security.Authenticated(username, redirectToLogin) {
    user =>
      Action(b)(request => f(user)(request))
  }

  def OperatorPage(b: BodyParser[play.api.libs.json.JsValue] = parse.json)
                  (f: => String => Request[play.api.libs.json.JsValue] => Result) = Security.Authenticated(operator, redirectToLogin) {
    user =>
      Action(b)(request => f(user)(request))
  }

  def OperatorPage(f: => String => Request[AnyContent] => Result) = Security.Authenticated(operator, redirectToLogin) {
    user =>
      Action(request => f(user)(request))
  }

  def IsLoggedIn(f: => String => Request[AnyContent] => Result) = Security.Authenticated(checkSchool, forbidAccess) {
    user =>
      Action(request => f(user)(request))
  }

  def IsLoggedIn(b: BodyParser[play.api.libs.json.JsValue] = parse.json)
                (f: => String => Request[play.api.libs.json.JsValue] => Result) = Security.Authenticated(checkSchool, forbidAccess) {
    user =>
      Action(b)(request => f(user)(request))
  }

  def IsOperator(f: => String => Request[AnyContent] => Result) = Security.Authenticated(operator, forbidAccess) {
    user =>
      Action(request => f(user)(request))
  }

  def IsOperator(b: BodyParser[play.api.libs.json.JsValue] = parse.json)
                (f: => String => Request[play.api.libs.json.JsValue] => Result) = Security.Authenticated(operator, forbidAccess) {
    user =>
      Action(b)(request => f(user)(request))
  }

}