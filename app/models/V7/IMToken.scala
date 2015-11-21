package models.V7

import java.security.MessageDigest

import models.IMAccount
import org.apache.commons.codec.binary.Hex
import play.Logger
import play.api.Play
import play.api.db.DB
import play.api.libs.json.Json
import play.api.libs.ws.WS
import anorm.SqlParser._
import anorm.{~, _}
import scala.concurrent.Future
import play.api.Play.current

case class IMTokenReq(userId: String, name: String, portraitUri: String)

case class IMTokenRes(code: Int, token: String, userId: String) {

  def exists(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from im_token where school_id={kg} and user_id={id} and status=1").on('kg -> kg, 'id -> userId).as(get[Long]("count(1)") single) > 0
  }

  def save(kg: Long) = DB.withConnection {
    implicit c =>
      exists(kg) match {
        case true =>
          SQL("update im_token set token={token}, updated_at={time} where school_id={kg} and user_id={id}")
            .on('kg -> kg, 'id -> userId, 'token -> token, 'time -> System.currentTimeMillis).executeUpdate()
        case false =>
          SQL("insert into im_token (school_id, user_id, token, updated_at, created_at) VALUES ({kg}, {id}, {token}, {time}, {time})")
            .on('kg -> kg, 'id -> userId, 'token -> token, 'time -> System.currentTimeMillis).executeInsert()
      }
  }
}


object IMToken {
  implicit val writeIMToken = Json.writes[IMTokenReq]
  implicit val readIMTokenRes = Json.reads[IMTokenRes]
  implicit val writeIMTokenRes = Json.writes[IMTokenRes]
  val key = Play.current.configuration.getString("im.ak").getOrElse("")
  val secret = Play.current.configuration.getString("im.sk").getOrElse("")
  val urlPrefix = "https://api.cn.ronghub.com/"

  def token(kg: Long, account: IMAccount)(implicit exec: scala.concurrent.ExecutionContextExecutor): Future[Option[IMTokenRes]] = {
    val dbEntity: Option[IMTokenRes] = show(kg, account.imUserId)
    dbEntity match {
      case Some(entity) =>
        Future {dbEntity}
      case None =>
        retrieveIMToken(kg, Some(account.imUserInfo))
    }
  }

  def show(kg: Long, userId: String) = DB.withConnection {
    implicit c =>
      SQL("select * from im_token where school_id={kg} and user_id={id}").on('kg -> kg, 'id -> userId).as(simple singleOpt)
  }

  val simple = {
    get[String]("user_id") ~
    get[String]("token") map {
      case id ~ token =>
        IMTokenRes(0, token, id)
    }

  }

  def retrieveIMToken(kg: Long, body: Option[String])(implicit exec: scala.concurrent.ExecutionContextExecutor): Future[Option[IMTokenRes]] = {
    body match {
      case Some(request) =>
        val nonce: String = String.valueOf(Math.random * 10000000).take(7)
        val timestamp: String = String.valueOf(System.currentTimeMillis)
        val sign: String = hexSHA1(s"$secret$nonce$timestamp")
        val url = s"${urlPrefix}user/getToken.json"
        WS.url(url).withHeaders("RC-App-Key" -> key, "RC-Nonce" -> nonce, "RC-Timestamp" -> timestamp,
          "RC-Signature" -> sign, "Content-Type" -> "application/x-www-form-urlencoded ").post(request).map {
          response =>
            Logger.info(s"response = ${response.json}")
            val maybeToken: Option[IMTokenRes] = Json.fromJson[IMTokenRes](response.json).asOpt
            maybeToken foreach(_.save(kg))
            maybeToken
        }
      case None => Future(None)
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


