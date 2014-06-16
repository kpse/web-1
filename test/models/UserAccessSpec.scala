package models

import org.specs2.mutable.Specification

class UserAccessSpec extends Specification {
  "UserAccess" should {

    "filter classes" in {

      val classes: List[SchoolClass] = List(SchoolClass(1, Some(123), "name", None),
        SchoolClass(1, Some(124), "name", None))
      val list: List[UserAccess] = List(UserAccess(1, "1", "teacher", "123"))
      val result = UserAccess.filter(list)(classes)

      result.size must beEqualTo(1)
    }

    "allow full access for principal" in {

      val classes: List[SchoolClass] = List(SchoolClass(1, Some(123), "name", None),
        SchoolClass(1, Some(124), "name", None))
      val list: List[UserAccess] = List(UserAccess(1, "1", "principal", "123"))
      val result = UserAccess.filter(list)(classes)

      result.size must beEqualTo(2)
    }

    "allow full access for operator" in {

      val classes: List[SchoolClass] = List(SchoolClass(1, Some(123), "name", None),
        SchoolClass(1, Some(124), "name", None))
      val list: List[UserAccess] = List(UserAccess(1, "1", "operator", ""))
      val result = UserAccess.filter(list)(classes)

      result.size must beEqualTo(2)
    }

    "allow full access when subordinates are matched " in {

      val classes: List[SchoolClass] = List(SchoolClass(1, Some(123), "name", None),
        SchoolClass(1, Some(124), "name", None))
      val list: List[UserAccess] = List(UserAccess(1, "1", "teacher", "123"), UserAccess(1, "1", "teacher", "124"))
      val result = UserAccess.filter(list)(classes)

      result.size must beEqualTo(2)
    }

    "restrict access if subordinate not matched" in {

      val classes: List[SchoolClass] = List(SchoolClass(1, Some(123), "name", None),
        SchoolClass(1, Some(124), "name", None))
      val list: List[UserAccess] = List(UserAccess(1, "1", "teacher", "125"))
      val result = UserAccess.filter(list)(classes)

      result.isEmpty must beTrue
    }

    "restrict access if no access rule found" in {

      val classes: List[SchoolClass] = List(SchoolClass(1, Some(123), "name", None),
        SchoolClass(1, Some(124), "name", None))
      val list: List[UserAccess] = List()
      val result = UserAccess.filter(list)(classes)

      result.isEmpty must beTrue
    }

    "keep one class for principal" in {

      val classes: Option[Long] = Some(123)
      val list: List[UserAccess] = List(UserAccess(1, "1", "principal", ""))
      val result = UserAccess.filterClassId(list)(classes)

      result must beSome(123)
    }

    "keep one class for operator" in {

      val classes: Option[Long] = Some(123)
      val list: List[UserAccess] = List(UserAccess(1, "1", "operator", ""))
      val result = UserAccess.filterClassId(list)(classes)

      result must beSome(123)
    }

    "keep one class by access" in {

      val classes: Option[Long] = Some(123)
      val list: List[UserAccess] = List(UserAccess(1, "1", "teacher", "123"))
      val result = UserAccess.filterClassId(list)(classes)

      result must beSome(123)
    }

    "remove one class by access" in {

      val classes: Option[Long] = Some(124)
      val list: List[UserAccess] = List(UserAccess(1, "1", "teacher", "123"))
      val result = UserAccess.filterClassId(list)(classes)

      result must beNone
    }

    "keep all classes for principal" in {

      val classes: Option[String] = Some("123,124")
      val list: List[UserAccess] = List(UserAccess(1, "1", "principal", ""))
      val result = UserAccess.filterClassIds(list)(classes)

      result must beEqualTo("123,124")
    }

    "keep all classes for operator" in {

      val classes: Option[String] = Some("123,124,125")
      val list: List[UserAccess] = List(UserAccess(1, "1", "operator", ""))
      val result = UserAccess.filterClassIds(list)(classes)

      result must beEqualTo("123,124,125")
    }

    "keep one class by access" in {

      val classes: Option[String] = Some("123,124,125")
      val list: List[UserAccess] = List(UserAccess(1, "1", "teacher", "123"))
      val result = UserAccess.filterClassIds(list)(classes)

      result must beEqualTo("123")
    }

    "remove all classes by access" in {

      val classes: Option[String] = Some("124,125")
      val list: List[UserAccess] = List(UserAccess(1, "1", "teacher", "123"))
      val result = UserAccess.filterClassIds(list)(classes)

      result.isEmpty must beTrue
    }

  }
}
