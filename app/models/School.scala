package models

import anorm.SqlParser._
import anorm.{~, _}
import controllers.V3.{RelativeController, StudentController}
import models.V7.IMToken
import play.api.Logger
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json

case class School(school_id: Long, name: String)

case class SchoolClass(school_id: Long, class_id: Option[Int], name: String, managers: Option[List[String]] = None, status: Option[Int] = None, updated_at: Option[Long] = None) {
  def imInfo = DB.withConnection {
    implicit c =>
      SQL("select * from im_class_group " +
        " where school_id={kg} and class_id={class_id} and status=1")
        .on('kg -> school_id, 'class_id -> class_id).as(IMToken.simpleClassGroup singleOpt)
  }
}


object School {
  implicit val schoolClassWriter = Json.writes[SchoolClass]
  implicit val schoolClassReader = Json.reads[SchoolClass]

  private val logger: Logger = Logger(classOf[School])

  def classNameExists(clazz: SchoolClass) = DB.withConnection {
    implicit c =>
      clazz.class_id match {
        case Some(x) =>
          SQL("select count(1) from classinfo where school_id={kg} and class_id <> {id} and class_name={name} and status=1").on(
            'kg -> clazz.school_id,
            'id -> x,
            'name -> clazz.name
          ).as(get[Long]("count(1)") single) > 0
        case None =>
          SQL("select count(1) from classinfo where school_id={kg} and class_name={name}  and status=1").on(
            'kg -> clazz.school_id,
            'name -> clazz.name
          ).as(get[Long]("count(1)") single) > 0
      }

  }

