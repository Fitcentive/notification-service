package services

import com.google.inject.ImplementedBy
import domain.errors.EmailValidationError
import infrastructure.validation.EntityValidationService

import javax.mail.internet.InternetAddress

@ImplementedBy(classOf[EntityValidationService])
trait ValidationService {
  def validateEmail(email: String): Either[EmailValidationError, InternetAddress]
}
