package controllers

import play.api.mvc._
import play.api.mvc.BodyParsers.parse
import play.api.data._
import play.api.data.Forms._

import models._
import views._
import play.Logger
import scala.Some

object Auth extends Controller {

  // -- Authentication

  val loginForm = Form(
    tuple(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
      "challenge" -> nonEmptyText,
      "answer" -> nonEmptyText
    ) verifying("无效的用户名或密码。", _ match {
      case (username, password, _, _) => Employee.authenticate(username, password).isDefined
    }) verifying("验证码不正确。", _ match {
      case (_, _, challenge, answer) => ReCaptcha.simpleCheck(challenge, answer)
    })
  )

  /**
   * Login page.
   */
  def login = Action {
    implicit request =>
      Logger.info(request.session.data.toString())
      request.session.get("id").fold(Ok(html.login(loginForm)))({
        case op if Employee.isOperator(op) =>
          Redirect("/operation")
        case admin =>
          Redirect("/admin")
      })

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
            Redirect("/admin#/kindergarten/%d".format(login.school_id)).withSession("username" -> login.login_name, "phone" -> login.phone, "name" -> login.name, "id" -> login.id.getOrElse(""))
        }

      )
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Auth.login).withNewSession.flashing(
      "success" -> "你已经成功退出登陆。"
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

  private def principal(request: RequestHeader) = {
    val user = request.session.get("username")
    val id: Option[String] = request.session.get("id")
    id match {
      case Some(op) =>
        val Pattern = "/kindergarten/(\\d+).*".r
        request.path match {
          case path if Employee.canAccess(id) => user
          case Pattern(c) if Employee.isPrincipal(op, c.toLong) => user
          case _ => None
        }
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
  private def redirectToLogin(request: RequestHeader) = {
    request.headers.get("source").fold(Results.Redirect(routes.Auth.login))({
      case _ => forbidAccess(request)
    })

  }

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

  def IsPrincipal(f: => String => Request[AnyContent] => Result) = Security.Authenticated(principal, forbidAccess) {
    user =>
      Action(request => f(user)(request))
  }

  def IsPrincipal(b: BodyParser[play.api.libs.json.JsValue] = parse.json)
                (f: => String => Request[play.api.libs.json.JsValue] => Result) = Security.Authenticated(principal, forbidAccess) {
    user =>
      Action(b)(request => f(user)(request))
  }

}