  def manageMoreClass(kg: Long, classId: Long, employee: Employee) = DB.withConnection {
    implicit c =>
      SQL("update privilege set subordinate= CONCAT((select subordinate from privilege where employee_id={id}) , {class_id} ) where school_id={kg} " +
        "and status=1 and employee_id={id}")
        .on('kg -> kg.toString, 'class_id -> ",%d".format(classId), 'id -> employee.id).as(get[Long]("count(1)") single) > 0
  }

  def alreadyAManagerInSchool(kg: Long, employee: Employee) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from privilege where school_id={kg} " +
        "and status=1 and employee_id={id}")
        .on('kg -> kg.toString, 'id -> employee.id).as(get[Long]("count(1)") single) > 0
  }

  def deleteSchool(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("delete from schoolInfo where school_id={kg}")
        .on('kg -> kg.toString).execute()
  }

  def cleanSchoolData(kg: Long) = DB.withConnection {
    implicit c =>
      List("employeeinfo", "privilege", "chargeinfo", "macwhitelist", "videomembers", "videoprovidertoken",
        "advertisement", "schoolconfig", "buslocation", "childrenonbus", "childrenbusplan", "schoolbus", "agentschool",
        "agentcontractorinschool", "agentactivityenrollment", "agentactivityinschool").map {
        table =>
          SQL("delete from " + table + " where school_id={kg}")
            .on('kg -> kg.toString).execute()
      }
  }

  def delete(kg: Long) = DB.withTransaction {
    implicit c =>

      try {
        cleanSchoolClassesData(kg)
        cleanSchoolData(kg)
        deleteSchool(kg)
        c.commit()
      }
      catch {
        case t: Throwable =>
          logger.warn("error %s".format(t.toString))
          c.rollback()
      }
  }

  def cleanSchoolClassesData(kg: Long) = DB.withTransaction {
    implicit c =>
      clearCurrentCache(kg)
      try {
        SQL("delete from accountinfo where accountid in (select phone from parentinfo where school_id={kg})")
          .on('kg -> kg.toString).execute()
        SQL("delete from relationmap where child_id in (select child_id from childinfo where school_id={kg})")
          .on('kg -> kg.toString).execute()
        SQL("delete from relationmap where parent_id in (select parent_id from parentinfo where school_id={kg})")
          .on('kg -> kg.toString).execute()

        SQL("delete from privilege where school_id={kg} and `group`='teacher'")
          .on('kg -> kg.toString).execute()

        List("classinfo", "childinfo", "parentinfo", "news",
          "scheduleinfo", "cookbookinfo", "dailylog", "conversation",
          "assignment", "assess", "sessionlog", "sessionread").map {
          table =>
            SQL("delete from " + table + " where school_id={kg}")
              .on('kg -> kg.toString).execute()
        }
        c.commit()
      }
      catch {
        case t: Throwable =>
          logger.warn("error %s".format(t.toString))
          c.rollback()
      }
  }

  def managerExists(kg: Long, classId: Long, employee: Employee) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from privilege where school_id={kg} " +
        "and subordinate=cast({class_id} as char(10)) and status=1 and employee_id={id}")
        .on('kg -> kg.toString, 'class_id -> classId, 'id -> employee.id).as(get[Long]("count(1)") single) > 0
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
        "(select distinct employee_id from privilege p, classinfo c " +
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

  def removedClasses(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from classinfo where status=0 and school_id = {kg}")
        .on(
          'kg -> kg.toString
        ).as(simple *)
  }

  def findClass(kg: Long, id: Option[Int]) : Option[SchoolClass] = DB.withConnection {
    implicit c =>
      SQL("select * from classinfo where " +
        " school_id = {school_id} and class_id={class_id}")
        .on('school_id -> kg.toString,
          'class_id -> id).as(simple singleOpt)
  }

  def updateManager(clazz: SchoolClass) = DB.withConnection {
    implicit c =>
      if (clazz.managers.nonEmpty) {
        SQL("delete from privilege where school_id={kg} and subordinate={class}")
          .on('kg -> clazz.school_id.toString, 'class -> clazz.class_id.getOrElse(-1).toString).execute()
      }
      clazz.managers.getOrElse(List()) map {
        manager =>
          logger.info(Employee.findByName(clazz.school_id, manager).toString)
          SQL("insert into privilege (school_id, employee_id, `group`, subordinate, promoter, update_at) values " +
            "({kg},{id},{group},{subordinate},{promoter}, {time})").on(
              'kg -> clazz.school_id.toString,
              'id -> Employee.findByName(clazz.school_id, manager).get.id,
              'group -> "teacher",
              'promoter -> "admin",
              'subordinate -> clazz.class_id.getOrElse(0).toString,
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

      logger.info("exist is %s".format(exist))
      exist match {
        case 0 =>
          createClass(clazz.school_id, clazz)
        case _ =>
          updateManager(clazz)
          update(clazz)
          findClass(clazz.school_id, clazz.class_id)
      }

  }

  def update(clazz: SchoolClass) = DB.withConnection {
    implicit c =>
      SQL("update classinfo set class_name={name}, update_at={time}, status=1 " +
        "where school_id={school_id} and class_id={class_id}")
        .on('school_id -> clazz.school_id.toString,
          'class_id -> clazz.class_id,
          'name -> clazz.name,
          'time -> System.currentTimeMillis).executeUpdate()

  }

  def generateClassId(kg: Long): Int = DB.withConnection {
    implicit c =>
      val maybeMaybeInt1: Option[Option[Int]] = SQL("select max(class_id) as max from classinfo where school_id = {school_id}")
        .on('school_id -> kg.toString).as(get[Option[Int]]("max") singleOpt)
      maybeMaybeInt1.getOrElse(Some(999)).getOrElse(999) + 1
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
      get[String]("class_name") ~
      get[Long]("update_at") ~
      get[Int]("status") map {
      case id ~ school_id ~ name ~ updatedAt ~ status =>
        SchoolClass(school_id.toLong, Some(id), name, None, Some(status), Some(updatedAt))
    }
  }

  def allClasses(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select c.* from classinfo c, schoolinfo s " +
        "where s.school_id = c.school_id and c.school_id={kg} and c.status=1")
        .on('kg -> kg.toString)
        .as(simple *)
  }

  def clearCurrentCache(kg: Long) = {
    RelativeController.clearCurrentCache(kg)
    StudentController.clearCurrentCache(kg)
  }

}