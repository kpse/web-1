package controllers.V3

import controllers.Secured
import models.SuccessResponse
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller


case class MorningHealthCheck(id: Option[Long], recorder_id: Option[Long], writer_id: Option[Long], checked_at: Option[Long], memo: Option[String])

object MorningHealthCheckController extends Controller with Secured {

  implicit val writeMorningHealthCheck = Json.writes[MorningHealthCheck]
  implicit val readMorningHealthCheck = Json.reads[MorningHealthCheck]

  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(MorningHealthCheck(Some(1), Some(1), Some(1), Some(System.currentTimeMillis), Some("某次晨检")))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(MorningHealthCheck(Some(id), Some(1), Some(1), Some(System.currentTimeMillis), Some("某次晨检"))))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[MorningHealthCheck].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[MorningHealthCheck].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(new SuccessResponse()))
  }
}

/*
*
create table IMSInfo.dbo.MorningCheckRecord(
SysNO int identity(1,1),系统编号
Memo nvarchar(500),备注
RecordTime datetime,晨检时间
RecorderSysNO int,操作老师编号
WriterSysNO int填表老师编号
)
*/

case class StudentHealthCheck(id: Option[Long], record_id: Option[Long], student_id: Option[Long], temperature: Option[String],
                              check_status_id: Option[Long], check_status_name: Option[String], relative_words: Option[String],
                              memo: Option[String])

object StudentMorningHealthCheckController extends Controller with Secured {

  implicit val writeStudentHealthCheck = Json.writes[StudentHealthCheck]
  implicit val readStudentHealthCheck = Json.reads[StudentHealthCheck]

  def index(kg: Long, studentId: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(StudentHealthCheck(Some(1), Some(1), Some(studentId), Some("37.4C"), Some(1), Some("晨检结果名称"), Some("家长能说啥？"), Some("这次晨检的备忘")))))
  }

  def show(kg: Long, studentId: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(StudentHealthCheck(Some(id), Some(1), Some(studentId), Some("37.4C"), Some(1), Some("晨检结果名称"), Some("家长能说啥？"), Some("这次晨检的备忘"))))
  }

  def create(kg: Long, studentId: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[StudentHealthCheck].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, studentId: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[StudentHealthCheck].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, studentId: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(new SuccessResponse()))
  }
}

/*
create table IMSInfo.dbo.MorningCheckStudent(
SysNO int identity(1,1),系统编号
RecordSysNO int,晨检记录编号
StudentSysNO int,学生编号
Temperature varchar(20),温度
CheckStatusSysNO int,晨检结果编号
CheckStatusName nvarchar(20),晨检结果名称
RelativeWords nvarchar(500),家长嘱咐
Memo nvarchar(500)备注
)
 */

case class HeadCheckResult(head_eye_left: Option[String], head_eye_right: Option[String], head_eye_trachoma: Option[String],
                           head_eye_conjunctivitis: Option[String], head_ear_leftear: Option[String], head_ear_rightear: Option[String], head_tooth_qty: Option[String],
                           head_tooth_caries: Option[String], head_tooth_periodontal: Option[String])

case class SurgeryCheckResult(surgery_headcircumference: Option[String], surgery_chestcircumference: Option[String],
                              surgery_limbs: Option[String], surgery_spine: Option[String], surgery_skin: Option[String], surgery_lymphaden: Option[String])

case class MedicineCheckResult(medicine_lefttonsil: Option[String], medicine_righttonsil: Option[String], medicine_bloodpressure: Option[String], medicine_bloodtypesysno: Option[String],
                               medicine_bloodtypename: Option[String], medicine_hemoglobin: Option[String], medicine_heart: Option[String], medicine_liver: Option[String],
                               medicine_spleen: Option[String], medicine_lung: Option[String])

case class OtherCheckResult(other_rickets_chongmen: Option[String], other_rickets_fangtou: Option[String],
                            other_rickets_pingpongtou: Option[String], other_rickets_zhentu: Option[String], other_rickets_jixiong: Option[String], other_rickets_lechuanzhu: Option[String],
                            other_rickets_haoshigou: Option[String], other_rickets_yijingduohan: Option[String], other_rickets_xo: Option[String], other_infantileparalysis: Option[String],
                            other_ascarid: Option[String], other_skins: Option[String], other_deformity: Option[String], other_psychosis: Option[String],
                            other_hernia: Option[String], other_others: Option[String])

case class StudentHealthCheckDetail(id: Option[Long], record_name: Option[String], recorded_at: Option[Long], student_id: Option[Long], basic_height: Option[String],
                                    basic_weight: Option[String], head: Option[HeadCheckResult], surgery: Option[SurgeryCheckResult], medicine: Option[MedicineCheckResult],
                                    other: Option[OtherCheckResult], memo: Option[String])

object StudentHealthCheckController extends Controller with Secured {

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

