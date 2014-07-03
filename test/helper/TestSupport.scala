package helper

import org.specs2.execute.AsResult
import play.api.test.FakeApplication
import play.api.test.Helpers._
import org.specs2.mutable.{Around, Specification}

trait TestSupport {
  self: Specification =>

  trait WithApplication extends Around {
    def around[T: AsResult](t: => T) = {
      val appWithMemoryDatabase = FakeApplication(additionalConfiguration = Map(
        "db.default.driver" -> "org.h2.Driver",
        "db.default.url" -> "jdbc:h2:mem:test;MODE=MySQL"
      ))
      running(appWithMemoryDatabase) {
        AsResult.effectively(t)
      }
    }
  }

}
