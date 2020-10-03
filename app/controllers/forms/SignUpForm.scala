package controllers.forms

import play.api.libs.json.Json

case class SignUpForm(firstName: String, lastName: String, email: String, password: String)

object SignUpForm {
  implicit val jsonReads = Json.reads[SignUpForm]
}
