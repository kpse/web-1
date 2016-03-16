package models

import _root_.helper.TestSupport
import org.specs2.mutable.Specification

class VideoMemberSpec extends Specification with TestSupport {

  "VideoMember" should {
    "should contain expire_at field" in new WithApplication {

      private val created: Option[Long] = VideoMember("id", Some("account"), Some("password"), Some(1), Some(2), Some("memo")).create

      created.get must greaterThan(0L)

      private val member: VideoMember = VideoMember.show(1, "id").get

      member.account must beSome("account")
      member.school_id must beSome(1)
      member.memo must beSome("memo")
      member.expire_at must beSome(2)
    }

  }
}
