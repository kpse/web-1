package models.V7

import models.IMAccount
import models.V7.IMToken.IMWS
import play.Logger
import play.api.db.DB
import anorm.SqlParser._
import anorm._
import play.api.Play.current

case class IMBanUser(id: String, minute: Option[Int]) {
  val IMSystemGroupMonitor = IMBanUser.IMSystemGroupMonitor

  def ban(schoolId: Long, classId: Int) = DB.withConnection {
    implicit c =>
      SQL(s"insert into im_internal_banned_users (school_id, class_id, user_id, updated_at, created_at) values " +
        s"({kg}, {class_id}, {user}, {time}, {time})")
        .on(
          'kg -> schoolId.toString,
          'class_id -> classId,
          'user -> id,
          'time -> System.currentTimeMillis()
        ).executeInsert()
  }

  def approve(schoolId: Long, classId: Int) = DB.withConnection {
    implicit c =>
      SQL(s"update im_internal_banned_users set status=0, updated_at={time} where " +
        s" school_id={kg} and class_id={class_id} and user_id={user} and status=1")
        .on(
          'kg -> schoolId.toString,
          'class_id -> classId,
          'user -> id,
          'time -> System.currentTimeMillis()
        ).executeUpdate()
  }


  def approvalNotification(schoolId: Long, classId: Int)(implicit ws: IMWS[IMBasicRes] = IMToken.rongyunWS[IMBasicRes]) = {
    val approvalBroadcastMessage = s"approval - $id - $schoolId - $classId"
    val groupId = s"${schoolId}_$classId"
    val payload: String = s"""fromUserId=$IMSystemGroupMonitor&toUserId=$id&objectName=CB:CtrlMsg&content={\"content\":\"$approvalBroadcastMessage\"}"""
    Logger.info(s"Approval user $id - sending to group $groupId with approvalBroadcastMessage: $payload")
    ws(schoolId, "/message/private/publish.json", payload, IMToken.readsIMBasicRes)
  }


  def bannedNotification(schoolId: Long, classId: Int)(implicit ws: IMWS[IMBasicRes] = IMToken.rongyunWS[IMBasicRes]) = {
    val banningBroadcastMessage = s"ban - $id - $schoolId - $classId"
    val groupId = s"${schoolId}_$classId"
    val payload: String = s"""fromUserId=$IMSystemGroupMonitor&toUserId=$id&objectName=CB:CtrlMsg&content={\"content\":\"$banningBroadcastMessage\"}"""
    Logger.info(s"Ban user $id - sending to group $groupId with banningBroadcastMessage: $payload")
    ws(schoolId, "/message/private/publish.json", payload, IMToken.readsIMBasicRes)
  }

  def undo(kg: Long, classId: Int): String = s"userId=$id&groupId=${kg}_$classId"

  def execute(kg: Long, classId: Int): String = s"userId=$id&groupId=${kg}_$classId&minute=${minute.getOrElse(99999)}"
}

object IMBanUser {
  val IMSystemGroupMonitor = "IMSystemGroupMonitor"
  def bannedUserList(kg: Long, classId: Int, imAccount: Option[IMAccount]): List[IMBanUserFromServer] = DB.withConnection {
    implicit c =>
      SQL(s"select distinct user_id from im_internal_banned_users where school_id={kg} and status=1 and class_id={class_id}")
        .on(
          'kg -> kg.toString,
          'class_id -> classId
        ).as(simple *)
  }

  val simple = {
    get[String]("user_id") map {
      case userId =>
        IMBanUserFromServer(userId, "0")
    }
  }
}
