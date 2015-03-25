package models

import anorm.SqlParser._
import anorm.{~, _}
import play.api.Play.current
import play.api.db.DB

case class AppPackage(version_code: Int, url: String, file_size: Long, version_name: String, summary: String, release_time: Long, package_type: Option[String] = None, id: Option[Long])

case class AppUpgradeResponse(error_code: Int, url: Option[String], size: Option[Long], version: Option[String], summary: Option[String], package_type: Option[String] = None)

object AppPackage {
  def delete(id: Long) = DB.withConnection {
    implicit c =>
      SQL("delete from appinfo where uid={id}")
        .on('id -> id).execute()
  }


  def forceUpdate = latest(Some("parent")).fold(new ForceUpdateResponse)({
    case lastPackage: AppPackage =>
      ForceUpdateResponse(lastPackage.url)
  })

  def create(app: (String, String, Long, String, Int, Option[String])) = DB.withConnection {
    implicit c =>
      SQL("insert into appinfo (version_code, version_name, url, summary, file_size, release_time, package_type) VALUES" +
        " ({version_code}, {version_name}, {url}, {summary}, {size}, {timestamp}, {type})")
        .on('version_code -> app._5,
          'version_name -> app._4,
          'url -> app._2,
          'summary -> app._1,
          'size -> app._3,
          'timestamp -> System.currentTimeMillis,
          'type -> app._6.getOrElse("parent")
        ).executeInsert()
      latest()
  }


  def response(pkg: AppPackage) = AppUpgradeResponse(0, Some(pkg.url), Some(pkg.file_size), Some(pkg.version_name), Some(pkg.summary), pkg.package_type)

  def noUpdate = AppUpgradeResponse(1, None, None, None, None)

  val simple = {
    get[Long]("uid") ~
      get[Int]("version_code") ~
      get[String]("url") ~
      get[String]("version_name") ~
      get[String]("summary") ~
      get[Option[String]]("package_type") ~
      get[Long]("file_size") ~
      get[Long]("release_time") map {
      case uid ~ code ~ url ~ name ~ summary ~ pkgType ~ size ~ release =>
        AppPackage(code, url, size, name, summary, release, pkgType, Some(uid))
    }
  }

  def latest(typ: Option[String] = None) = DB.withConnection {
    implicit c =>
      SQL("select * from appinfo where version_code=(SELECT MAX(version_code) FROM appinfo where package_type={type})")
        .on('type -> typ.getOrElse("parent")).as(simple singleOpt)
  }
}
