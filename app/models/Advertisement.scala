package models

case class Advertisement(id: Long, school_id: Long, position_id: Long, link: String, image: String, name: String)

object Advertisement {
  def find(schoolId: Long): Advertisement = {
    if (schoolId > 2100 && schoolId != 93740362) {
      default
    } else {
      Advertisement(1, schoolId, 1, "https://www.cocobabys.com/", "https://www.cocobabys.com/assets/images/hero-1.jpg", "库贝影楼")
    }
  }

  val default = Advertisement(0, 0, 0, "", "", "幼乐宝")
}


