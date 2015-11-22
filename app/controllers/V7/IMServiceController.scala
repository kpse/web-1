package controllers.V7

import controllers.Secured
import controllers.helper.JsonLogger._
import models.ErrorResponse.writeErrorResponse
import models.V7.IMToken.{writeIMToken, writeIMBasicRes}
import models.V7.{IMClassGroup, IMToken}
import models._
import play.Logger
import play.api.libs.json.Json
import play.api.mvc.Controller

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object IMServiceController extends Controller with Secured {
  def token(kg: Long, id: Long) = IsLoggedInAsync { username =>
    request =>
      Logger.info(s"username = $username")
      val imAccount: Option[IMAccount] = createRequestByUser(username, kg, id)
      Logger.info(s"imAccount = $imAccount")
      imAccount.fold(Future(InternalServerError(Json.toJson(ErrorResponse("网络故障,不能与IM提供商建立连接.(error in creating connection with IM provider)"))))) {
        case account =>
          IMToken.retrieveIMToken(kg, Some(account.imUserInfo)).map {
            case Some(imToken) =>
              Ok(loggedJson(imToken))
            case None =>
              InternalServerError(Json.toJson(ErrorResponse("获取IM提供商Token出错.(error in retrieving IM token)")))
          }
      }
  }

  def createRequestByUser(username: String, kg: Long, id: Long): Option[IMAccount] = {
    whatIfParents(username, kg, id) orElse whatIfEmployees(username, kg, id)
  }

  def whoIsRequesting(username: String, kg: Long): Option[IMAccount] = {
    Parent.phoneSearch(username).filter(_.school_id == kg) orElse
      Employee.findByLoginName(username).filter(_.school_id == kg)
  }

  def whatIfParents(username: String, kg: Long, id: Long): Option[IMAccount] = {
    Parent.phoneSearch(username).filter(_.school_id == kg).filter(_.id == Some(id))
  }

  def whatIfEmployees(loginName: String, kg: Long, id: Long): Option[IMAccount] = {
    Employee.findByLoginName(loginName).filter(_.school_id == kg).filter(_.uid == Some(id))
  }

  def classGroup(kg: Long, id: Long) = IsLoggedInAsync { username =>
    request =>
      Logger.info(s"username = $username")
      val imAccount: Option[IMAccount] = whoIsRequesting(username, kg)
      Logger.info(s"imAccount = $imAccount")
      IMToken.classGroup(kg, username, id, imAccount).map {
        case Some(imToken) =>
          Ok(loggedJson(imToken))
        case None =>
          InternalServerError(Json.toJson(ErrorResponse("创建班级聊天群出错.(error in creating class IM group)")))
      }


  }
}
