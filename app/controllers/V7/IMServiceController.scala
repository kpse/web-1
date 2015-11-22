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
      val classes = eligibleClasses(kg, username, imAccount)
      classes.find(_.class_id == Some(id)) match {
        case Some(clazz) =>
          clazz.imInfo match {
            case Some(info) =>
              Future.successful(Ok(loggedJson(info)))
            case _ =>
              Logger.info(s"imAccount = ${imAccount}")
              imAccount.fold(Future(Forbidden(Json.toJson(ErrorResponse("网络故障,不能与IM提供商建立连接.(error in creating connection with IM provider)"))))) {
                case account =>
                  IMToken.createClassGroup(kg, Some(account.imClassInfo(clazz))).map {
                    case Some(classGroup) =>
                      val updated: IMClassGroup = classGroup.copy(school_id = kg, class_id = id.toInt, group_id = s"${clazz.school_id}_${clazz.class_id.get}", group_name = clazz.name)
                      updated.save(kg)
                      Ok(loggedJson(updated))
                    case None =>
                      InternalServerError(Json.toJson(ErrorResponse("创建群组出错.(error in creating class IM group)")))
                  }
              }
          }
        case None =>
          Future.successful(Forbidden(Json.toJson(ErrorResponse("权限不足,不能获取指定班级的群组信息.(error in access given class)"))))
      }
  }

  def eligibleClasses(kg: Long, username: String, imAccount: Option[IMAccount]): List[SchoolClass] = {
    imAccount match {
      case Some(Employee(_, _, _, _, _, _, _, _, _, loginName, _, _, _, _, _, _)) =>
        UserAccess.filter(UserAccess.queryByUsername(loginName, kg))(School.allClasses(kg))
      case Some(Parent(_, _, _, phone, _, _, _, _, _, _, _, _, _, _)) =>
        val classes: List[Int] = Relationship.index(kg, Some(phone), None, None).map(_.child.get.class_id)
        School.allClasses(kg).filter(c => classes.contains(c.class_id.get))
      case _ => List()
    }
  }
}
