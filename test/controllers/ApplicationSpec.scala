package controllers

import _root_.helper.TestSupport
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._
import play.api.mvc.{Cookies, Session}

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification with TestSupport {

  "Application" should {

    "send 404 on a bad request" in new WithApplication {
      route(FakeRequest(GET, "/boum")) must beNone
    }


    "render the page" in new WithApplication {
      val sessionCookie = Session.encodeAsCookie(Session(Map("username" -> "admin")))

      val home = route(FakeRequest(GET, "/admin").withHeaders(play.api.http.HeaderNames.COOKIE -> Cookies.encode(Seq(sessionCookie)))).get

      status(home) must equalTo(OK)
      contentType(home) must beSome("text/html")
    }

    "redirecting while no cookie" in new WithApplication {
      val login = route(FakeRequest(GET, "/admin")).get

      status(login) must equalTo(SEE_OTHER)
      contentType(login) must beNone
    }

    "render the login page" in new WithApplication{
      val login = route(FakeRequest(GET, "/login")).get

      status(login) must equalTo(OK)
      contentType(login) must beSome("text/html")
    }

    "render the index page" in new WithApplication{
      val index = route(FakeRequest(GET, "/")).get

      status(index) must equalTo(OK)
      contentType(index) must beSome("text/html")

    }

  }
}
