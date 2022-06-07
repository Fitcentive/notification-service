package io.fitcentive.notification.services

import com.google.inject.ImplementedBy
import io.fitcentive.notification.domain.errors.EmailValidationError
import io.fitcentive.notification.infrastructure.validation.EntityValidationService

import javax.mail.internet.InternetAddress

@ImplementedBy(classOf[EntityValidationService])
trait ValidationService {
  def validateEmail(email: String): Either[EmailValidationError, InternetAddress]
}
