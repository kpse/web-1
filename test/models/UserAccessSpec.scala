package models

import org.specs2.mutable.Specification

class UserAccessSpec extends Specification {
  "UserAccess" should {
    "filter Classes" in {

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


  }
}
