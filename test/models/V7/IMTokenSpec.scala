package models.V7

import _root_.helper.TestSupport
import models.{Employee, IMAccount, Parent}
import org.specs2.mutable.Specification
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global


class IMTokenSpec extends Specification with TestSupport {

  def successCall(kg: Long, request: String): Future[Option[IMTokenRes]] = Future.successful(Some(IMTokenRes(200, "123", "321")))
  def failureCall(kg: Long, request: String): Future[Option[IMTokenRes]] = Future{None}

  "IMToken" should {
    "retrieve token from IM provider if it is not existing in db" in new WithApplication {

      private val account: IMAccount = Parent.phoneSearch("14880498549").get
      private val result = IMToken.token(93740362, account)(successCall)

      result must beSome(IMToken("internet", "123", "321")).await
    }

    "provide token if it is existing in db" in new WithApplication {

      private val account: IMAccount = Parent.phoneSearch("13402815317").get
      private val result = IMToken.token(93740362, account)(failureCall)

      result must beSome(IMToken("db", "8888", "p_93740362_Some(1)_13402815317")).await
    }

    "provide token for employee if it is existing in db" in new WithApplication {

      private val existingEmployee: Employee = Employee.findByLoginName("e0001").get
      private val account: IMAccount = existingEmployee
      private val result = IMToken.token(93740362, account)(failureCall)

      result must beSome(IMToken("db", "9999", "t_93740362_Some(1)_e0001")).await
    }

    "report error if the network fails" in new WithApplication {

      private val account: IMAccount = Parent.phoneSearch("14880498549").get
      private val result = IMToken.token(93740362, account)(failureCall)

      result must beNone.await
    }

  }





}
