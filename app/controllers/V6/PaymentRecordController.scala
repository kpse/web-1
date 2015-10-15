package controllers.V6

import controllers.Secured
import models.V6.PaymentInfo
import models.V6.PaymentInfo.{readPaymentInfo, writePaymentInfo}
import play.api.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}

object PaymentRecordController extends Controller with Secured {
  private val logger: Logger = Logger(classOf[PaymentInfo])
  def create = Action(parse.json) {
    request =>
      logger.info(request.body.toString())
      request.body.validate[PaymentInfo].map {
        case (payment) if payment.isValid =>
          payment.save(request.body.toString())
          Ok("success")
        case _ =>
          logger.warn("错误的webhook数据。(invalid webhook data)")
          Ok("success")
      }.recoverTotal {
        e => logger.warn("Detected error:" + JsError.toFlatJson(e))
          Ok("success")
      }
  }

  def index(parentId: Option[String]) = Action {
    Ok(Json.toJson(List[PaymentInfo]()))
  }

}
