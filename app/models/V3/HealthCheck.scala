package models.V3

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json

case class HeadCheckResult(head_eye_left: Option[String], head_eye_right: Option[String], head_eye_trachoma: Option[String],
                           head_eye_conjunctivitis: Option[String], head_ear_leftear: Option[String], head_ear_rightear: Option[String], head_tooth_qty: Option[String],
                           head_tooth_caries: Option[String], head_tooth_periodontal: Option[String])

case class SurgeryCheckResult(surgery_headcircumference: Option[String], surgery_chestcircumference: Option[String],
                              surgery_limbs: Option[String], surgery_spine: Option[String], surgery_skin: Option[String], surgery_lymphaden: Option[String])

case class MedicineCheckResult(medicine_lefttonsil: Option[String], medicine_righttonsil: Option[String], medicine_bloodpressure: Option[String], medicine_bloodtype: Option[String],
                               medicine_bloodtypename: Option[String], medicine_hemoglobin: Option[String], medicine_heart: Option[String], medicine_liver: Option[String],
                               medicine_spleen: Option[String], medicine_lung: Option[String])

case class OtherCheckResult(other_rickets_chongmen: Option[String], other_rickets_fangtou: Option[String],
                            other_rickets_pingpongtou: Option[String], other_rickets_zhentu: Option[String], other_rickets_jixiong: Option[String], other_rickets_lechuanzhu: Option[String],
                            other_rickets_haoshigou: Option[String], other_rickets_yijingduohan: Option[String], other_rickets_xo: Option[String], other_infantileparalysis: Option[String],
                            other_ascarid: Option[String], other_skins: Option[String], other_deformity: Option[String], other_psychosis: Option[String],
                            other_hernia: Option[String], other_others: Option[String])

