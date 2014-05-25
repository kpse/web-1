package controllers

import play.api.mvc.Controller
import play.api.libs.json.{JsError, Json}
import models.{ChargeInfo, Charge}

object ChargeController extends Controller with Secured {

  implicit val read1 = Json.reads[ChargeInfo]
  implicit val write1 = Json.writes[ChargeInfo]

  def update(kg: Long) = IsOperator(parse.json) {
    u =>
      request =>
        request.body.validate[ChargeInfo].map {
          case (chargeInfo) =>
            Ok(Json.toJson(Charge.update(kg, chargeInfo)))
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }

  }

  def index(kg: Long) = IsAuthenticated {
    u =>
      _ =>
        Ok(Json.toJson(Charge.createIfNotExists(kg)))
  }

  def delete(kg: Long) = IsOperator {
    u =>
      _ =>
        Ok(Json.toJson(Charge.delete(kg)))
  }
}
