package models

import play.api.db.DB
import anorm._
import anorm.SqlParser._
import anorm.~
import play.api.Play.current
import play.Logger

case class School(school_id: Long, name: String)

case class SchoolClass(school_id: Long, class_id: Option[Int], name: String, managers: Option[List[String]] = Some(List[String]()))


object School {
  def delete(kg: Long) = DB.withTransaction {
    implicit c =>

      try {
        SQL("delete from accountinfo where accountid in (select phone from parentinfo where school_id={kg})")
          .on('kg -> kg.toString).execute()
        SQL("delete from relationmap where child_id in (select child_id from childinfo where school_id={kg})")
          .on('kg -> kg.toString).execute()
        SQL("delete from relationmap where parent_id in (select parent_id from parentinfo where school_id={kg})")
          .on('kg -> kg.toString).execute()

        List("classinfo", "childinfo", "parentinfo", "news",
          "scheduleinfo", "cookbookinfo", "schoolinfo", "employeeinfo", "dailylog", "conversation",
          "assignment", "assess", "privilege", "chargeinfo").map {
          table =>
            SQL("delete from " + table + " where school_id={kg}")
              .on('kg -> kg.toString).execute()
        }
        c.commit
      }
      catch {
        case t: Throwable =>
          Logger.info("error %s".format(t.toString))
          c.rollback
      }


  }

  def managerExists(kg: Long, classId: Long, employee: Employee) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from privilege where school_id={kg} " +
        "and subordinate=cast({class_id} as char(10)) and status=1 and employee_id={id}")
        .on('kg -> kg, 'class_id -> classId, 'id -> employee.id).as(get[Long]("count(1)") single) > 0
  }

  def createClassManagers(kg: Long, classId: Long, employee: Employee) = DB.withConnection {
    implicit c =>
      SQL("insert into privilege (school_id, employee_id, `group`, subordinate, promoter, update_at) " +
        "values ({kg}, {id}, 'teacher',{class_id}, '', {time})")
        .on('kg -> kg.toString, 'id -> employee.id, 'class_id -> classId.toString, 'time -> System.currentTimeMillis)
        .executeInsert()
  }

  def getClassManagers(kg: Long, classId: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from employeeinfo where employee_id in " +
        "(select employee_id from privilege p, classinfo c " +
        "where p.school_id=c.school_id and p.school_id ={kg} " +
        "and p.subordinate=cast(c.class_id as char(10)) and c.class_id={class_id} and c.status=1 and p.status=1)")
        .on(
          'kg -> kg.toString,
          'class_id -> classId
        ).as(Employee.simple *)
  }

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

  def findClass(kg: Long, id: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL("select * from classinfo where " +
        " school_id = {school_id} and class_id={class_id}")
        .on('school_id -> kg.toString,
          'class_id -> id).as(simple singleOpt)
  }

  def updateManager(clazz: SchoolClass) = DB.withConnection {
    implicit c =>
      Logger.info(clazz.toString)
      SQL("delete from privilege where school_id={kg} and subordinate={class}")
        .on('kg -> clazz.school_id.toString, 'class -> clazz.class_id.getOrElse(-1).toString).execute()
      clazz.managers.getOrElse(List()) map {
        manager =>
          Logger.info(Employee.findByName(clazz.school_id, manager).toString)
          SQL("insert into privilege (school_id, employee_id, `group`, subordinate, promoter, update_at) values " +
            "({kg},{id},{group},{subordinate},{promoter}, {time})").on(
              'kg -> clazz.school_id.toString,
              'id -> Employee.findByName(clazz.school_id, manager).get.id,
              'group -> "teacher",
              'promoter -> "admin",
              'subordinate -> clazz.class_id.getOrElse("").toString,
              'time -> System.currentTimeMillis
            ).executeInsert()
      }

  }

  def updateOrCreate(clazz: SchoolClass): Option[SchoolClass] = DB.withConnection {
    implicit c =>
      val exist = SQL("select count(1) from classinfo where school_id={kg} and class_id={id}").on(
        'kg -> clazz.school_id,
        'id -> clazz.class_id.getOrElse(-1)
      ).as(get[Long]("count(1)") single)

      Logger.info("exist is %s".format(exist))
      updateManager(clazz)
      exist match {
        case 0 =>
          createClass(clazz.school_id, clazz)
        case _ =>
          update(clazz)
          findClass(clazz.school_id, clazz.class_id)
      }

  }

  def update(clazz: SchoolClass) = DB.withConnection {
    implicit c =>
      SQL("update classinfo set class_name={name}, update_at={time} " +
        "where school_id={school_id} and class_id={class_id}")
        .on('school_id -> clazz.school_id.toString,
          'class_id -> clazz.class_id,
          'name -> clazz.name,
          'time -> System.currentTimeMillis).executeUpdate()

  }

  def generateClassId(kg: Long): Int = DB.withConnection {
    implicit c =>
      SQL("select max(class_id) as max from classinfo where school_id = {school_id}")
        .on('school_id -> kg.toString).as(get[Int]("max") single) + 3
  }


  def createClass(kg: Long, classInfo: SchoolClass) = DB.withConnection {
    implicit c =>
      val time = System.currentTimeMillis
      val insert = SQL("insert into classinfo (school_id, class_id, class_name, update_at) " +
        "values ({kg}, {class_id}, {name}, {time})")
        .on('kg -> kg.toString,
          'class_id -> classInfo.class_id.getOrElse(generateClassId(kg)),
          'name -> classInfo.name,
          'time -> time).executeInsert()
      SQL("select * from classinfo where uid={uid}")
        .on('uid -> insert).as(simple singleOpt)
  }

  val simple = {
    get[Int]("class_id") ~
      get[String]("school_id") ~
      get[String]("class_name") map {
      case id ~ school_id ~ name =>
        SchoolClass(school_id.toLong, Some(id), name)
    }
  }

  def allClasses(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select c.* from classinfo c, schoolinfo s " +
        "where s.school_id = c.school_id and c.school_id={kg} and c.status=1")
        .on('kg -> kg.toString)
        .as(simple *)
  }

}