case class StudentHealthCheckDetail(id: Option[Long], school_id: Option[Long], record_name: Option[String], recorded_at: Option[Long], student_id: Option[Long], basic_height: Option[String],
                                    basic_weight: Option[String], head: Option[HeadCheckResult], surgery: Option[SurgeryCheckResult], medicine: Option[MedicineCheckResult],
                                    other: Option[OtherCheckResult], memo: Option[String]) {
  def create(kg: Long, studentId: Long): Option[StudentHealthCheckDetail] = DB.withConnection {
    implicit c =>
      val headData: HeadCheckResult = head.getOrElse(HealthCheck.emptyHead)
      val surgeryData: SurgeryCheckResult = surgery.getOrElse(HealthCheck.emptySurgery)
      val medicineData: MedicineCheckResult = medicine.getOrElse(HealthCheck.emptyMedicine)
      val otherData: OtherCheckResult = other.getOrElse(HealthCheck.emptyOther)
      val insert: Option[Long] = SQL("insert into healthcheckdetail (school_id, record_name, recorded_at, student_id, " +
        "basic_height, basic_weight, head_eye_left, head_eye_right, head_eye_trachoma, head_eye_conjunctivitis," +
        "head_ear_leftear, head_ear_rightear, head_tooth_caries, head_tooth_qty, head_tooth_periodontal, " +
        "surgery_headcircumference, surgery_chestcircumference, surgery_limbs, surgery_spine, surgery_skin, surgery_lymphaden, " +
        "medicine_lefttonsil, medicine_righttonsil, medicine_bloodpressure, medicine_bloodtype, medicine_bloodtypename, " +
        "medicine_hemoglobin, medicine_heart, medicine_liver, medicine_spleen, medicine_lung, other_rickets_chongmen, " +
        "other_rickets_fangtou, other_rickets_pingpongtou, other_rickets_zhentu, other_rickets_jixiong, " +
        "other_rickets_lechuanzhu, other_rickets_haoshigou,other_rickets_yijingduohan, other_rickets_xo, " +
        "other_infantileparalysis, other_ascarid, other_skins, other_deformity, other_psychosis, other_hernia, other_others, memo) VALUES " +
        "({school_id}, {record_name}, {recorded_at}, {student_id}, {basic_height}, {basic_weight}, {head_eye_left}, " +
        "{head_eye_right}, {head_eye_trachoma}, {head_eye_conjunctivitis}, {head_ear_leftear}, {head_ear_rightear}, " +
        "{head_tooth_caries}, {head_tooth_qty}, {head_tooth_periodontal}, {surgery_headcircumference}, {surgery_chestcircumference}, " +
        "{surgery_limbs}, {surgery_spine}, {surgery_skin}, {surgery_lymphaden}, {medicine_lefttonsil}, {medicine_righttonsil}, " +
        "{medicine_bloodpressure}, {medicine_bloodtype}, {medicine_bloodtypename}, {medicine_hemoglobin}, {medicine_heart}, " +
        "{medicine_liver}, {medicine_spleen}, {medicine_lung}, {other_rickets_chongmen}, {other_rickets_fangtou}, " +
        "{other_rickets_pingpongtou}, {other_rickets_zhentu}, {other_rickets_jixiong}, {other_rickets_lechuanzhu}, " +
        "{other_rickets_haoshigou}, {other_rickets_yijingduohan}, {other_rickets_xo}, {other_infantileparalysis}, " +
        "{other_ascarid}, {other_skins}, {other_deformity}, {other_psychosis}, {other_hernia}, {other_others}, {memo})")
        .on(
          'school_id -> kg,
          'record_name -> record_name,
          'recorded_at -> recorded_at,
          'student_id -> studentId,
          'basic_height -> basic_height,
          'basic_weight -> basic_weight,
          'head_eye_left -> headData.head_eye_left,
          'head_eye_right -> headData.head_eye_right,
          'head_eye_trachoma -> headData.head_eye_trachoma,
          'head_eye_conjunctivitis -> headData.head_eye_conjunctivitis,
          'head_ear_leftear -> headData.head_ear_leftear,
          'head_ear_rightear -> headData.head_ear_rightear,
          'head_tooth_caries -> headData.head_tooth_caries,
          'head_tooth_qty -> headData.head_tooth_qty,
          'head_tooth_periodontal -> headData.head_tooth_periodontal,
          'surgery_headcircumference -> surgeryData.surgery_headcircumference,
          'surgery_chestcircumference -> surgeryData.surgery_chestcircumference,
          'surgery_limbs -> surgeryData.surgery_limbs,
          'surgery_spine -> surgeryData.surgery_spine,
          'surgery_skin -> surgeryData.surgery_skin,
          'surgery_lymphaden -> surgeryData.surgery_lymphaden,
          'medicine_lefttonsil -> medicineData.medicine_lefttonsil,
          'medicine_righttonsil -> medicineData.medicine_righttonsil,
          'medicine_bloodpressure -> medicineData.medicine_bloodpressure,
          'medicine_bloodtype -> medicineData.medicine_bloodtype,
          'medicine_bloodtypename -> medicineData.medicine_bloodtypename,
          'medicine_hemoglobin -> medicineData.medicine_hemoglobin,
          'medicine_heart -> medicineData.medicine_heart,
          'medicine_liver -> medicineData.medicine_liver,
          'medicine_spleen -> medicineData.medicine_spleen,
          'medicine_lung -> medicineData.medicine_lung,
          'other_rickets_chongmen -> otherData.other_rickets_chongmen,
          'other_rickets_fangtou -> otherData.other_rickets_fangtou,
          'other_rickets_pingpongtou -> otherData.other_rickets_pingpongtou,
          'other_rickets_zhentu -> otherData.other_rickets_zhentu,
          'other_rickets_jixiong -> otherData.other_rickets_jixiong,
          'other_rickets_lechuanzhu -> otherData.other_rickets_lechuanzhu,
          'other_rickets_haoshigou -> otherData.other_rickets_haoshigou,
          'other_rickets_yijingduohan -> otherData.other_rickets_yijingduohan,
          'other_rickets_xo -> otherData.other_rickets_xo,
          'other_infantileparalysis -> otherData.other_infantileparalysis,
          'other_ascarid -> otherData.other_ascarid,
          'other_skins -> otherData.other_skins,
          'other_deformity -> otherData.other_deformity,
          'other_psychosis -> otherData.other_psychosis,
          'other_hernia -> otherData.other_hernia,
          'other_others -> otherData.other_others,
          'memo -> memo
        ).executeInsert()
      HealthCheck.show(kg, studentId, insert.getOrElse(0))
  }
}

