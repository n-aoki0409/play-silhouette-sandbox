package controllers.forms

import play.api.libs.json.Json

case class SignInForm(email: String, password: String)

object SignInForm {
  implicit val jsonReads = Json.reads[SignInForm]
}
