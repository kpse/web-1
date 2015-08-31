package models

import anorm.SqlParser._
import anorm._
import models.helper.MD5Helper.md5
import play.api.Play
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json


case class AvailableSlots(school_id: Long, count: Long)

case class RawVideoMember(account: String)

case class VideoMember(id: String, account: Option[String], password: Option[String], school_id: Option[Long]) {
  def update = DB.withConnection {
    implicit c =>
      SQL("update videomembers set account={account}, status=1 where parent_id={id}")
        .on('account -> generateAccount(account), 'id -> id).executeUpdate()
  }

  def create = DB.withConnection {
    implicit c =>
      SQL("insert into videomembers (parent_id, school_id, account) " +
        "values ({id}, {kg}, {account})")
        .on('kg -> school_id, 'account -> generateAccount(account), 'id -> id).executeInsert()
  }

  def isExisting = DB.withConnection {
    implicit c =>
      SQL("select count(1) from videomembers where school_id={kg} and parent_id={id}")
        .on('kg -> school_id, 'id -> id).as(get[Long]("count(1)") single) > 0
  }

  def isAccountDuplicated = DB.withConnection {
    implicit c =>
      SQL("select count(1) from videomembers where account={account} and status=1")
        .on('account -> account).as(get[Long]("count(1)") single) > 0
  }

  private def generateAccount(base: Option[String]) = {
    base match {
      case Some(key) => key
      case None => md5(s"$school_id$id").take(24).toLowerCase
    }
  }

}

object VideoMember {
  def default(kg: Long) = VideoMember("default", Some("8888"), Some("000000"), Some(kg))

  implicit val write = Json.writes[VideoMember]
  implicit val write2 = Json.writes[AvailableSlots]
  implicit val read = Json.reads[VideoMember]

  def all(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from videomembers where school_id={kg} and status=1").on('kg -> kg).as(simple *)
  }

  def show(kg: Long, id: String) = DB.withConnection {
    implicit c =>
      trialAccount(kg, id) orElse SQL("select * from videomembers where school_id={kg} and parent_id={id} and status=1")
        .on('kg -> kg, 'id -> id).as(simple singleOpt)
  }

  def delete(kg: Long, id: String) = DB.withConnection {
    implicit c =>
      SQL("update videomembers set status=0 where school_id={kg} and parent_id={id} and status=1")
        .on('kg -> kg, 'id -> id).executeUpdate()
  }

  def trialAccount(kg: Long, id: String): Option[VideoMember] = {
    for (
      account <- School.config(kg).config.find(_.name == "videoTrialAccount");
      password <- School.config(kg).config.find(_.name == "videoTrialPassword") if account.value.length > 0
    ) yield VideoMember(id, Some(account.value), Some(password.value), Some(kg))
  }

  def passwordOfVideo = Play.current.configuration.getString("video.provider.password")

  val simple = {
    get[String]("school_id") ~
      get[String]("parent_id") ~
      get[String]("account") map {
      case kg ~ id ~ account =>
        //        VideoMember(id, Some(account), passwordOfVideo, Some(kg.toLong))
        fakeAccountAccordingSchool(kg.toLong, id, account)

    }
  }

  def fakeAccountAccordingSchool(kg: Long, id: String, account: String) = kg match {
    case 2046 => VideoMember(id, Some("cocbaby"), Some("13880498549"), Some(kg))
    case 2001 => default(2001)
    case _ => VideoMember(id, Some(account), passwordOfVideo, Some(kg))
  }

  def availableCounting(kg: Long) = {
    get[Option[Long]]("count") map {
      case count =>
        AvailableSlots(kg, count.getOrElse(0))
    }
  }

  def available(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select ((select total_video_account from chargeinfo where school_id={kg} and status=1) - count(1)) as count from videomembers where school_id={kg} and status=1")
        .on('kg -> kg).as(availableCounting(kg) single)
  }

  def accountExists(kg: Long, account: String) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from videomembers where school_id={kg} and account={account}")
        .on('kg -> kg, 'account -> account).as(get[Long]("count(1)") single) > 0
  }

  def limitExceed(kg: Long): Boolean = available(kg).count <= 0
}

object RawVideoMember {
  implicit val writer = Json.writes[RawVideoMember]

  def validate(token: String): Option[String] = DB.withConnection {
    implicit c =>
      SQL("select school_id from videoprovidertoken where token={token}")
        .on('token -> token).as(get[String]("school_id") singleOpt)
  }

  def index(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select account from videomembers where school_id={kg}").on('kg -> kg).as(simple *)
  }

  val simple = {
    get[String]("account") map {
      case account =>
        RawVideoMember(account)
    }
  }

}

