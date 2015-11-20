package controllers

import models.V4.KulebaoAgent
import play.api.Play
import play.api.mvc._
import play.api.mvc.BodyParsers.parse
import play.api.data._
import play.api.data.Forms._

import models._
import views._
import play.Logger
import scala.Some
import scala.concurrent.Future

object Auth extends Controller {

  // -- Authentication

  val loginForm = Form(
    tuple(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
      "challenge" -> nonEmptyText,
      "answer" -> nonEmptyText
    ) verifying("无效的用户名或密码。", _ match {
      case (username, password, _, _) => Employee.authenticate(username, password).isDefined || KulebaoAgent.authenticate(username, password).isDefined
    }) verifying("验证码不正确。", _ match {
      case anything if play.api.Play.mode(Play.application(play.api.Play.current)) == play.api.Mode.Test => true
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
        case agent if KulebaoAgent.isAgent(agent, s"/main/$agent/school") =>
          Redirect(s"/agent#/main/${agent.toLong}/school")
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
          val login: LoginAccount = Employee.authenticate(user._1, user._2).getOrElse(KulebaoAgent.authenticate(user._1, user._2).get)
          Redirect(login.url()).withSession(login.session())
        }
      )
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Auth.login()).withNewSession.flashing(
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

  private def agent(request: RequestHeader) = {
    val id: Option[String] = request.session.get("id")
    id match {
      case Some(agent) if operator(request).isDefined =>
        request.session.get("username")
      case Some(agent) if KulebaoAgent.isAgent(agent, request.path) =>
        request.session.get("username")
      case _ => None
    }
  }

  private def principal(request: RequestHeader) = {
    val user = request.session.get("username")
    val id: Option[String] = request.session.get("id")
    id match {
      case Some(op) =>
        val Pattern = "^(?:/api/v\\d+)?/kindergarten/(\\d+).*".r
        val PrincipalImport = "/api/v1/batch_import/(\\d+).*".r
        request.path match {
          case path if Employee.canAccess(id) => user
          case Pattern(c) if Employee.isPrincipal(op, c.toLong) => user
          case PrincipalImport(c) if Employee.isPrincipal(op, c.toLong) => user
          case _ => None
        }
      case _ => None
    }

  }

  private def checkSchool(request: RequestHeader) = {
    val user = request.session.get("username")
    val id = request.session.get("id")
    val token = request.session.get("token")
    val school = "^(?:/api/v\\d+)?/kindergarten/(\\d+).*".r
    request.path match {
      case path if Employee.canAccess(id) => user
      case school(c) if Employee.canAccess(id, c.toLong) || Parent.canAccess(user, token, c.toLong) => user
      case _ => None
    }

  }


  /**
   * Redirect to login if the user in not authorized.
   */
  private def redirectToLogin(request: RequestHeader) = {
    request.headers.get("source").fold(Results.Redirect(routes.Auth.login()))({
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

  def AgentPage(f: => String => Request[AnyContent] => Result) = Security.Authenticated(agent, redirectToLogin) {
    user =>
      Action(request => f(user)(request))
  }

  def IsAgentLoggedIn(f: => String => Request[AnyContent] => Result) = Security.Authenticated(agent, forbidAccess) {
    user =>
      Action(request => f(user)(request))
  }

  def IsAgentLoggedIn(b: BodyParser[play.api.libs.json.JsValue] = parse.json)
                (f: => String => Request[play.api.libs.json.JsValue] => Result) = Security.Authenticated(agent, forbidAccess) {
    user =>
      Action(b)(request => f(user)(request))
  }

  def IsLoggedIn(f: => String => Request[AnyContent] => Result) = Security.Authenticated(checkSchool, forbidAccess) {
    user =>
      Action(request => f(user)(request))
  }

  def IsLoggedInAsync(f: => String => Request[AnyContent] => Future[SimpleResult]) = Security.Authenticated(checkSchool, forbidAccess) {
    user =>
      Action.async(request => f(user)(request))
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