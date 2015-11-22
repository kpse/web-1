package models.V7

import java.security.MessageDigest

import anorm.SqlParser._
import anorm.{~, _}
import models._
import org.apache.commons.codec.binary.Hex
import play.api.Play
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.{Json, Reads}
import play.api.libs.ws.WS

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class IMTokenReq(userId: String, name: String, portraitUri: String)

case class IMToken(source: String, token: String, user_id: String)

case class IMClassGroup(source: String, school_id: Long, class_id: Int, group_id: String, group_name: String) {
  def exists(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from im_class_group where school_id={kg} and class_id={id} and status=1").on('kg -> kg, 'id -> class_id).as(get[Long]("count(1)") single) > 0
  }

  def save(kg: Long) = DB.withConnection {
    implicit c =>
      exists(kg) match {
        case true =>
          SQL("update im_class_group set group_name={group_name}, group_id={group_id}, updated_at={time} where school_id={kg} and class_id={id}")
            .on('kg -> kg, 'id -> class_id, 'group_id -> group_id, 'group_name -> group_name, 'time -> System.currentTimeMillis).executeUpdate()
        case false =>
          SQL("insert into im_class_group (school_id, class_id, group_id, group_name, updated_at, created_at) VALUES ({kg}, {id}, {group_id}, {group_name}, {time}, {time})")
            .on('kg -> kg, 'id -> class_id, 'group_id -> group_id, 'group_name -> group_name, 'time -> System.currentTimeMillis).executeInsert()
      }
  }
}


trait IMResponseTrait {
  val code: Int
}

case class IMBasicRes(code: Int) extends IMResponseTrait

case class IMTokenRes(code: Int, token: String, userId: String) extends IMResponseTrait {

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
  implicit val writeIMTokenReq = Json.writes[IMTokenReq]
  implicit val readIMTokenRes = Json.reads[IMTokenRes]
  implicit val readIMToken = Json.reads[IMToken]
  implicit val writeIMToken = Json.writes[IMToken]
  implicit val writeIMBasicRes = Json.writes[IMClassGroup]
  implicit val readsIMBasicRes = Json.reads[IMBasicRes]
  val key = Play.current.configuration.getString("im.ak").getOrElse("")
  val secret = Play.current.configuration.getString("im.sk").getOrElse("")
  val urlPrefix = "https://api.cn.ronghub.com/"
  type IMWS[T <: IMResponseTrait] = (Long, String, Reads[T]) => Future[Option[T]]

  def token(kg: Long, account: IMAccount)(implicit ws: IMWS[IMTokenRes] = rongyunWS[IMTokenRes]): Future[Option[IMToken]] = {
    val dbEntity: Option[IMToken] = show(kg, account.imUserId)
    dbEntity match {
      case Some(entity) =>
        Future {
          dbEntity
        }
      case None =>
        retrieveIMToken(kg, Some(account.imUserInfo))(ws)
    }
  }

  def show(kg: Long, userId: String) = DB.withConnection {
    implicit c =>
      SQL("select * from im_token where school_id={kg} and user_id={id} limit 1").on('kg -> kg, 'id -> userId).as(simple singleOpt)
  }

  val simple = {
    get[String]("user_id") ~
      get[String]("token") map {
      case id ~ token =>
        IMToken("db", token, id)
    }
  }

  val simpleClassGroup = {
    get[String]("school_id") ~
      get[Int]("class_id") ~
      get[String]("group_id") ~
      get[String]("group_name") map {
      case kg ~ classId ~ id ~ name =>
        IMClassGroup("db", kg.toLong, classId, id, name)
    }
  }

  def retrieveIMToken(kg: Long, body: Option[String])(implicit ws: IMWS[IMTokenRes] = rongyunWS[IMTokenRes]): Future[Option[IMToken]] = {
    body match {
      case Some(request) =>
        ws(kg, request, readIMTokenRes).map {
          maybeToken =>
            maybeToken foreach (_.save(kg))
            maybeToken.map(t => IMToken("internet", t.token, t.userId))
        }
      case None => Future(None)
    }
  }

  def classGroup(kg: Long, username: String, id: Long, imAccount: Option[IMAccount])(implicit ws: IMWS[IMBasicRes] = rongyunWS[IMBasicRes]): Future[Option[IMClassGroup]] = {
    eligibleClasses(kg, username, imAccount).find(_.class_id == Some(id)) match {
      case Some(clazz) =>
        clazz.imInfo match {
          case Some(info) =>
            Future.successful(Some(info))
          case _ =>
            IMToken.createClassGroup(kg, Some(imAccount.get.imClassInfo(clazz))).map {
              case Some(classGroup) =>
                val updated: IMClassGroup = classGroup.copy(school_id = kg, class_id = id.toInt, group_id = s"${clazz.school_id}_${clazz.class_id.get}", group_name = clazz.name)
                updated.save(kg)
                Some(updated)
              case None => None
            }
        }
      case None =>
        Future.successful(None)
    }
  }

  def createClassGroup(kg: Long, body: Option[String])(implicit ws: IMWS[IMBasicRes] = rongyunWS[IMBasicRes]): Future[Option[IMClassGroup]] = {
    body match {
      case Some(request) =>
        ws(kg, request, readsIMBasicRes).map {
          case Some(res) if res.code == 200 =>
            Some(IMClassGroup("internet", 0, 0, "", ""))
          case _ => None
        }
      case None => Future(None)
    }
  }

  def eligibleClasses(kg: Long, username: String, imAccount: Option[IMAccount]): List[SchoolClass] = {
    imAccount match {
      case Some(Employee(_, _, _, _, _, _, _, _, _, loginName, _, _, _, _, _, _)) =>
        UserAccess.filter(UserAccess.queryByUsername(loginName, kg))(School.allClasses(kg))
      case Some(Parent(_, _, _, phone, _, _, _, _, _, _, _, _, _, _)) =>
        val classes: List[Int] = Relationship.index(kg, Some(phone), None, None).map(_.child.get.class_id)
        School.allClasses(kg).filter(c => classes.contains(c.class_id.get))
      case _ => List()
    }
  }

  def rongyunWS[T <: IMResponseTrait](kg: Long, request: String, reads: Reads[T]): Future[Option[T]] = {
    val nonce: String = String.valueOf(Math.random * 10000000).take(7)
    val timestamp: String = String.valueOf(System.currentTimeMillis)
    val sign: String = hexSHA1(s"$secret$nonce$timestamp")
    val url = s"${urlPrefix}user/getToken.json"
    WS.url(url).withHeaders("RC-App-Key" -> key, "RC-Nonce" -> nonce, "RC-Timestamp" -> timestamp,
      "RC-Signature" -> sign, "Content-Type" -> "application/x-www-form-urlencoded ").post(request).map(_.json).map {
      response =>
        Json.fromJson[T](response)(reads).asOpt
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


