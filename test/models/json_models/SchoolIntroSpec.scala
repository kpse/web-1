package models.json_models

import models.{Employee, ChargeInfo}
import org.specs2.mutable.Specification
import helper.TestSupport


class SchoolIntroSpec extends Specification with TestSupport {

  "School" should {
    "report index" in new WithApplication {

      private val index = SchoolIntro.index()

      index.size must beGreaterThanOrEqualTo(3)
      index.head.name must equalTo("第三军区幼儿园")
      index.head.school_id must equalTo(93740362L)

      index(1).name must equalTo("高新一幼")
      index(1).school_id must equalTo(93740562L)

    }
    "be created by creatingSchool model" in new WithApplication {

      private val principalPhone: String = "13999999991"
      private val creatingSchool: CreatingSchool = CreatingSchool(123, "010-88881111", "名称", "token", PrincipalOfSchool("login", "pass", principalPhone),
        ChargeInfo(123, 100, "2016-01-01", 1, 99), "address", Some("全名"))
      private val created = SchoolIntro.create(creatingSchool).get
      private val detail = SchoolIntro.detail(created.school_id).get

      detail.name must equalTo("名称")
      detail.school_id must equalTo(created.school_id)
      detail.full_name must equalTo(creatingSchool.full_name)

      Employee.show(principalPhone).get.login_name must equalTo("login")

    }

    "not be created if principal phone conflicts" in new WithApplication {

      private val principalPhone: String = "13999999991"
      private val firstSchool: CreatingSchool = CreatingSchool(123, "010-88881111", "名称", "token",
        PrincipalOfSchool("login", "pass", principalPhone),
        ChargeInfo(123, 100, "2016-01-01", 1, 99), "address", Some("全名"))
      private val secondSchool: CreatingSchool = CreatingSchool(124, "010-88881111", "名称", "token",
        PrincipalOfSchool("login2", "pass", principalPhone),
        ChargeInfo(124, 100, "2016-01-01", 1, 99), "address", Some("全名2"))

      SchoolIntro.create(firstSchool) must not beEmpty

      SchoolIntro.create(secondSchool) must throwAn[IllegalArgumentException]


      Employee.show(principalPhone).get.login_name must equalTo("login")

    }

    "not be created if full name conflicts" in new WithApplication {

      private val fullName: String = "全名一样"
      private val firstSchool: CreatingSchool = CreatingSchool(123, "010-88881111", "名称", "token",
        PrincipalOfSchool("login", "pass", "13999999991"),
        ChargeInfo(123, 100, "2016-01-01", 1, 99), "address", Some(fullName))
      private val secondSchool: CreatingSchool = CreatingSchool(124, "010-88881111", "名称", "token",
        PrincipalOfSchool("login2", "pass", "13999999992"),
        ChargeInfo(124, 100, "2016-01-01", 1, 99), "address", Some(fullName))

      SchoolIntro.create(firstSchool) must not beEmpty

      SchoolIntro.create(secondSchool) must throwAn[IllegalArgumentException]
    }

    "not be created if school id conflicts" in new WithApplication {

      private val schoolId: Long = 9982
      private val firstSchool: CreatingSchool = CreatingSchool(schoolId, "010-88881111", "名称", "token",
        PrincipalOfSchool("login", "pass", "13999999991"),
        ChargeInfo(schoolId, 100, "2016-01-01", 1, 99), "address", Some("全名1"))
      private val secondSchool: CreatingSchool = CreatingSchool(schoolId, "010-88881111", "名称", "token",
        PrincipalOfSchool("login2", "pass", "13999999992"),
        ChargeInfo(schoolId, 100, "2016-01-01", 1, 99), "address", Some("全名2"))

      SchoolIntro.create(firstSchool) must not beEmpty

      SchoolIntro.create(secondSchool) must throwAn[IllegalArgumentException]
    }
  }
}
