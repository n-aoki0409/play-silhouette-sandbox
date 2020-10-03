package services

import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import infrastructure.repositories.UserRepository
import models.User

import scala.concurrent.{ExecutionContext, Future}

trait UserService extends IdentityService[User] {
 def add(user: User): Future[Int]
}

@Singleton
class UserServiceImpl @Inject() (userRepository: UserRepository) (implicit val ec: ExecutionContext) extends UserService {
  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userRepository.retrieve(loginInfo)
  override def add(user: User): Future[Int] = userRepository.add(user)
}
