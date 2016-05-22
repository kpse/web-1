package models.V8

import _root_.helper.TestSupport
import models.V7.{IMBasicRes, IMTokenRes}
import models.{Employee, IMAccount, Parent}
import org.specs2.mutable.Specification
import play.api.libs.json.Reads

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class IMHidingChatMessageSpec extends Specification with TestSupport {

  def successfulCall(kg: Long, uri: String, request: String, reads: Reads[IMBasicRes]): Future[Option[IMBasicRes]] = Future.successful(Some(IMBasicRes(200)))
  def failedCall(kg: Long, uri: String, request: String, reads: Reads[IMBasicRes]): Future[Option[IMBasicRes]] = Future{None}
  val schoolId: Long = 123
  val classId: Int = 321

  "IMHidingChatMessage" should {
    "spread out hide notification" in new WithApplication {
      private val result = IMHidingChatMessage("messageId", schoolId, classId).hideNotification()(successfulCall)

      result must beSome(IMBasicRes(200)).await
    }

    "failed when server return nothing" in new WithApplication {
      private val result = IMHidingChatMessage("messageId", schoolId, classId).hideNotification()(failedCall)

      result must beNone.await
    }

  }

  "IMHidingPrivateChatMessage" should {
    "spread out hide notification" in new WithApplication {

      private val result = IMHidingPrivateChatMessage("messageId", schoolId, "userId").hideNotification()(successfulCall)

      result must beSome(IMBasicRes(200)).await
    }

    "failed when server return nothing" in new WithApplication {
      private val result = IMHidingPrivateChatMessage("messageId", schoolId, "userId").hideNotification()(failedCall)

      result must beNone.await
    }

  }
}
