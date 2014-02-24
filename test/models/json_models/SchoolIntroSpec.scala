package models.json_models

import org.specs2.mutable.Specification
import helper.TestSupport


class SchoolIntroSpec extends Specification with TestSupport {

  "School" should {
    "report index" in new WithApplication {

      private val index = SchoolIntro.index

      index.size must equalTo(2)
      index(0).name must equalTo("成都市第三军区幼儿园")
      index(0).school_id must equalTo(93740362L)

      index(1).name must equalTo("西安市高新一幼")
      index(1).school_id must equalTo(93740562L)

    }
  }
}
