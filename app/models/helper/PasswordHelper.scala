package models.helper

object PasswordHelper {
  def isValid(password: String): Boolean = password.matches("^\\w{6,16}$")
}
