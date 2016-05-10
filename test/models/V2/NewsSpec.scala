package models.V2

import _root_.helper.TestSupport
import models.News
import org.specs2.mutable.Specification

class NewsSpec extends Specification with TestSupport {

  "News" should {
    "be able to contain multiple images" in new WithApplication {

      private val news = News.findById(93740362, 8).get

      news.images.get must contain("http://attachment.van698.com/forum/201305/07/181354wxq8xkv8tccbwc9v.jpg")
      news.images.get must contain("http://stock.591hx.com/images/hnimg/201401/27/80/14517761501597144164.jpg")
    }

    "be able to contain corresponding sms content" in new WithApplication {

      private val news = News.findById(93740362, 9).get

      news.sms.get must equalTo("sms content for news 9")
    }
  }

}
