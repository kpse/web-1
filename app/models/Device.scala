package models

import play.api.db.DB
import anorm._
import anorm.SqlParser._
import play.api.Play.current
import helper.MD5Helper.md5

case class Device(id: Option[Long], mac: String, school_id: Long) {
  def duplicated: Boolean = DB.withConnection {
    implicit c =>
      SQL("select count(1) from macwhitelist where encoded_mac={encoded} and status=1 and uid <> {id}")
        .on('encoded -> md5(mac), 'id -> id)
        .as(get[Long]("count(1)") single) > 0
  }

  def exists() = DB.withConnection {
    implicit c =>
      SQL("select count(1) from macwhitelist where encoded_mac={encoded} and status=1")
        .on('encoded -> md5(mac))
        .as(get[Long]("count(1)") single) > 0
  }

  def update() = DB.withConnection {
    implicit c =>
      SQL("update macwhitelist set school_id={kg}, mac={mac}, encoded_mac={encoded}, update_at={time} where uid={id}")
        .on('kg -> school_id.toString,'mac -> mac, 'encoded -> md5(mac), 'time -> System.currentTimeMillis, 'id -> id)
        .executeUpdate()
  }

  def create() = DB.withConnection {
    implicit c =>
      SQL("insert into macwhitelist (school_id, mac, encoded_mac, update_at) values ({kg}, {mac}, {encoded}, {time})")
        .on('kg -> school_id.toString,'mac -> mac, 'encoded -> md5(mac), 'time -> System.currentTimeMillis())
        .executeInsert()
  }
}

object Device {
  def delete(id: Long) = DB.withConnection {
    implicit c =>
      SQL("delete from macwhitelist where uid={id}")
        .on('id -> id).execute()
  }

  val simple = {
    get[Long]("uid") ~
    get[String]("school_id") ~
      get[String]("mac") map {
      case id ~ kg ~ mac =>
        Device(Some(id), mac, kg.toLong)
    }
  }

  def all = DB.withConnection {
    implicit c =>
      SQL("select * from macwhitelist").as(simple *)
  }

  def exists(encoded: String) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from macwhitelist where encoded_mac={encoded} and status=1")
        .on('encoded -> encoded)
        .as(get[Long]("count(1)") single) > 0
  }

}
