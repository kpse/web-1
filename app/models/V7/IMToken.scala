package models.V7

import io.rong.util.CodeUtil
import models.BindingResponseV1
import play.Logger
import play.api.Play
import play.api.libs.json.Json
import play.api.libs.ws.WS

import scala.concurrent.Future

case class IMTokenReq(userId: String, name: String, portraitUri: String)

case class IMTokenRes(code: Int, token: String, userId: String)


object IMToken {
  implicit val writeIMToken = Json.writes[IMTokenReq]
  implicit val readIMTokenRes = Json.reads[IMTokenRes]

  def retrieveIMToken(result: BindingResponseV1, body: Option[String])(implicit exec: scala.concurrent.ExecutionContextExecutor): Future[Option[IMTokenRes]] = {
    val key = Play.current.configuration.getString("im.ak").getOrElse("")
    val secret = Play.current.configuration.getString("im.sk").getOrElse("")
    val nonce: String = String.valueOf(Math.random * 10000000).take(7)
    val timestamp: String = String.valueOf(System.currentTimeMillis)
    val sign: String = CodeUtil.hexSHA1(s"$secret$nonce$timestamp")
    val url = "https://api.cn.ronghub.com/user/getToken.json"
    WS.url(url).withHeaders("RC-App-Key" -> key, "RC-Nonce" -> nonce, "RC-Timestamp" -> timestamp,
      "RC-Signature" -> sign, "Content-Type" -> "application/x-www-form-urlencoded ").post(body.getOrElse("")).map {
      response =>
        Logger.info(s"response = ${response.json}")
        Json.fromJson[IMTokenRes](response.json).asOpt
    }
  }

}
