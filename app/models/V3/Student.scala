package models.V3

import java.sql.Connection
import java.util.Date

import anorm.SqlParser._
import anorm._
import models.json_models.CheckNotification
import models.{Children, ChildInfo}
import models.Children._
import models.helper.TimeHelper.any2DateTime
import models.helper.TimeHelper.parseShortDate
import org.joda.time.DateTime
import play.Logger
import play.api.cache.Cache
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class StudentExt(display_name: Option[String], former_name: Option[String], student_id: Option[String], social_id: Option[String], residence_place: Option[String],
                      residence_type: Option[String], nationality: Option[String], original_place: Option[String], ethnos: Option[String],
                      student_type: Option[Int], in_date: Option[String], interest: Option[String], bed_number: Option[String], memo: Option[String],
                      bus_status: Option[Int], medical_history: Option[String], base_id: Option[Long] = None, id: Option[Long] = None) {
  def extExists(id: Long) = DB.withTransaction {
    implicit c =>
      SQL("select count(1) from studentext where base_id={base_id}")
        .on(
          'base_id -> id
        ).as(get[Long]("count(1)") single) > 0
  }

  def handleExt(id: Long) = extExists(id) match {
    case true =>
      update(id)
    case false =>
      create(id)
  }

  def update(id: Long) = DB.withTransaction {
    implicit c =>
      SQL("update studentext set display_name={display}, former_name={former_name}, student_id={student_id}, social_id={social_id}, " +
        "residence_place={residence_place}, residence_type={residence_type}, nationality={nationality}, " +
        "original_place={original_place}, ethnos={ethnos}, student_type={student_type}, in_date={in_date}, interest={interest}, " +
        "bed_number={bed_number}, memo={memo}, bus_status={bus_status}, medical_history={medical_history} " +
        " where base_id={base_id}")
        .on(
          'base_id -> id,
          'display -> display_name,
          'former_name -> former_name,
          'student_id -> student_id,
          'social_id -> social_id,
          'residence_place -> residence_place,
          'residence_type -> residence_type,
          'nationality -> nationality,
          'original_place -> original_place,
          'ethnos -> ethnos,
          'student_type -> student_type,
          'in_date -> parseShortDate(in_date.getOrElse("1970-01-01")).toDate.getTime,
          'interest -> interest,
          'bed_number -> bed_number,
          'memo -> memo,
          'bus_status -> bus_status,
          'medical_history -> medical_history
        ).executeUpdate()
  }

  def create(id: Long) = DB.withTransaction {
    implicit c =>
      SQL("insert into studentext (base_id, display_name, former_name, student_id, social_id, residence_place, residence_type, nationality, " +
        "original_place, ethnos, student_type, in_date, interest, bed_number, memo, bus_status, medical_history) values (" +
        "{base_id}, {display}, {former_name}, {student_id}, {social_id}, {residence_place}, {residence_type}, {nationality}, " +
        "{original_place}, {ethnos}, {student_type}, {in_date}, {interest}, {bed_number}, {memo}, {bus_status}, {medical_history})")
        .on(
          'base_id -> id,
          'display -> display_name,
          'former_name -> former_name,
          'student_id -> student_id,
          'social_id -> social_id,
          'residence_place -> residence_place,
          'residence_type -> residence_type,
          'nationality -> nationality,
          'original_place -> original_place,
          'ethnos -> ethnos,
          'student_type -> student_type,
          'in_date -> parseShortDate(in_date.getOrElse("1970-01-01")).toDate.getTime,
          'interest -> interest,
          'bed_number -> bed_number,
          'memo -> memo,
          'bus_status -> bus_status,
          'medical_history -> medical_history
        ).executeInsert()
  }
}

