package models

import play.api.db.DB
import anorm._
import anorm.SqlParser._
import play.api.Play.current
import helper.MD5Helper.md5

case class Device(mac: String, school_id: Long) {
  def create() = DB.withConnection {
    implicit c =>
      SQL("insert into macwhitelist (mac, encoded_mac, update_at) values ({mac}, {encoded}, {time})")
        .on('mac -> mac, 'encoded -> md5(mac), 'time -> System.currentTimeMillis())
        .executeInsert()
  }
}

object Device {
  val simple = {
    get[String]("school_id") ~
      get[String]("mac") map {
      case kg ~ mac =>
        Device(mac, kg.toLong)
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
