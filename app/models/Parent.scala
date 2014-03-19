package models

import anorm.SqlParser._
import anorm._
import play.api.db.DB
import play.api.Play.current
import models.json_models.BindNumberResponse._
import anorm.~
import java.util.Date
import play.Logger
import models.helper.TimeHelper.any2DateTime

case class Parent(parent_id: Option[String], school_id: Long, name: String, phone: String, portrait: Option[String], gender: Int, birthday: String, timestamp: Option[Long], member_status: Option[Int], status: Option[Int])

case class ParentInfo(id: Option[Long], birthday: String, gender: Int, portrait: String, name: String, phone: String, kindergarten: School, relationship: String, child: ChildInfo, card: String)


object Parent {

  def allowToAccess(phone: String, token: Option[String], kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from parentinfo p, accountinfo a where a.accountid=p.phone " +
        "and phone={phone} and school_id={kg} and a.pwd_change_time={token} and status=1")
        .on(
          'kg -> kg.toString,
          'phone -> phone,
          'token -> token
        ).as(get[Long]("count(1)") single) > 0
  }

  def canAccess(phoneNum: Option[String], token: Option[String], kg: Long) = phoneNum.exists {
    case (phone) if allowToAccess(phone, token, kg) => true
    case _ => false
  }

  def existsInOtherSchool(parent: Parent) = DB.withConnection {
    implicit c =>
      SQL("select count(1) as count from parentinfo where phone={phone}")
        .on(
          'phone -> parent.phone
        ).as(get[Long]("count") single) > 0
  }

  def idExists(parentId: Option[String]): Boolean = DB.withConnection {
    implicit c =>
      parentId match {
        case Some(id) =>
          SQL("select count(1) as count from parentinfo where parent_id={parent_id}")
            .on('parent_id -> id)
            .as(get[Long]("count") single) > 0
        case None => false
      }
  }

  def phoneExists(kg: Long, phone: String): Boolean = DB.withConnection {
    implicit c =>
      SQL("select count(1) as count from parentinfo where phone={phone} and school_id={kg}")
        .on(
          'phone -> phone,
          'kg -> kg.toString
        ).as(get[Long]("count") single) > 0
  }

  def updatePushAccount(parent: Parent) = DB.withConnection {
    implicit c =>
      val oldPhone = oldPhoneNumber(parent)
      if (isConflicting(parent)) throw new IllegalAccessError("电话号码已经存在。")
      SQL("update accountinfo set accountid={phone}, " +
        "password={password},pushid='', active=0, pwd_change_time=0 where accountid={old_phone}")
        .on('old_phone -> oldPhone,
          'phone -> parent.phone,
          'password -> generateNewPassword(parent.phone)).executeUpdate()
  }

  def oldPhoneNumber(parent: Parent) = DB.withConnection {
    implicit c =>
      SQL("select phone from parentinfo where parent_id={parent_id}")
        .on('parent_id -> parent.parent_id).as(get[String]("phone") single)
  }

  def isConflicting(parent: Parent) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from accountinfo where accountid={phone}")
        .on('phone -> parent.phone).as(get[Long]("count(1)") single) > 0
  }

  def update(parent: Parent) = DB.withConnection {
    implicit c =>
      val timestamp = System.currentTimeMillis
      updatePushAccount(parent)
      SQL("update parentinfo set name={name}, " +
        "phone={phone}, gender={gender}, company={company}, " +
        "picurl={picurl}, birthday={birthday}, " +
        "update_at={timestamp}, member_status={member} where parent_id={parent_id}")
        .on('name -> parent.name,
          'phone -> parent.phone,
          'gender -> parent.gender,
          'company -> "",
          'picurl -> parent.portrait.getOrElse(""),
          'birthday -> parent.birthday,
          'parent_id -> parent.parent_id,
          'member -> parent.member_status.getOrElse(0),
          'timestamp -> timestamp).executeUpdate()
      info(parent.school_id, parent.parent_id.get)
  }

  def updateWithPhone(kg: Long, parent: Parent) = DB.withConnection {
    implicit c =>
      val timestamp = System.currentTimeMillis
      SQL("update parentinfo set name={name}, gender={gender}, company={company}, " +
        "picurl={picurl}, birthday={birthday}, " +
        "update_at={timestamp}, member_status={member} where phone={phone} and school_id={kg}")
        .on('name -> parent.name,
          'phone -> parent.phone,
          'gender -> parent.gender,
          'company -> "",
          'picurl -> parent.portrait.getOrElse(""),
          'birthday -> parent.birthday,
          'timestamp -> timestamp,
          'member -> parent.member_status.getOrElse(0),
          'kg -> kg.toString
        ).executeUpdate()
      findByPhone(parent.school_id)(parent.phone)
  }

  def findByPhone(kg: Long)(phone: String) = DB.withConnection {
    implicit c =>
      SQL(fullStructureSql + " and p.phone={phone}")
        .on('kg -> kg,
          'phone -> phone)
        .as(withRelationship.singleOpt)
  }

  def delete(kg: Long)(phone: String) = DB.withConnection {
    implicit c =>
      SQL("update parentinfo set status=0 where phone={phone}")
        .on('phone -> phone
        ).executeUpdate
  }

  def findById(kg: Long)(id: Long) = DB.withConnection {
    implicit c =>
      SQL(fullStructureSql + " and p.uid={id}")
        .on('kg -> kg,
          'id -> id)
        .as(withRelationship.singleOpt)
  }

  def create(kg: Long, parent: Parent) = DB.withConnection {
    implicit c =>
      val timestamp = System.currentTimeMillis
      val parent_id = parent.parent_id.getOrElse("2_%d_%d".format(kg, timestamp))
      val createdId: Option[Long] = SQL("INSERT INTO parentinfo(name, parent_id, relationship, phone, gender, company, picurl, birthday, school_id, status, update_at, member_status) " +
        "VALUES ({name},{parent_id},{relationship},{phone},{gender},{company},{picurl},{birthday},{school_id},{status},{timestamp},{member})")
        .on(
          'name -> parent.name,
          'parent_id -> parent_id,
          'relationship -> "",
          'phone -> parent.phone,
          'gender -> parent.gender,
          'company -> "",
          'picurl -> parent.portrait.getOrElse(""),
          'birthday -> parent.birthday,
          'school_id -> kg.toString,
          'status -> 1,
          'member -> parent.member_status.getOrElse(0),
          'timestamp -> timestamp).executeInsert()
      Logger.info("created parent %s".format(createdId))
      val accountinfoUid = createPushAccount(parent)
      Logger.info("created accountinfo %s".format(accountinfoUid))
      createdId.flatMap {id => info(parent.school_id, parent_id)}
  }


  def createPushAccount(parent: Parent): Option[Long] = DB.withConnection {
    implicit c =>
      if (isConflicting(parent)) throw new IllegalAccessError("电话号码已经存在。")
      SQL("INSERT INTO accountinfo(accountid, password, pushid, active, pwd_change_time) " +
        "VALUES ({accountid},{password},'',0,0)")
        .on('accountid -> parent.phone,
          'password -> generateNewPassword(parent.phone)).executeInsert()
  }

  val withRelationship = {
    get[Long]("parentinfo.uid") ~
      get[String]("school_id") ~
      get[String]("parentinfo.name") ~
      get[Date]("parentinfo.birthday") ~
      get[Int]("parentinfo.gender") ~
      get[Option[String]]("parentinfo.picurl") ~
      get[String]("schoolinfo.name") ~
      get[String]("parentinfo.school_id") ~
      get[String]("parentinfo.relationship") ~
      get[String]("childinfo.name") ~
      get[String]("nick") ~
      get[Date]("childinfo.birthday") ~
      get[Int]("childinfo.gender") ~
      get[Option[String]]("childinfo.picurl") ~
      get[String]("childinfo.child_id") ~
      get[Int]("class_id") ~
      get[String]("card_num") ~
      get[String]("phone") ~
      get[String]("classinfo.class_name") ~
      get[Long]("childinfo.update_at") map {
      case id ~ k_id ~ name ~ birthday ~ gender ~ portrait ~
        schoolName ~ schoolId ~ relationship ~ childName ~
        nick ~ childBirthday ~ childGender ~ childPortrait ~ child_id ~ classId ~ card ~ phone ~ className ~ childTime =>
        new ParentInfo(Some(id), birthday.toDateOnly, gender.toInt, portrait.getOrElse(""), name, phone,
          new School(schoolId.toLong, schoolName), relationship,
          new ChildInfo(Some(child_id), childName, nick, childBirthday.toDateOnly, childGender.toInt,
            Some(childPortrait.getOrElse("")), classId, Some(className), Some(childTime), Some(schoolId.toLong)), card)
    }
  }


  def show(kg: Long, phone: String) = DB.withConnection {
    implicit c =>
      SQL(simpleSql + " and p.phone = {phone}")
        .on('kg -> kg,
          'phone -> phone)
        .as(simple singleOpt)
  }

  val fullStructureSql = "select p.*, s.name, c.*, ci.class_name from parentinfo p, schoolinfo s, childinfo c, relationmap r, classinfo ci " +
    "where p.school_id = s.school_id and s.school_id={kg} and p.status=1 and ci.class_id=c.class_id " +
    "and r.child_id = c.child_id and r.parent_id = p.parent_id"

  @deprecated(message="no use anymore", since="20140320")
  def all(kg: Long, classId: Option[Long]): List[ParentInfo] = DB.withConnection {
    implicit c =>
      classId match {
        case Some(id) =>
          Logger.info("id = " + id.toString)
          SQL(fullStructureSql + " and c.class_id={class_id}")
            .on('kg -> kg,
              'class_id -> id.toString)
            .as(withRelationship *)
        case None =>
          SQL(fullStructureSql)
            .on('kg -> kg)
            .as(withRelationship *)
      }

  }

  val simple = {
    get[String]("parent_id") ~
      get[String]("school_id") ~
      get[String]("parentinfo.name") ~
      get[String]("phone") ~
      get[Int]("parentinfo.gender") ~
      get[Option[String]]("parentinfo.picurl") ~
      get[Date]("parentinfo.birthday") ~
      get[Int]("member_status") ~
      get[Int]("parentinfo.status") ~
      get[Long]("parentinfo.update_at") map {
      case id ~ kg ~ name ~ phone ~ gender ~ portrait ~ birthday ~ member ~ status ~ t =>
        new Parent(Some(id), kg.toLong, name, phone, Some(portrait.getOrElse("")), gender, birthday.toDateOnly, Some(t), Some(member), Some(status))
    }
  }

  val simpleSql = "select * from parentinfo p where school_id={kg} and status=1 "

  def info(kg: Long, parentId: String) = DB.withConnection {
    implicit c =>
      SQL(simpleSql + " and parent_id={parent_id} ")
        .on('kg -> kg,
          'parent_id -> parentId)
        .as(simple singleOpt)
  }

  def generateMemberQuery(member: Option[Boolean]): String = member match {
    case Some(m) => " and member_status={member}"
    case None => ""
  }

  def simpleIndex(kg: Long, member: Option[Boolean]) = DB.withConnection {
    implicit c =>
      SQL(simpleSql + generateMemberQuery(member))
        .on('kg -> kg, 'member -> (if (member.getOrElse(false)) 1 else 0))
        .as(simple *)
  }

  def indexInClass(kg: Long, classId: Long, member: Option[Boolean]) = DB.withConnection {
    implicit c =>
      SQL(fullStructureSql + " and c.class_id={class_id}" + generateMemberQuery(member))
        .on('kg -> kg,
          'class_id -> classId,
          'member -> (if (member.getOrElse(false)) 1 else 0)
        ).as(simple *)
  }

}
