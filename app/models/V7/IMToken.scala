package models.V7

import java.security.MessageDigest
import models.BindingResponseV1
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

object CodeUtil {
  def hexSHA1(value: String): String = {
    try {
      val md: MessageDigest = MessageDigest.getInstance("SHA-1")
      md.update(value.getBytes("utf-8"))
      val digest: Array[Byte] = md.digest
      return byteToHexString(digest)
    }
    catch {
      case ex: Exception => {
        throw new RuntimeException(ex)
      }
    }
  }

  def byteToHexString(bytes: Array[Byte]): String = {
    return String.valueOf(Hex.encodeHex(bytes))
  }
}

