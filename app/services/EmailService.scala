package services

import com.google.inject.ImplementedBy
import domain.email.EmailContents
import domain.errors.EmailError
import infrastructure.verification.SmtpEmailService

import scala.concurrent.Future

@ImplementedBy(classOf[SmtpEmailService])
trait EmailService {
  def sendEmail(emailContents: EmailContents): Future[Either[EmailError, Unit]]
}
