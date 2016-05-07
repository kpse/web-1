package models.V7

import _root_.helper.TestSupport
import org.specs2.mutable.Specification


class IMBanUserSpec extends Specification with TestSupport {

  "IMBanUser" should {
    val schoolId = 333
    val classId = 222

    "ban user in school" in new WithApplication {
      IMBanUser("123", None).ban(schoolId, classId)

      private val userList: List[IMBanUserFromServer] = IMBanUser.bannedUserList(schoolId, classId, None)

      userList must contain(IMBanUserFromServer("123", "0"))
    }

    "approve banned user in school " in new WithApplication {

      IMBanUser("123", None).ban(schoolId, classId)
      IMBanUser.bannedUserList(schoolId, classId, None) must contain(IMBanUserFromServer("123", "0"))
      IMBanUser("123", None).approve(schoolId, classId)

      IMBanUser.bannedUserList(schoolId, classId, None) must not contain IMBanUserFromServer("123", "0")
    }

  }
}
