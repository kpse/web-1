package controllers.V5

import controllers.{RelationshipController, Secured}
import models.Relationship.writeRelationship
import models.V5.{InvitationPhoneKey, Invitation, InvitationCode}
import models.{ErrorResponse, Parent, Relationship}
import play.api.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller
import play.cache.Cache
import controllers.helper.JsonLogger.loggedJson

object InvitationController extends Controller with Secured {

  implicit def stringToInvitationPhoneKey(phone: String): InvitationPhoneKey = InvitationPhoneKey(phone)

  def create(schoolId: Long) = IsLoggedIn(parse.json) {
    u =>
      request =>
        Logger.info(request.body.toString())
        request.body.validate[Invitation].map {
          case (invitation) if invitation.code.isEmpty =>
            InternalServerError(loggedJson(ErrorResponse("验证码不正确。(wrong verification code)")))
          case (invitation) if invitation.code.get.phone != invitation.to.phone =>
            InternalServerError(loggedJson(ErrorResponse("验证码不正确。(wrong verification code)")))
          case (invitation) if !InvitationCode.isMatched(invitation.code.get) =>
            InternalServerError(loggedJson(ErrorResponse("验证码不正确。(wrong verification code)")))
          case (invitation) if invitation.from.phone != u =>
            Logger.info(s"u = $u")
            InternalServerError(loggedJson(ErrorResponse("邀请人信息不正确。(wrong host information)", 10)))
          case (invitation) if Parent.phoneSearch(invitation.to.phone).exists(_.status == Some(1)) =>
            InternalServerError(loggedJson(ErrorResponse("被邀请号码已存在。(invitee's phone number exists already)", 20)))
          case (invitation) =>
            Cache.remove(invitation.code.get.phone.iKey)
            val newCreation: List[Relationship] = invitation.copy(from = invitation.from.copy(school_id = schoolId)).settle
            RelationshipController.clearCurrentCache(schoolId)
            Logger.info("invitation creates : " + newCreation.toString)
            Ok(loggedJson(newCreation))
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }
}
