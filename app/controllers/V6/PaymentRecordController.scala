package controllers.V6

import controllers.Secured
import models.ErrorResponse
import models.V6.PaymentInfo
import models.V6.PaymentInfo.readPaymentInfo
import models.V6.PaymentInfo.writePaymentInfo
import play.api.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}



object PaymentRecordController extends Controller with Secured {
  private val logger: Logger = Logger(classOf[PaymentInfo])
  def create = Action(parse.json) {
    request =>
      request.body.validate[PaymentInfo].map {
        case (payment) if payment.isValid =>
          logger.info(request.body.toString())
          payment.save(request.body.toString())
          Ok("success")
        case _ =>
          Ok(Json.toJson(ErrorResponse("错误的webhook数据。(invalid webhook data)")))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def index(parentId: Option[String]) = Action {
    Ok(Json.toJson(List[PaymentInfo]()))
  }

}
