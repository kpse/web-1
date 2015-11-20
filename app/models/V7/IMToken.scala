package models.V7

import java.security.MessageDigest

import org.apache.commons.codec.binary.Hex
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
  implicit val writeIMTokenRes = Json.writes[IMTokenRes]
  val key = Play.current.configuration.getString("im.ak").getOrElse("")
  val secret = Play.current.configuration.getString("im.sk").getOrElse("")
  val urlPrefix = "https://api.cn.ronghub.com/"

  def retrieveIMToken(body: Option[String])(implicit exec: scala.concurrent.ExecutionContextExecutor): Future[Option[IMTokenRes]] = {
    val nonce: String = String.valueOf(Math.random * 10000000).take(7)
    val timestamp: String = String.valueOf(System.currentTimeMillis)
    val sign: String = hexSHA1(s"$secret$nonce$timestamp")
    val url = s"${urlPrefix}user/getToken.json"
    WS.url(url).withHeaders("RC-App-Key" -> key, "RC-Nonce" -> nonce, "RC-Timestamp" -> timestamp,
      "RC-Signature" -> sign, "Content-Type" -> "application/x-www-form-urlencoded ").post(body.getOrElse("")).map {
      response =>
        Logger.info(s"response = ${response.json}")
        Json.fromJson[IMTokenRes](response.json).asOpt
    }
  }

  def hexSHA1(value: String): String = {
      val md: MessageDigest = MessageDigest.getInstance("SHA-1")
      md.update(value.getBytes("utf-8"))
      val digest: Array[Byte] = md.digest
      byteToHexString(digest)
  }

  def byteToHexString(bytes: Array[Byte]): String = String.valueOf(Hex.encodeHex(bytes))
}


