package controllers

import play.api.mvc.{Action, Controller}
import models.json_models._
import play.api.libs.json.Json
import models.json_models.CheckInfo
import play.api.libs.ws.WS
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import play.Logger
import models.{DailyLog, Card}

object CheckInController extends Controller with Secured {

  case class CheckingInAndOutResponse(error_code: Int, error_msg: String)

  implicit val read = Json.reads[CheckInfo]
  implicit val write1 = Json.writes[CheckingInAndOutResponse]
  implicit val write2 = Json.writes[IOSField]
  implicit val write3 = Json.writes[CheckNotification]
  implicit val write4 = Json.writes[Card]

  @deprecated(message = "delegate to Baidu BAE server no more", since = "2014-02-10")
  def create(kg: Long) = Action.async(parse.json) {
    request =>
      Logger.info("checking : " + request.body)
      request.body.validate[CheckInfo].map {
        case (check) =>
          CheckingMessage.convert(check).map {
            c =>
              val url = "http://djcwebtest.duapp.com/forwardswipe.do"
              val json = Json.toJson(c)
              Logger.info("json sent to push server: %s".format(json.toString()))
              WS.url(url).post(json).map {
                response =>
                  Ok(response.json)
              }
          }
          Future {
            Ok(Json.toJson(CheckingInAndOutResponse(1, "未找到与卡号(%s)匹配的数据。".format(check.card_no))))
          }

      }.getOrElse(Future {
        BadRequest
      })
  }



  def show(kg: Long, cardId: String) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(Card.show(kg, cardId)))
  }

  def index(kg: Long) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(Card.index(kg)))
  }
}
