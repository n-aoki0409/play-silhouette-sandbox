package models

import com.mohiva.play.silhouette.api.Identity

case class User(
  userId: String,
  firstName: String,
  lastName: String,
  email: String,
  hashedPassword: String
) extends Identity
