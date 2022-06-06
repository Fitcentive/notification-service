package infrastructure.validation

import domain.errors.EmailValidationError
import services.ValidationService

import javax.inject.{Inject, Singleton}
import javax.mail.internet.InternetAddress
import scala.util.Try
import scala.util.chaining.scalaUtilChainingOps

@Singleton
class EntityValidationService @Inject() extends ValidationService {

  override def validateEmail(email: String): Either[EmailValidationError, InternetAddress] =
    Try {
      val internetAddress = new InternetAddress(email)
      internetAddress.tap(_.validate())
    }.toEither
      .filterOrElse(!_.isGroup, new Exception("Invalid email type: Group emails not supported"))
      .left
      .map(ex => EmailValidationError(ex.getMessage))

}