object HealthCheck {
  implicit val writeHeadCheckResult = Json.writes[HeadCheckResult]
  implicit val readHeadCheckResult = Json.reads[HeadCheckResult]

  implicit val writeSurgeryCheckResult = Json.writes[SurgeryCheckResult]
  implicit val readSurgeryCheckResult = Json.reads[SurgeryCheckResult]

  implicit val writeMedicineCheckResult = Json.writes[MedicineCheckResult]
  implicit val readMedicineCheckResult = Json.reads[MedicineCheckResult]

  implicit val writeOtherCheckResult = Json.writes[OtherCheckResult]
  implicit val readOtherCheckResult = Json.reads[OtherCheckResult]

  implicit val writeStudentHealthCheckDetail = Json.writes[StudentHealthCheckDetail]
  implicit val readStudentHealthCheckDetail = Json.reads[StudentHealthCheckDetail]


  def index(kg: Long, studentId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from healthcheckdetail where school_id={kg} and status=1 and student_id={sid} ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'sid -> studentId,
          'from -> from,
          'to -> to
        ).as(simple *)
  }

  def show(kg: Long, studentId: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from healthcheckdetail where school_id={kg} and uid={id} and status=1 and student_id={sid}")
        .on(
          'kg -> kg.toString,
          'sid -> studentId,
          'id -> id
        ).as(simple singleOpt)
  }

  def deleteById(kg: Long, studentId: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update healthcheckdetail set status=0 where uid={id} and school_id={kg} and status=1 and student_id={sid}")
        .on(
          'kg -> kg.toString,
          'sid -> studentId,
          'id -> id
        ).executeUpdate()
  }

  val simple = {
    get[Long]("uid") ~
      get[String]("school_id") ~
      get[Option[String]]("record_name") ~
      get[Option[Long]]("recorded_at") ~
      get[Option[Long]]("student_id") ~
      get[Option[String]]("basic_height") ~
      get[Option[String]]("basic_weight") ~
      get[Option[String]]("head_eye_left") ~
      get[Option[String]]("head_eye_right") ~
      get[Option[String]]("head_eye_trachoma") ~
      get[Option[String]]("head_eye_conjunctivitis") ~
      get[Option[String]]("head_ear_leftear") ~
      get[Option[String]]("head_ear_rightear") ~
      get[Option[String]]("head_tooth_caries") ~
      get[Option[String]]("head_tooth_qty") ~
      get[Option[String]]("head_tooth_periodontal") ~
      get[Option[String]]("surgery_headcircumference") ~
      get[Option[String]]("surgery_chestcircumference") ~
      get[Option[String]]("surgery_limbs") ~
      get[Option[String]]("surgery_spine") ~
      get[Option[String]]("surgery_skin") ~
      get[Option[String]]("surgery_lymphaden") ~
      get[Option[String]]("medicine_lefttonsil") ~
      get[Option[String]]("medicine_righttonsil") ~
      get[Option[String]]("medicine_bloodpressure") ~
      get[Option[String]]("medicine_bloodtype") ~
      get[Option[String]]("medicine_bloodtypename") ~
      get[Option[String]]("medicine_hemoglobin") ~
      get[Option[String]]("medicine_heart") ~
      get[Option[String]]("medicine_liver") ~
      get[Option[String]]("medicine_spleen") ~
      get[Option[String]]("medicine_lung") ~
      get[Option[String]]("other_rickets_chongmen") ~
      get[Option[String]]("other_rickets_fangtou") ~
      get[Option[String]]("other_rickets_pingpongtou") ~
      get[Option[String]]("other_rickets_zhentu") ~
      get[Option[String]]("other_rickets_jixiong") ~
      get[Option[String]]("other_rickets_lechuanzhu") ~
      get[Option[String]]("other_rickets_haoshigou") ~
      get[Option[String]]("other_rickets_yijingduohan") ~
      get[Option[String]]("other_rickets_xo") ~
      get[Option[String]]("other_infantileparalysis") ~
      get[Option[String]]("other_ascarid") ~
      get[Option[String]]("other_skins") ~
      get[Option[String]]("other_deformity") ~
      get[Option[String]]("other_psychosis") ~
      get[Option[String]]("other_hernia") ~
      get[Option[String]]("other_others") ~
      get[Option[String]]("memo") map {
      case id ~ school_id ~ record_name ~ recorded_at ~ student_id ~ basic_height ~ basic_weight ~ head_eye_left ~
        head_eye_right ~ head_eye_trachoma ~ head_eye_conjunctivitis ~ head_ear_leftear ~ head_ear_rightear ~
        head_tooth_caries ~ head_tooth_qty ~ head_tooth_periodontal ~ surgery_headcircumference ~ surgery_chestcircumference ~
        surgery_limbs ~ surgery_spine ~ surgery_skin ~ surgery_lymphaden ~ medicine_lefttonsil ~ medicine_righttonsil ~
        medicine_bloodpressure ~ medicine_bloodtype ~ medicine_bloodtypename ~ medicine_hemoglobin ~ medicine_heart ~
        medicine_liver ~ medicine_spleen ~ medicine_lung ~ other_rickets_chongmen ~ other_rickets_fangtou ~ other_rickets_pingpongtou ~
        other_rickets_zhentu ~ other_rickets_jixiong ~ other_rickets_lechuanzhu ~ other_rickets_haoshigou ~ other_rickets_yijingduohan ~
        other_rickets_xo ~ other_infantileparalysis ~ other_ascarid ~ other_skins ~ other_deformity ~ other_psychosis ~ other_hernia ~ other_others ~ memo =>
        StudentHealthCheckDetail(Some(id), Some(school_id.toLong), record_name, recorded_at, student_id, basic_height, basic_weight,
          Some(HeadCheckResult(head_eye_left, head_eye_right, head_eye_trachoma, head_eye_conjunctivitis, head_ear_leftear, head_ear_rightear,
            head_tooth_qty, head_tooth_caries, head_tooth_periodontal)),
          Some(SurgeryCheckResult(surgery_headcircumference, surgery_chestcircumference,
            surgery_limbs, surgery_spine, surgery_skin, surgery_lymphaden)),
          Some(MedicineCheckResult(medicine_lefttonsil, medicine_righttonsil,
            medicine_bloodpressure, medicine_bloodtype, medicine_bloodtypename, medicine_hemoglobin, medicine_heart,
            medicine_liver, medicine_spleen, medicine_lung)),
          Some(OtherCheckResult(other_rickets_chongmen, other_rickets_fangtou,
            other_rickets_pingpongtou, other_rickets_zhentu, other_rickets_jixiong, other_rickets_lechuanzhu,
            other_rickets_haoshigou, other_rickets_yijingduohan, other_rickets_xo, other_infantileparalysis,
            other_ascarid, other_skins, other_deformity, other_psychosis, other_hernia, other_others)), memo)
    }
  }

  val emptyHead = HeadCheckResult(None, None, None, None, None, None, None, None, None)
  val emptySurgery = SurgeryCheckResult(None, None, None, None, None, None)
  val emptyMedicine = MedicineCheckResult(None, None, None, None, None, None, None, None, None, None)
  val emptyOther = OtherCheckResult(None, None, None, None, None, None, None, None, None, None, None, None, None, None, None, None)

}

