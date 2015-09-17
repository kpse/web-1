package models

import _root_.helper.TestSupport
import models.V4.KulebaoAgent
import org.specs2.mutable.Specification

class KulebaoAgentSpec extends Specification with TestSupport {

  "Agent" should {
    "be able to login with username and password" in new WithApplication {

      private val user = KulebaoAgent.authenticate("a0001", "secret").get

      user.login_name must beSome("a0001")
      user.name must beSome("肉夹馍")
    }

    "have default password as phone number" in new WithApplication {

      createAgent("11223344556", "newcreated").create

      private val user = KulebaoAgent.authenticate("newcreated", "11223344556").get
      user.phone must beSome("11223344556")
    }
  }

  def createAgent(phone: String, loginName: String): KulebaoAgent = {
    KulebaoAgent(None, None, None, Some(phone), None, None, None, None, Some(loginName), None, None, None)
  }
}
