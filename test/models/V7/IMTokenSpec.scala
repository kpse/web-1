package models.V7

import _root_.helper.TestSupport
import models.{Employee, IMAccount, Parent}
import org.specs2.mutable.Specification
import play.api.libs.json.Reads

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}


class IMTokenSpec extends Specification with TestSupport {

  def successCall(kg: Long, request: String, reads: Reads[IMTokenRes]): Future[Option[IMTokenRes]] = Future.successful(Some(IMTokenRes(200, "123", "321")))
  def failureCall(kg: Long, request: String, reads: Reads[IMTokenRes]): Future[Option[IMTokenRes]] = Future{None}
  def successClassGroupCall(kg: Long, request: String, reads: Reads[IMBasicRes]): Future[Option[IMBasicRes]] = Future.successful(Some(IMBasicRes(200)))
  def failureClassGroupCall(kg: Long, request: String, reads: Reads[IMBasicRes]): Future[Option[IMBasicRes]] = Future{None}

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

    "create class group for parents if it is existing in db" in new WithApplication {

      private val account: IMAccount = Parent.phoneSearch("13408654680").get
      private val result = IMToken.classGroup(93740362, "13408654680", 777888, Some(account))(failureClassGroupCall)

      result must beSome(IMClassGroup("db", 93740362, 777888, "93740362_777888", "苹果班")).await
    }

    "create class group for employee if it is existing in db" in new WithApplication {

      private val account: IMAccount = Employee.findByLoginName("e0001").get
      private val result = IMToken.classGroup(93740362, "e0001", 777888, Some(account))(failureClassGroupCall)

      result must beSome(IMClassGroup("db", 93740362, 777888, "93740362_777888", "苹果班")).await
    }

    "create class group from internet if it is not existing in db" in new WithApplication {

      private val account: IMAccount = Parent.phoneSearch("14880498549").get
      private val result = IMToken.classGroup(93740362, "e0001", 777999, Some(account))(successClassGroupCall)

      result must beSome(IMClassGroup("internet", 93740362, 777999, "93740362_777999", "香蕉班")).await
    }

    "report error creating group if the network fails" in new WithApplication {

      private val account: IMAccount = Parent.phoneSearch("14880498549").get
      private val result = IMToken.classGroup(93740362, "e0001", 777999, Some(account))(failureClassGroupCall)

      result must beNone.await
    }

  }
}
