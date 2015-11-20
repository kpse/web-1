package controllers.V7

import controllers.Secured
import controllers.helper.JsonLogger._
import models.V7.IMToken
import models.V7.IMToken.writeIMTokenRes
import models.ErrorResponse.writeErrorResponse
import models.{ErrorResponse, Parent}
import play.api.libs.json.Json
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext.Implicits.global

object IMServiceController extends Controller with Secured {
  def token(kg: Long, id: Long) = IsLoggedInAsync { username =>
    request =>
      val user: Option[Parent] = Parent.phoneSearch(username)
      IMToken.retrieveIMToken(user.map(u => s"userId=${username}&name=${u.name}&portraitUri=${u.portrait.getOrElse("")}")).map {
        case Some(imToken) =>
          Ok(loggedJson(imToken))
        case None =>
          InternalServerError(Json.toJson(ErrorResponse("获取IM提供商Token出错.(error in retrieving IM token)")))
      }
  }

}
