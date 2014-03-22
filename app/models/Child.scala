package models

import play.api.db.DB
import anorm._
import anorm.SqlParser._
import play.api.Play.current
import java.util.Date
import play.Logger
import models.helper.TimeHelper.any2DateTime

case class ChildInfo(child_id: Option[String], name: String, nick: String, birthday: String, gender: Int, portrait: Option[String], class_id: Int, class_name: Option[String], timestamp: Option[Long], school_id: Option[Long])

object Children {
  def idExists(childId: Option[String]): Boolean = DB.withConnection {
    implicit c =>
      childId match {
        case Some(id) =>
          SQL("select count(1) as count from childinfo where child_id={id}")
            .on('id -> id)
            .as(get[Long]("count") single) > 0
        case None => false
      }
  }

  def updateByChildId(kg: Long, childId: String, child: ChildInfo) = DB.withConnection {
    implicit c =>
      SQL("update childinfo set name={name},nick={nick},gender={gender},class_id={class_id}," +
        "birthday={birthday},picurl={picurl}, update_at={timestamp} where child_id={child_id}")
        .on(
          'name -> child.name,
          'nick -> child.nick,
          'gender -> child.gender,
          'class_id -> child.class_id,
          'birthday -> child.birthday,
          'picurl -> child.portrait.getOrElse(""),
          'timestamp -> System.currentTimeMillis,
          'child_id -> childId
        ).executeUpdate
      info(kg, childId)
  }


  def findById(kg: Long, uid: Long) = DB.withConnection {
    implicit c =>
      SQL("select c.*, c2.class_name from childinfo c, classinfo c2 where c.class_id=c2.class_id " +
        "and c.school_id={kg} and c2.school_id=c.school_id and c.uid={uid}")
        .on(
          'uid -> uid,
          'kg -> kg.toString
        ).as(childInformation singleOpt)

  }

  def create(kg: Long, child: ChildInfo) = DB.withConnection {
    implicit c =>
      val timestamp = System.currentTimeMillis
      val childId = child.child_id.getOrElse("1_%d".format(timestamp))
      val childUid : Option[Long] = SQL("INSERT INTO childinfo(name, child_id, student_id, gender, classname, picurl, birthday, " +
        "indate, school_id, address, stu_type, hukou, social_id, nick, status, update_at, class_id) " +
        "VALUES ({name},{child_id},{student_id},{gender},{classname},{picurl},{birthday},{indate}," +
        "{school_id},{address},{stu_type},{hukou},{social_id},{nick},{status},{timestamp},{class_id})")
        .on(
          'name -> child.name,
          'child_id -> childId,
          'student_id -> "%d".format(timestamp).take(5),
          'gender -> child.gender,
          'classname -> "",
          'picurl -> child.portrait.getOrElse(""),
          'birthday -> child.birthday,
          'indate -> child.birthday,
          'school_id -> kg.toString,
          'address -> "address",
          'stu_type -> 2,
          'hukou -> 1,
          'social_id -> "social_id",
          'nick -> child.nick,
          'status -> 1,
          'class_id -> child.class_id,
          'timestamp -> timestamp).executeInsert()
      Logger.info("created childinfo %s".format(childUid))
      childUid.flatMap {
        c =>
        findById(kg, c)
      }
  }


  val childInformation = {
    get[String]("school_id") ~
      get[String]("child_id") ~
      get[String]("name") ~
      get[String]("nick") ~
      get[Option[String]]("picurl") ~
      get[Int]("gender") ~
      get[Date]("birthday") ~
      get[Int]("childinfo.class_id") ~
      get[String]("classinfo.class_name") ~
      get[Long]("childinfo.update_at") map {
      case schoolId ~ childId ~ childName ~ nick ~ icon_url ~ childGender
        ~ childBirthday ~ classId ~ className ~ t =>
        new ChildInfo(Some(childId), childName, nick, childBirthday.toDateOnly, childGender.toInt,
          Some(icon_url.getOrElse("")), classId, Some(className), Some(t), Some(schoolId.toLong))
    }
  }

  def info(kg: Long, childId: String): Option[ChildInfo] = DB.withConnection {
    implicit c =>
      SQL("select cd.*, ci.class_name from childinfo cd, classinfo ci where ci.class_id=cd.class_id " +
        "and ci.school_id={kg} and ci.school_id=cd.school_id and cd.child_id={child_id}")
        .on('child_id -> childId, 'kg -> kg.toString).as(childInformation singleOpt)
  }

  def findAllInClass(kg: Long, classId: Option[Long], connected: Option[Boolean]) = DB.withConnection {
    implicit c =>
      val sql = "select c.*, c2.class_name from childinfo c, classinfo c2 " +
        "where c.class_id=c2.class_id and c.status=1 and c.school_id={kg} and c.school_id=c2.school_id "
      SQL(generateSQL(sql, classId, connected))
        .on(
          'classId -> classId.getOrElse(0),
          'kg -> kg.toString
        ).as(childInformation *)
  }


  def generateSQL(sql: String, classId: Option[Long], connected: Option[Boolean]): String = {
    sql + classId.map {
      l => " and c.class_id={classId} "
    }.getOrElse("") + connected.map {
      case (c) if c || c == 1 => " and child_id in (select child_id from relationmap r where r.status=1)"
      case _ => ""
    }.getOrElse("")
  }

  def findAll(school: Long, phone: String): List[ChildInfo] = DB.withConnection {
    implicit c =>
      SQL("select c.*, c2.class_name from childinfo c, relationmap r, parentinfo p, classinfo c2 " +
        " where c.class_id=c2.class_id and p.status=1 and c.status=1 and r.child_id = c.child_id " +
        " and p.parent_id = r.parent_id and c.school_id=c2.school_id and p.phone={phone} " +
        "and c.school_id={kg} and r.status=1")
        .on('phone -> phone, 'kg -> school.toString).as(childInformation *)
  }

  def show(schoolId: Long, phone: String, child: String): Option[ChildInfo] = DB.withConnection {
    implicit c =>
      SQL("select c.*, c2.class_name from childinfo c, classinfo c2 where c.school_id=c2.school_id " +
        "and c.class_id=c2.class_id and c.child_id={child_id} and c.school_id={kg} and c.status=1")
        .on('child_id -> child, 'kg -> schoolId.toString).as(childInformation singleOpt)
  }


}