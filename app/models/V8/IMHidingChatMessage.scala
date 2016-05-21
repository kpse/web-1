package models.V8

import models.V7.{IMBanUser, IMBasicRes, IMToken}
import models.V7.IMToken._
import play.Logger
import play.api.libs.json.Json

case class IMHidingChatMessage(id: String, school_id: Long, class_id: Int) {
  def hideNotification()(implicit ws: IMWS[IMBasicRes] = IMToken.rongyunWS[IMBasicRes]) = {
    val hideGroupBroadcastMessage = s"hidemsg - group - $id - $school_id - $class_id"
    val groupId = s"${school_id}_$class_id"
    val payload: String = s"""fromUserId=${IMBanUser.IMSystemGroupMonitor}&toGroupId=$groupId&objectName=CB:CtrlMsg&content={\"content\":\"$hideGroupBroadcastMessage\"}"""
    Logger.info(s"Hide group message $id - sending to group $groupId with hideGroupBroadcastMessage: $payload")
    ws(school_id, "/message/group/publish.json", payload, IMToken.readsIMBasicRes)
  }
}

case class IMHidingPrivateChatMessage(id: String, school_id: Long, user_id: String) {
  def hideNotification()(implicit ws: IMWS[IMBasicRes] = IMToken.rongyunWS[IMBasicRes]) = {
    val hidePrivateBroadcastMessage = s"hidemsg - private - $id - $user_id - $school_id"
    val payload: String = s"""fromUserId=${IMBanUser.IMSystemGroupMonitor}&toUserId=$id&objectName=CB:CtrlMsg&content={\"content\":\"$hidePrivateBroadcastMessage\"}"""
    Logger.info(s"Hide private chat message $id - sending to user $user_id with hidePrivateBroadcastMessage: $payload")
    ws(school_id, "/message/private/publish.json", payload, IMToken.readsIMBasicRes)
  }
}

object IMHidingChatMessage {
  implicit val readIMHidingChatMessage = Json.reads[IMHidingChatMessage]
  implicit val readIMHidingPrivateChatMessage = Json.reads[IMHidingPrivateChatMessage]
}
