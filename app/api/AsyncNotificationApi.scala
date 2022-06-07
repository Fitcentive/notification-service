package api

import domain.email.{EmailContents, EmailFrom}
import domain.errors.EmailError
import services.EmailService

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AsyncNotificationApi @Inject() (emailService: EmailService)(implicit ec: ExecutionContext) {

  def sendEmail(emailId: String, token: String): Future[Either[EmailError, Unit]] = {
    emailService.sendEmail(
      EmailContents(
        from = EmailFrom("Fitcentive", "no-reply@fitcentive.io"),
        to = emailId,
        subject = "Email Verification Token",
        body = s"Here is your token: ${token}"
      )
    )
  }
}
