package controllers.V5

import controllers.Secured
import models.Relationship
import models.Relationship.{writeRelationship, writeChildInfo, writeParent}
import models.V5.Invitation
import play.api.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object InvitationController extends Controller with Secured {
  def create(schoolId: Long) = IsLoggedIn(parse.json) {
    u =>
      request =>
        Logger.info(request.body.toString())
        request.body.validate[Invitation].map {
          case (me) =>
            val newCreation: List[Relationship] = me.copy(from = me.from.copy(school_id = schoolId)).settle
            Logger.info("invitation creates : " + newCreation.toString)
            Ok(Json.toJson(newCreation))
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }
}
