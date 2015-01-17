package controllers

import play.api.mvc.Controller
import play.api.libs.json.{JsError, Json}
import models._

object ChargeController extends Controller with Secured {

  implicit val read1 = Json.reads[ChargeInfo]
  implicit val write1 = Json.writes[ChargeInfo]
  implicit val write2 = Json.writes[ActiveCount]

  implicit val writeSuccessResponse = Json.writes[SuccessResponse]
  implicit val writeErrorResponse = Json.writes[ErrorResponse]

  def update(kg: Long) = IsOperator(parse.json) {
    u =>
      request =>
        request.body.validate[ChargeInfo].map {
          case (chargeInfo) =>
            Charge.update(kg, chargeInfo) match {
              case 0 => Ok(Json.toJson(new SuccessResponse))
              case _ => Ok(Json.toJson(ErrorResponse("update charge error.")))
            }

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

  def activeCount(kg: Long) = IsAuthenticated {
    u =>
      _ =>
        Ok(Json.toJson(Charge.countActivePhones(kg)))
  }
}
