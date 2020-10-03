package modules

import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.api.crypto.{Base64AuthenticatorEncoder, Signer}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.api.services.{AuthenticatorService, IdentityService}
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.crypto.{JcaSigner, JcaSignerSettings}
import com.mohiva.play.silhouette.impl.authenticators.{CookieAuthenticator, CookieAuthenticatorService, CookieAuthenticatorSettings}
import com.mohiva.play.silhouette.impl.util.{DefaultFingerprintGenerator, SecureRandomIDGenerator}
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import infrastructure.repositories.{UserRepository, UserRepositoryImpl}
import models.{DefaultEnv, User}
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import net.ceedubs.ficus.readers.ValueReader
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.mvc.{Cookie, CookieHeaderEncoding}
import services.{UserService, UserServiceImpl}

import scala.concurrent.ExecutionContext

class SilhouetteModule extends AbstractModule with ScalaModule {

  implicit val sameSiteReader: ValueReader[Option[Cookie.SameSite]] =
    ValueReader.relative(cfg => Cookie.SameSite.parse(cfg.as[String]))

  override def configure(): Unit = {
    bind[IdentityService[User]].to[UserService]
    bind[EventBus].toInstance(EventBus())
    bind[Clock].toInstance(Clock())
    bind[FingerprintGenerator].toInstance(new DefaultFingerprintGenerator(false))
    bind[Silhouette[DefaultEnv]].to[SilhouetteProvider[DefaultEnv]]
    bind[PasswordHasher].toInstance(new BCryptPasswordHasher())
    bind[UserService].to[UserServiceImpl]
    bind[UserRepository].to[UserRepositoryImpl]
    bind[AuthInfoRepository].to[UserRepository]
  }

  @Provides
  def getEnvironment(
    identityService: IdentityService[User],
    authenticatorService: AuthenticatorService[CookieAuthenticator],
    eventBus: EventBus
  )(implicit ec: ExecutionContext): Environment[DefaultEnv] = {
    Environment[DefaultEnv](identityService, authenticatorService, Seq(), eventBus)
  }

  @Provides
  def getIDGenerator()(implicit ec: ExecutionContext): IDGenerator =
    new SecureRandomIDGenerator

  @Provides
  def getPasswordHasherRegistry(passwordHasher: PasswordHasher): PasswordHasherRegistry =
    PasswordHasherRegistry(passwordHasher)

  @Provides
  def getAuthenticatorService(
    fingerprintGenerator: FingerprintGenerator,
    signer: Signer,
    cookieHeaderEncoding: CookieHeaderEncoding,
    idGenerator: IDGenerator,
    configuration: Configuration,
    clock: Clock
  )(implicit ec: ExecutionContext): AuthenticatorService[CookieAuthenticator] = {
    val authenticatorEncoder = new Base64AuthenticatorEncoder
    val settings = configuration.underlying.as[CookieAuthenticatorSettings]("silhouette.authenticator")
    new CookieAuthenticatorService(
      settings,
      None,
      signer,
      cookieHeaderEncoding,
      authenticatorEncoder,
      fingerprintGenerator,
      idGenerator,
      clock
    )
  }

  @Provides
  def provideAuthenticatorSigner(configuration: Configuration): Signer =
    new JcaSigner(configuration.underlying.as[JcaSignerSettings]("silhouette.authenticator.signer"))
}
