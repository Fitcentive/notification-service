package io.fitcentive.notification.services

import com.google.inject.ImplementedBy
import io.fitcentive.notification.domain.email.EmailContents
import io.fitcentive.notification.domain.errors.EmailError
import io.fitcentive.notification.infrastructure.verification.SmtpEmailService

import scala.concurrent.Future

@ImplementedBy(classOf[SmtpEmailService])
trait EmailService {
  def sendEmail(emailContents: EmailContents): Future[Either[EmailError, Unit]]
}
