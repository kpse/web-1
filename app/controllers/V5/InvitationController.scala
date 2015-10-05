package controllers.V5

import controllers.Secured
import models.{Verification, Parent, ErrorResponse, Relationship}
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
          case (me) if me.code.phone != me.to.phone || !Verification.isMatched(me.code) =>
            InternalServerError(Json.toJson(ErrorResponse("验证码不正确。(wrong verification code)")))
          case (me) if me.from.phone != u =>
            InternalServerError(Json.toJson(ErrorResponse("邀请人信息不正确。(wrong host information)", 10)))
          case (me) if Parent.phoneSearch(me.to.phone).exists(_.status == Some(1)) =>
            InternalServerError(Json.toJson(ErrorResponse("被邀请号码已存在。(invitee's phone number exists already)", 20)))
          case (me) =>
            val newCreation: List[Relationship] = me.copy(from = me.from.copy(school_id = schoolId)).settle
            Logger.info("invitation creates : " + newCreation.toString)
            Ok(Json.toJson(newCreation))
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }
}
