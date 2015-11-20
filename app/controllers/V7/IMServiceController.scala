package controllers.V7

import controllers.Secured
import controllers.helper.JsonLogger._
import models.V7.IMToken
import models.V7.IMToken.writeIMTokenRes
import models.ErrorResponse.writeErrorResponse
import models.{Employee, ErrorResponse, Parent}
import play.Logger
import play.api.libs.json.Json
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext.Implicits.global

object IMServiceController extends Controller with Secured {
  def token(kg: Long, id: Long) = IsLoggedInAsync { username =>
    request =>
      Logger.info(s"username = $username")
      val tokenRequestBody: Option[String] = createRequestByUser(username, kg, id)
      IMToken.retrieveIMToken(tokenRequestBody).map {
        case Some(imToken) =>
          Ok(loggedJson(imToken))
        case None =>
          InternalServerError(Json.toJson(ErrorResponse("获取IM提供商Token出错.(error in retrieving IM token)")))
      }
  }

  def createRequestByUser(username: String, kg: Long, id: Long): Option[String] = {
    parentRequestBody(username, kg, id) orElse employeeRequestBody(username, kg, id)
  }

  def parentRequestBody(username: String, kg: Long, id: Long): Option[String] = {
    val user: Option[Parent] = Parent.phoneSearch(username)
    Logger.info(s"user = $user")
    user.filter(_.school_id == kg).filter(_.id == Some(id)).map(_.imUserInfo)
  }

  def employeeRequestBody(loginName: String, kg: Long, id: Long): Option[String] = {
    val user: Option[Employee] = Employee.findByLoginName(loginName)
    Logger.info(s"user = $user")
    user.filter(_.school_id == kg).filter(_.uid == Some(id)).map(_.imUserInfo)
  }
}
