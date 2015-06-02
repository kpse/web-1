package models.V3

import java.util.Date

import anorm.SqlParser._
import anorm._
import models.{Children, ChildInfo}
import models.Children._
import models.helper.TimeHelper.any2DateTime
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class StudentExt(display_name: Option[String], former_name: Option[String], student_id: Option[String], social_id: Option[String], residence_place: Option[String],
                      residence_type: Option[String], nationality: Option[String], original_place: Option[String], ethnos: Option[String],
                      student_type: Option[Int], inDate: Option[String], interest: Option[String], bed_number: Option[String], memo: Option[String],
                      bus_status: Option[Int], medical_history: Option[String])

case class Student(id: Option[Long], basic: ChildInfo, ext: Option[StudentExt])

object Student {
  implicit val writeStudentExt = Json.writes[StudentExt]
  implicit val readStudentExt = Json.reads[StudentExt]
  implicit val writeStudent = Json.writes[Student]
  implicit val readStudent = Json.reads[Student]

  def generateSpan(from: Option[Long], to: Option[Long], most: Option[Int]): String = {
    var result = ""
    from foreach { _ => result = " and c.uid > {from} " }
    to foreach { _ => result = s"$result and c.uid <= {to} " }
    s"$result limit ${most.getOrElse(25)}"
  }

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      val children: List[Student] = SQL(s"select * from childinfo c, classinfo c2 where c.class_id=c2.class_id and c.school_id=c2.school_id and c.school_id={kg} and c.status=1 ${generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to
        ).as(simple *)
      children map (c => c.copy(ext = extend(c.id.get)))
  }

  def show(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      val child: Option[Student] = SQL(s"select * from childinfo c, classinfo c2 where c.class_id=c2.class_id and c.school_id=c2.school_id and c.school_id={kg} and c.status=1 and c.uid={id}")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple singleOpt)
      child map (c => c.copy(ext = extend(c.id.get)))
  }

  def extend(id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from studentext s where s.base_id = {id}")
        .on(
          'id -> id
        ).as(simpleExt singleOpt)
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
        val info = ChildInfo(Some(childId), childName, nick, childBirthday.toDateOnly, childGender.toInt,
          Some(icon_url.getOrElse("")), classId, Some(className), Some(t), Some(schoolId.toLong), address, Some(status), Some(created))
        Student(Some(id), info, None)
    }
  }

  val simpleExt = {
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
      case display ~ former ~ studentId ~ socialId ~ resiPlace ~ resiType ~ nationality ~ originalPlace ~ ethnos ~ studentType
        ~ inDate ~ bed ~ interest ~ memo ~ bus ~ medical =>
        StudentExt(display, former, studentId, socialId, resiPlace, resiType, nationality,
          originalPlace, ethnos, studentType, Some(inDate.getOrElse(0).toDateOnly), interest, bed, memo, bus, medical)
    }
  }

}
