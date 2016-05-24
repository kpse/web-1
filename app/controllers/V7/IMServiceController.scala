package controllers.V7

import controllers.Secured
import controllers.helper.JsonLogger._
import models.ErrorResponse.writeErrorResponse
import models.V7.IMToken.{writeIMBasicRes, writeIMToken}
import models.V7.{IMBanUser, IMToken}
import models.V7.IMToken.readsIMBanUser
import models.V7.IMToken.writeIMBanUserFromServer
import models.V8.IMHidingChatMessage.readIMHidingChatMessage
import models.V8.IMHidingChatMessage.readIMHidingPrivateChatMessage
import models.V8.{IMHidingChatMessage, IMHidingPrivateChatMessage}
import models._
import play.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object IMServiceController extends Controller with Secured {
  def token(kg: Long, id: Long) = IsLoggedInAsync { username =>
    request =>
      Logger.info(s"username = $username")
      val imAccount: Option[IMAccount] = createRequestByUser(username, kg, id)
      Logger.info(s"imAccount = $imAccount")
      imAccount.fold(Future(InternalServerError(Json.toJson(ErrorResponse("提供的信息不准确, 您无权查看该用户的聊天token.(error in querying user information)"))))) {
        case account =>
          IMToken.retrieveIMToken(kg, account).map {
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

  def classGroup(kg: Long, classId: Long) = IsLoggedInAsync { username =>
    request =>
      Logger.info(s"username = $username")
      val imAccount: Option[IMAccount] = whoIsRequesting(username, kg)
      Logger.info(s"imAccount = $imAccount")
      IMToken.classGroup(kg, username, classId, imAccount).map {
        case Some(classGroup) =>
          imAccount.foreach {
            case Parent(_, _, _, phone, _, _, _, _, _, _, _, _, _, _) =>
              val relationships: List[Relationship] = Relationship.index(kg, Some(phone), None, Some(classId))
              classGroup.welcomeToGroup(relationships)
            case _ => Logger.info("教师不发欢迎信息.(no welcome message for employees)")
          }
          Ok(loggedJson(classGroup))
        case None =>
          InternalServerError(Json.toJson(ErrorResponse("创建班级聊天群出错.(error in creating class IM group)")))
      }
  }

  def joinGroup(kg: Long, classId: Long) = IsLoggedInAsync(parse.json) { username =>
    request =>
      Logger.info(s"joinGroup username = $username")
      val imAccount: Option[IMAccount] = whoIsRequesting(username, kg)
      Logger.info(s"imAccount = ${imAccount}")
      IMToken.joinGroup(kg, username, classId, imAccount).map {
        case Some(classGroup) =>
          imAccount.foreach {
            case Parent(_, _, _, phone, _, _, _, _, _, _, _, _, _, _) =>
              val relationships: List[Relationship] = Relationship.index(kg, Some(phone), None, Some(classId))
              classGroup.welcomeToGroup(relationships)
            case _ => Logger.info("教师不发欢迎信息.(no welcome message for employees)")
          }
          Ok(loggedJson(classGroup))
        case None =>
          InternalServerError(Json.toJson(ErrorResponse("加入班级聊天群出错.(error in joining class IM group)", 11)))
      }
  }

  def leaveGroup(kg: Long, classId: Long, userId: String) = IsLoggedInAsync { username =>
    request =>
      Logger.info(s"leaveGroup username = $username")
      val imAccount: Option[IMAccount] = whoIsRequesting(username, kg)
      Logger.info(s"imAccount = $imAccount")
      imAccount match {
        case Some(account) if "me" == userId =>
          IMToken.leaveGroup(kg, username, classId, imAccount).map {
            case Some(classGroup) =>
              Ok(loggedJson(classGroup))
            case None =>
              InternalServerError(Json.toJson(ErrorResponse("离开班级聊天群出错.(error in leaving class IM group)", 21)))
          }
        case _ =>
          Future.successful(InternalServerError(Json.toJson(ErrorResponse(s"没有这个用户${userId}.(no such IM user)", 22))))
      }

  }

  //v7 interface..
  def banUser(kg: Long, class_id: Int) = IsLoggedInAsync(parse.json) { username =>
    request =>
      Logger.info(s"banUser username = $username")
      val imAccount: Option[IMAccount] = whoIsRequesting(username, kg)
      Logger.info(s"banUser imAccount = $imAccount")

      request.body.validate[List[IMBanUser]].map {
        case all =>
          IMToken.banUser(kg, class_id, imAccount, all).map {
            case res if res.size == all.size =>
              Ok(Json.toJson(new SuccessResponse))
            case res =>
              InternalServerError(Json.toJson(ErrorResponse(s"禁言用户出错.(error in banning users in class, $res)")))
          }
      }.recoverTotal {
        e => Future.successful(BadRequest("Detected error:" + JsError.toFlatJson(e)))
      }


  }

  def allowUser(kg: Long, class_id: Int, id: String) = IsLoggedInAsync { username =>
    request =>
      Logger.info(s"allowUser username = $username")
      val imAccount: Option[IMAccount] = whoIsRequesting(username, kg)
      Logger.info(s"allowUser imAccount = $imAccount")
      IMToken.allowUser(kg, class_id, imAccount, IMBanUser(id, None)).map {
        case Some(res) =>
          Ok(Json.toJson(new SuccessResponse))
        case None =>
          InternalServerError(Json.toJson(ErrorResponse(s"取消禁言用户出错.(error in un-banning user ${id} in class)")))
      }
  }

  def bannedUserList(kg: Long, class_id: Int) = IsLoggedInAsync { username =>
    request =>
      Logger.info(s"bannedUserList username = $username")
      val imAccount: Option[IMAccount] = whoIsRequesting(username, kg)
      Logger.info(s"imAccount = $imAccount")
      IMToken.bannedUserList(kg, class_id, imAccount).map {
        case users =>
          Ok(loggedJson(users))
      }
  }

  //v8 interfaces ...
  def internalBanUser(kg: Long, classId: Int) = IsLoggedIn(parse.json) { username =>
    request =>
      Logger.info(s"internalBanUser username = $username")
      val imAccount: Option[IMAccount] = whoIsRequesting(username, kg)
      Logger.info(s"imAccount = $imAccount")

      request.body.validate[List[IMBanUser]].map {
        case all =>
          all map {
            user =>
              user.ban(kg, classId)
              user.bannedNotification(kg, classId)
          }
          Ok(Json.toJson(new SuccessResponse))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }


  }

  def internalAllowUser(kg: Long, classId: Int, id: String) = IsLoggedIn { username =>
    request =>
      Logger.info(s"internalAllowUser username = $username")
      val imAccount: Option[IMAccount] = whoIsRequesting(username, kg)
      Logger.info(s"imAccount = $imAccount")
      val user: IMBanUser = IMBanUser(id, None)
      user.approve(kg, classId)
      user.approvalNotification(kg, classId)
      Ok(Json.toJson(new SuccessResponse))
  }

  def internalBannedUserList(kg: Long, class_id: Int) = IsLoggedIn { username =>
    request =>
      Logger.info(s"internalBannedUserList username = $username")
      val imAccount: Option[IMAccount] = whoIsRequesting(username, kg)
      Logger.info(s"imAccount = $imAccount")
      Ok(loggedJson(IMBanUser.bannedUserList(kg, class_id, imAccount)))

  }

  def hideMessage(kg: Long, classId: Int) = IsLoggedIn(parse.json) { username =>
    request =>
      Logger.info(s"internalBanUser username = $username")
      val imAccount: Option[IMAccount] = whoIsRequesting(username, kg)
      Logger.info(s"imAccount = $imAccount")

      request.body.validate[List[IMHidingChatMessage]].map {
        case all =>
          all foreach (_.hideNotification())
          Ok(Json.toJson(new SuccessResponse("尝试隐藏群组聊天信息。(try to hide a group chat message)")))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def hidePrivateMessage(kg: Long) = IsLoggedIn(parse.json) { username =>
    request =>
      Logger.info(s"internalBanUser username = $username")
      val imAccount: Option[IMAccount] = whoIsRequesting(username, kg)
      Logger.info(s"imAccount = $imAccount")

      request.body.validate[List[IMHidingPrivateChatMessage]].map {
        case all =>
          all foreach (_.hideNotification())
          Ok(Json.toJson(new SuccessResponse("尝试隐藏单聊信息。(try to hide a private chat message)")))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }
}