case class Student(id: Option[Long], basic: ChildInfo, ext: Option[StudentExt], check_status: Option[String] = Some("out")) {
  def checkCachedStatus: Student = {
    val cacheKey: Option[String] = for {kg <- basic.school_id
                                        id <- basic.child_id} yield s"dailylog_${kg}_${id}"

    val maybeCheckNotifications: Option[List[CheckNotification]] = Cache.getAs[List[CheckNotification]](cacheKey.getOrElse("wrong_key"))
    Logger.debug(s"maybeCheckNotifications = ${cacheKey} $maybeCheckNotifications")
    maybeCheckNotifications match {
      case Some(notifications) if notifications.dropWhile(_.timestamp <= DateTime.now().withTimeAtStartOfDay().getMillis).count(List(0, 1, 11, 12) contains _.notice_type) % 2 != 0 =>
        copy(check_status = Some("in"))
      case _ =>
        copy(check_status = Some("out"))
    }
  }

  def checkStatus: Student = DB.withConnection {
    implicit c =>
      val evenSwiping: Boolean = SQL(s"select count(1) from dailylog " +
        s"where child_id={child_id} and check_at > {begin} and check_at < {end} and notice_type in (0, 1, 11, 12)")
        .on(
          'child_id -> basic.child_id,
          'begin -> DateTime.now.withHourOfDay(0).getMillis,
          'end -> DateTime.now.plusDays(1).withHourOfDay(0).getMillis
        ).as(get[Long]("count(1)") single) % 2 == 0
      Logger.debug(s"Student id = ${id} evenSwiping = ${evenSwiping}")
      evenSwiping match {
        case false => copy(check_status = Some("in"))
        case true => this
      }
  }

  def update: Option[Student] = DB.withTransaction {
    implicit c =>
      try {
        val timestamp = System.currentTimeMillis
        val kg: Long = basic.school_id.getOrElse(0)
        val childId = basic.child_id.getOrElse("2_%d_%d".format(kg, timestamp % 100000))
        SQL("update childinfo set name={name}, child_id={child_id}, student_id={student_id}, " +
          "gender={gender}, classname={classname}, picurl={picurl}, birthday={birthday}, " +
          "indate={indate}, school_id={school_id}, address={address}, stu_type={stu_type}, hukou={hukou}, " +
          "social_id={social_id}, nick={nick}, status=1, update_at={timestamp}, class_id={class_id} " +
          " where uid={id}")
          .on(
            'id -> id,
            'name -> basic.name,
            'child_id -> childId,
            'student_id -> s"$timestamp".take(5),
            'gender -> basic.gender,
            'classname -> "",
            'picurl -> basic.portrait.getOrElse(""),
            'birthday -> basic.birthday,
            'indate -> basic.birthday,
            'school_id -> basic.school_id.getOrElse(0),
            'address -> basic.address,
            'stu_type -> 2,
            'hukou -> 1,
            'social_id -> "social_id",
            'nick -> basic.nick,
            'status -> 1,
            'class_id -> basic.class_id,
            'timestamp -> timestamp).executeInsert()
        ext foreach (_.handleExt(id.get))
        c.commit()
        val info: Option[ChildInfo] = Children.findById(kg, id.get)
        Logger.info(info.toString)
        ext match {
          case Some(x) =>
            Some(Student(id, info.get, Some(x)))
          case None =>
            Some(Student(id, info.get, None))
        }
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }

  def create: Option[Student] = DB.withTransaction {
    implicit c =>
      try {
        val timestamp = System.currentTimeMillis
        val kg: Long = basic.school_id.getOrElse(0)
        val childId = basic.child_id.getOrElse("2_%d_%d".format(kg, timestamp % 100000))
        val childUid: Option[Long] = SQL("INSERT INTO childinfo(name, child_id, student_id, gender, classname, picurl, birthday, " +
          "indate, school_id, address, stu_type, hukou, social_id, nick, status, update_at, class_id, created_at) " +
          "VALUES ({name},{child_id},{student_id},{gender},{classname},{picurl},{birthday},{indate}," +
          "{school_id},{address},{stu_type},{hukou},{social_id},{nick},{status},{timestamp},{class_id},{created})")
          .on(
            'name -> basic.name,
            'child_id -> childId,
            'student_id -> s"$timestamp".take(5),
            'gender -> basic.gender,
            'classname -> "",
            'picurl -> basic.portrait.getOrElse(""),
            'birthday -> basic.birthday,
            'indate -> basic.birthday,
            'school_id -> basic.school_id.getOrElse(0),
            'address -> basic.address,
            'stu_type -> 2,
            'hukou -> 1,
            'social_id -> "social_id",
            'nick -> basic.nick,
            'status -> 1,
            'class_id -> basic.class_id,
            'timestamp -> timestamp,
            'created -> timestamp).executeInsert()
        ext foreach (_.handleExt(childUid.get))
        c.commit()
        val info: Option[ChildInfo] = Children.findById(kg, childUid.get)
        Logger.info(info.toString)
        ext match {
          case Some(x) =>
            Some(Student(childUid, info.get, Some(x)))
          case None =>
            Some(Student(childUid, info.get, None))
        }
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }
}

object Student {
  implicit val writeStudentExt = Json.writes[StudentExt]
  implicit val readStudentExt = Json.reads[StudentExt]
  implicit val writeStudent = Json.writes[Student]
  implicit val readStudent = Json.reads[Student]

  def generateSpan(from: Option[Long], to: Option[Long], most: Option[Int]): String = {
    var result = ""
    from foreach { _ => result = " and c.uid > {from} " }
    to foreach { _ => result = s"$result and c.uid < {to} " }
    s"$result order by c.uid DESC limit ${most.getOrElse(25)}"
  }

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      val children: List[Student] = SQL(s"select * from childinfo c, classinfo c2 where c.class_id=c2.class_id and c.school_id=c2.school_id and c.school_id={kg} and c.status=1 and c2.status=1 ${generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to
        ).as(simple *)
      children map (c => c.copy(ext = extend(c.id.get)))
  }

  def show(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      showWithoutCheckStatus(kg, id) map (_.checkStatus)
  }

  def showWithoutCheckStatus(kg: Long, id: Long)(implicit connection: Connection) = {
    val child: Option[Student] = SQL(s"select * from childinfo c, classinfo c2 where c.class_id=c2.class_id and " +
      s"c.school_id=c2.school_id and c.school_id={kg} and c.status=1 and c2.status=1 and c.uid={id}")
      .on(
        'kg -> kg.toString,
        'id -> id
      ).as(simple singleOpt)
    child map (c => c.copy(ext = extend(c.id.get)))
  }

  def showWithCachedStatus(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      showWithoutCheckStatus(kg, id) map (_.checkCachedStatus)
  }

  def extend(id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from studentext s where s.base_id = {id}")
        .on(
          'id -> id
        ).as(simpleExt singleOpt)
  }

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update childinfo set status=0 where uid={id} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
  }

  val simple = {
    get[Long]("childinfo.uid") ~
      get[String]("school_id") ~
      get[String]("child_id") ~
      get[String]("name") ~
      get[String]("nick") ~
      get[Option[String]]("picurl") ~
      get[Int]("gender") ~
      get[Date]("birthday") ~
      get[Int]("childinfo.class_id") ~
      get[String]("classinfo.class_name") ~
      get[Option[String]]("childinfo.address") ~
      get[Long]("childinfo.update_at") ~
      get[Long]("childinfo.created_at") ~
      get[Int]("childinfo.status") map {
      case id ~ schoolId ~ childId ~ childName ~ nick ~ icon_url ~ childGender
        ~ childBirthday ~ classId ~ className ~ address ~ t ~ created ~ status =>
        val info = ChildInfo(Some(childId), childName, nick, Some(childBirthday.toDateOnly), childGender.toInt,
          Some(icon_url.getOrElse("")), classId, Some(className), Some(t), Some(schoolId.toLong), address, Some(status), Some(created))
        Student(Some(id), info, None)
    }
  }

  val simpleExt = {
    get[Long]("uid") ~
      get[Long]("base_id") ~
      get[Option[String]]("display_name") ~
      get[Option[String]]("former_name") ~
      get[Option[String]]("student_id") ~
      get[Option[String]]("social_id") ~
      get[Option[String]]("residence_place") ~
      get[Option[String]]("residence_type") ~
      get[Option[String]]("nationality") ~
      get[Option[String]]("original_place") ~
      get[Option[String]]("ethnos") ~
      get[Option[Int]]("student_type") ~
      get[Option[Long]]("in_date") ~
      get[Option[String]]("interest") ~
      get[Option[String]]("bed_number") ~
      get[Option[String]]("memo") ~
      get[Option[Int]]("bus_status") ~
      get[Option[String]]("medical_history") map {
      case id ~ bId ~ display ~ former ~ studentId ~ socialId ~ resiPlace ~ resiType ~ nationality ~ originalPlace ~ ethnos ~ studentType
        ~ inDate ~ interest ~ bed ~ memo ~ bus ~ medical =>
        StudentExt(display, former, studentId, socialId, resiPlace, resiType, nationality,
          originalPlace, ethnos, studentType, Some(inDate.getOrElse(0).toDateOnly), interest, bed, memo, bus, medical, Some(id), Some(bId))
    }
  }

}
