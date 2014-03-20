package models

import play.api.db.DB
import anorm._
import anorm.SqlParser._
import anorm.~
import play.api.Play.current

case class School(school_id: Long, name: String)

case class SchoolClass(school_id: Long, class_id: Option[Int], name: String, manager: Option[String])

object School {
  def classExists(kg: Long, classId: Int) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from classinfo where school_id = {kg} and class_id={class_id} and status=1")
        .on(
          'kg -> kg.toString,
          'class_id -> classId
        ).as(get[Long]("count(1)") single) > 0
  }

  def hasChildrenInClass(kg: Long, classId: Long): Boolean = DB.withConnection {
    implicit c =>
      SQL("select count(1) from childinfo where school_id = {kg} and class_id={class_id} and status=1")
        .on(
          'kg -> kg.toString,
          'class_id -> classId
        ).as(get[Long]("count(1)") single) > 0
  }

  def removeClass(kg: Long, classId: Long) = DB.withConnection {
    implicit c =>
      SQL("update classinfo set status=0 where school_id = {kg} and class_id={class_id} and status=1")
        .on(
          'kg -> kg.toString,
          'class_id -> classId
        ).execute()
  }


  def findById(clazz: SchoolClass) = DB.withConnection {
    implicit c =>
      SQL("select c.*, employee_id from classinfo c left join (privilege p) " +
        "on (p.school_id=c.school_id and cast(c.class_id as varchar(20)) " +
        "and c.school_id = {school_id} and class_id={class_id}) limit 1")
        .on('school_id -> clazz.school_id.toString,
          'class_id -> clazz.class_id).as(simple singleOpt)
  }

  def updateManager(clazz: SchoolClass) = DB.withConnection {
    implicit c =>
      clazz.manager map {
        manager =>
          val exists = SQL("select count(1) from privilege where school_id={kg} and employee_id={id}").on(
            'kg -> clazz.school_id,
            'id -> manager
          ).as(get[Long]("count(1)") single) > 0
          exists match {
            case false =>
              SQL("insert into privilege (school_id, employee_id, `group`, subordinate, promoter, update_at) values " +
                "({kg},{id},{group},{subordinate},{promoter}, {time})").on(
                  'kg -> clazz.school_id.toString,
                  'id -> manager,
                  'group -> "teacher",
                  'promoter -> "admin",
                  'subordinate -> clazz.class_id,
                  'time -> System.currentTimeMillis
                ).executeInsert()
            case true =>
              SQL("update privilege set subordinate={class_id} where school_id={kg} and employee_id={id}").on(
                'kg -> clazz.school_id.toString,
                'class_id -> clazz.class_id,
                'id -> manager
              ).executeUpdate()
          }

      }

  }

  def updateOrCreate(clazz: SchoolClass): Option[SchoolClass] = DB.withConnection {
    implicit c =>
      val exist = SQL("select count(1) from classinfo where school_id={kg} and class_id={id}").on(
        'kg -> clazz.school_id,
        'id -> clazz.class_id
      ).as(get[Long]("count(1)") single)
      exist match {
        case (0l) => createClass(clazz.school_id, clazz)
        case (1l) => update(clazz)
      }
      updateManager(clazz)
      findById(clazz)
  }

  def update(clazz: SchoolClass) = DB.withConnection {
    implicit c =>
      SQL("update classinfo set class_name={name} where school_id={school_id} and class_id={class_id}")
        .on('school_id -> clazz.school_id.toString,
          'class_id -> clazz.class_id,
          'name -> clazz.name).executeUpdate()

  }

  def generateClassId(kg: Long): Int = DB.withConnection {
    implicit c =>
      SQL("select max(class_id) as max from classinfo where school_id = {school_id}")
        .on('school_id -> kg.toString).as(get[Int]("max") single)
  }


  def createClass(kg: Long, classInfo: SchoolClass) = DB.withConnection {
    implicit c =>
      val insert = SQL("insert into classinfo (school_id, class_id, class_name) values ({kg}, {class_id}, {name})")
        .on('kg -> kg.toString,
          'class_id -> classInfo.class_id.getOrElse(generateClassId(kg)),
          'name -> classInfo.name).executeInsert()
      SQL("select * from classinfo where uid={uid}")
        .on('uid -> insert).as(simple single)
  }

  val simple = {
    get[Int]("class_id") ~
      get[String]("school_id") ~
      get[String]("class_name") ~
      get[Option[String]]("employee_id") map {
      case id ~ school_id ~ name ~ manager =>
        SchoolClass(school_id.toLong, Some(id), name, manager)
    }
  }

  def allClasses(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select c.*, employee_id " +
        "from classinfo c join (schoolinfo s) " +
        "on (s.school_id = c.school_id and c.school_id={kg} and c.status=1) " +
        " left join (privilege p) on (p.subordinate=cast(c.class_id as varchar(20)) " +
        "and p.school_id=c.school_id )")
        .on('kg -> kg.toString)
        .as(simple *)
  }

}