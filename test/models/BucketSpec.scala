package models

import _root_.helper.TestSupport
import org.specs2.mutable.Specification

class BucketSpec extends Specification with TestSupport {

  "Bucket" should {
    "should parse url" in new WithApplication {
        val result: Option[Bucket] = Bucket.parse("https://dn-kulebao.qbox.me/3_93740362_9972%252FfireEffect.mp3")
        result must beSome(Bucket(Some("kulebao-prod"), "3_93740362_9972%252FfireEffect.mp3"))
    }

    "should ignore other string than url" in new WithApplication {
      val result: Option[Bucket] = Bucket.parse("e6f7681249d77e4e69c69fe7866f532b")
      result must beNone
    }

    "should ignore query string in url" in new WithApplication {
      val result: Option[Bucket] = Bucket.parse("https://dn-cocobabys-test.qbox.me/3_93740362_9972%252FfireEffect.mp3?a=b")
      result must beSome(Bucket(Some("cocobabys-test"), "3_93740362_9972%252FfireEffect.mp3"))
    }

    "should ignore hash string in url" in new WithApplication {
      val result: Option[Bucket] = Bucket.parse("https://dn-cocobabys.qbox.me/3_93740362_9972%252FfireEffect.mp3#abc")
      result must beSome(Bucket(Some("cocobabys"), "3_93740362_9972%252FfireEffect.mp3"))
    }

    "should recognise bucket kulebao-prod" in new WithApplication {
      val result: Option[Bucket] = Bucket.parse("https://dn-youlebao.qbox.me/3_93740362_9972/fireEffect.mp3")
      result must beSome(Bucket(Some("kulebao-prod"), "3_93740362_9972/fireEffect.mp3"))
    }

    "should recognise bucket suoqin-test" in new WithApplication {
      val result: Option[Bucket] = Bucket.parse("http://suoqin-test.u.qiniudn.com/3_93740362_9972/fireEffect.mp3")
      result must beSome(Bucket(Some("suoqin-test"), "3_93740362_9972/fireEffect.mp3"))
    }

    "should recognise bucket localtest" in new WithApplication {
      val result: Option[Bucket] = Bucket.parse("https://dn-local-test.qbox.me/3_93740362_9972/fireEffect.mp3")
      result must beSome(Bucket(Some("localtest"), "3_93740362_9972/fireEffect.mp3"))
    }
  }
}
