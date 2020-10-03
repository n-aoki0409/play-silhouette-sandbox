package infrastructure.repositories

import java.sql.Timestamp

import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api.{AuthInfo, LoginInfo}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordInfo
import infrastructure.repositories.Tables._
import models.User
import org.mindrot.jbcrypt.BCrypt
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

trait UserRepository extends AuthInfoRepository {

  def retrieve(loginInfo: LoginInfo): Future[Option[User]]

  def add(user: User): Future[Int]
}

@Singleton
class UserRepositoryImpl @Inject() (
  val dbConfigProvider: DatabaseConfigProvider
) (implicit val ec: ExecutionContext) extends UserRepository with HasDatabaseConfigProvider[JdbcProfile] {

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] =
    db.run(
      for {
        user <- Users.filter(_.email === loginInfo.providerKey).result.headOption
      } yield user.map { user =>
        User(
          userId = user.userId,
          firstName = user.firstName,
          lastName = user.lastName,
          email = user.email,
          hashedPassword = user.hashedPassword
        )
      }
    )

  override def add(user: User): Future[Int] = {
    val current = System.currentTimeMillis
    db.run(
      Users += UsersRow(user.userId, user.firstName, user.lastName, user.email, user.hashedPassword, Some(new Timestamp(current)), Some(new Timestamp(current)))
    )
  }

  override def find[T <: AuthInfo](loginInfo: LoginInfo)(implicit tag: ClassTag[T]): Future[Option[T]] =
    db.run(
      for {
        user <- Users.filter(_.email === loginInfo.providerKey).result.headOption
      } yield user.map { user =>
        PasswordInfo(
          hasher = "bcrypt",
          password = user.hashedPassword,
          salt = Some(BCrypt.gensalt(10))
        )
      }
    ).map(_.map(_.asInstanceOf[T]))

  override def add[T <: AuthInfo](loginInfo: LoginInfo, authInfo: T): Future[T] =
    Future.failed(new UnsupportedOperationException)

  override def update[T <: AuthInfo](loginInfo: LoginInfo, authInfo: T): Future[T] = {
    db.run(
      for {
        user <- Users.filter(_.email === loginInfo.providerKey).result.headOption
      } yield user match {
        case Some(_) =>
          db.run(
            Users.filter(_.email === loginInfo.providerKey)
              .map(user => (user.hashedPassword, user.updatedAt))
              .update((authInfo.asInstanceOf[PasswordInfo].password, Some(new Timestamp(System.currentTimeMillis))))
          )
        case _ =>
      }
    ) map (_ => authInfo)
  }

  override def save[T <: AuthInfo](loginInfo: LoginInfo, authInfo: T): Future[T] =
    Future.failed(new UnsupportedOperationException)

  override def remove[T <: AuthInfo](loginInfo: LoginInfo)(implicit tag: ClassTag[T]): Future[Unit] =
    Future.failed(new UnsupportedOperationException)
}
