package models.`enum`

sealed abstract class Role(val code: String, val name: String)

object Role {

  case object User extends Role("User", "User")

  val member: Seq[Role] = Seq(User)

  def apply(code: String): Role = {
    member.find(_.code == code) getOrElse(
      throw new IllegalArgumentException(s"Unknown Role: code=$code")
    )
  }
}
