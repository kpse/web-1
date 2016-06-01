package models.V8

import _root_.helper.TestSupport
import models.V7.IMBasicRes
import org.specs2.mutable.Specification
import play.api.libs.json.Reads

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class IMKeywordSpec extends Specification with TestSupport {

  val schoolId: Long = 93740362

  "IMKeyword" should {
    "report index" in new WithApplication {
      private val result = IMKeyword.index(schoolId, None, None, None)

      result must not beEmpty
    }

    "report empty if no keywords in specific school" in new WithApplication {
      val noKeywordSchoolId = 123
      private val result = IMKeyword.index(noKeywordSchoolId, None, None, None)

      result must beEmpty
    }

    "be created with word" in new WithApplication {
      private val result = IMKeyword(None, "new keyword").create(schoolId).get

      result.word must equalTo("new keyword")

      private val retrieval = IMKeyword.show(schoolId, result.id.get).get

      retrieval.word must equalTo("new keyword")
    }

  }
}
