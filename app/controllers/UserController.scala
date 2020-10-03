package controllers

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{LoginEvent, LoginInfo, LogoutEvent, Silhouette}
import com.mohiva.play.silhouette.api.util.{Credentials, PasswordHasher}
import com.mohiva.play.silhouette.impl.exceptions.{IdentityNotFoundException, InvalidPasswordException}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import controllers.forms.{SignInForm, SignUpForm}
import models.{DefaultEnv, User}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.UserService

import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject()(
  cc: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  credentialsProvider: CredentialsProvider,
  userService: UserService,
  passwordHasher: PasswordHasher
)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def signUp(): Action[SignUpForm] = silhouette.UnsecuredAction(parse.json[SignUpForm]).async { implicit request =>
    val loginInfo = LoginInfo(CredentialsProvider.ID, request.body.email)
    userService.retrieve(loginInfo).flatMap {
      case Some(_) =>
        Future.successful(Ok("[Silhouette][credentials] Duplicate user"))
      case None =>
        val authInfo = passwordHasher.hash(request.body.password)
        val user = User(
          userId = scala.util.Random.alphanumeric.take(20).mkString,
          firstName = request.body.firstName,
          lastName = request.body.lastName,
          email = request.body.email,
          hashedPassword = authInfo.password
        )
        for {
          _ <- userService.add(user)
          authenticator <- silhouette.env.authenticatorService.create(loginInfo)
          value <- silhouette.env.authenticatorService.init(authenticator)
          result <- silhouette.env.authenticatorService.embed(value, Ok)
        } yield {
          result
        }
    }
  }

  def signIn(): Action[SignInForm] = silhouette.UnsecuredAction(parse.json[SignInForm]).async { implicit request =>
    val credentials = Credentials(request.body.email, request.body.password)
    credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
      userService.retrieve(loginInfo).flatMap {
        case Some(user) =>
          silhouette.env.authenticatorService.create(loginInfo).flatMap { authenticator =>
            silhouette.env.eventBus.publish(LoginEvent(user, request))
            silhouette.env.authenticatorService.init(authenticator).flatMap { value =>
              silhouette.env.authenticatorService.embed(value, Ok)
            }
          }
        case None =>
          Future.successful(
            Unauthorized
          )
      }
    } recover {
      case e: InvalidPasswordException =>
        Unauthorized(e.getMessage)
      case e: IdentityNotFoundException =>
        Unauthorized(e.getMessage)
      case _ =>
        InternalServerError
    }
  }

  def signOut(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, Ok)
  }
}