  def index(kg: Long, studentId: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(StudentHealthCheckDetail(Some(1), Some("一次结果"), Some(System.currentTimeMillis), Some(studentId),
      Some("1.75cm"), Some("180kg"),
      Some(HeadCheckResult(Some("5.0"), Some("4.8"), Some("无"), Some("无"), Some("无"), Some("无"),
      Some("无"), Some("无"), Some("无"))),
        Some(SurgeryCheckResult(Some("无"), Some("无"), Some("无"), Some("无"), Some("无"), Some("无"))),
          Some(MedicineCheckResult(Some("无"),
      Some("无"), Some("无"), Some("无"), Some("无"), Some("无"), Some("无"), Some("无"), Some("无"), Some("无"))),
        Some(OtherCheckResult(Some("无"),
      Some("无"), Some("无"), Some("无"), Some("无"), Some("无"), Some("无"), Some("无"), Some("无"), Some("无"), Some("无"),
      Some("无"), Some("无"), Some("无"), Some("无"), Some("无"))), Some("备注")))))
  }

  def show(kg: Long, studentId: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(StudentHealthCheckDetail(Some(id), Some("一次结果"), Some(System.currentTimeMillis), Some(studentId),
      Some("1.75cm"), Some("180kg"),
      Some(HeadCheckResult(Some("5.0"), Some("4.8"), Some("无"), Some("无"), Some("无"), Some("无"),
        Some("无"), Some("无"), Some("无"))),
      Some(SurgeryCheckResult(Some("无"), Some("无"), Some("无"), Some("无"), Some("无"), Some("无"))),
      Some(MedicineCheckResult(Some("无"),
        Some("无"), Some("无"), Some("无"), Some("无"), Some("无"), Some("无"), Some("无"), Some("无"), Some("无"))),
      Some(OtherCheckResult(Some("无"),
        Some("无"), Some("无"), Some("无"), Some("无"), Some("无"), Some("无"), Some("无"), Some("无"), Some("无"), Some("无"),
        Some("无"), Some("无"), Some("无"), Some("无"), Some("无"))), Some("备注"))))
  }

  def create(kg: Long, studentId: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[StudentHealthCheckDetail].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, studentId: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[StudentHealthCheckDetail].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, studentId: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(new SuccessResponse()))
  }
}

/*
create table IMSInfo.dbo.PhysicalExamination(
SysNO int identity(1,1),系统编号
RecordName nvarchar(50),体检名称
RecordTime datetime,体检时间
StudentSysNO int,学生编号
Basic_Height nvarchar(50),--身高
Basic_Weight nvarchar(50),--体重
Head_Eye_LeftEyeSight nvarchar(50),--左眼视力
Head_Eye_RightEyeSight nvarchar(50),--右眼视力
Head_Eye_Trachoma nvarchar(50),--沙眼
Head_Eye_Conjunctivitis nvarchar(50),--结膜炎
Head_Ear_LeftEar nvarchar(50),--左耳听力
Head_Ear_RightEar nvarchar(50),--右耳听力
Head_Tooth_QTY nvarchar(50),--牙齿数量
Head_Tooth_Caries nvarchar(50),--龋齿
Head_Tooth_Periodontal nvarchar(50),--牙周
Surgery_HeadCircumference nvarchar(50),--头围
Surgery_ChestCircumference nvarchar(50),--胸围
Surgery_Limbs nvarchar(50),--四肢
Surgery_Spine nvarchar(50),--脊柱
Surgery_Skin nvarchar(50),--皮肤
Surgery_Lymphaden nvarchar(50),--淋巴结
Medicine_LeftTonsil nvarchar(50),--左扁桃体
Medicine_RightTonsil nvarchar(50),--右扁桃体
Medicine_BloodPressure nvarchar(50),--血压
Medicine_BloodTypeSysNO nvarchar(50),--血型编号
Medicine_BloodTypeName nvarchar(50),--血型名称
Medicine_Hemoglobin nvarchar(50),--血红蛋白素
Medicine_Heart nvarchar(50),--心
Medicine_Liver nvarchar(50),--肝
Medicine_Spleen nvarchar(50),--脾
Medicine_Lung nvarchar(50),--肺
Other_Rickets_ChongMen nvarchar(50),--囱门
Other_Rickets_FangTou nvarchar(50),--方头
Other_Rickets_PingPongTou nvarchar(50),--乒乓头
Other_Rickets_ZhenTu nvarchar(50),--枕秃
Other_Rickets_JiXiong nvarchar(50),--鸡胸
Other_Rickets_LeChuanZhu nvarchar(50),--肋串珠
Other_Rickets_HaoShiGou nvarchar(50),--郝氏沟
Other_Rickets_YiJingDuoHan nvarchar(50),--易惊多汗
Other_Rickets_XO nvarchar(50),--XO型腿
Other_InfantileParalysis nvarchar(50),--小儿麻痹症
Other_Ascarid nvarchar(50),--蛔虫病
Other_Skins nvarchar(50),--各种皮肤病
Other_Deformity nvarchar(50),--各种畸形
Other_Psychosis nvarchar(50),--精神性疾病
Other_Hernia nvarchar(50),--疝气
Other_Others nvarchar(50),--其他
Memo nvarchar(500)--总结
)
 */