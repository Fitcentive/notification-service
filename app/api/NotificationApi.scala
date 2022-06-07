package api

import domain.email.{EmailContents, EmailFrom}
import domain.errors.EmailError
import services.EmailService

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NotificationApi @Inject() (emailService: EmailService)(implicit ec: ExecutionContext) {

  def testMethod: Future[Unit] = {
    println("TEst string")
    Future.unit
  }

  def sendEmail: Future[Either[EmailError, Unit]] = {
    emailService.sendEmail(
      EmailContents(
        from = EmailFrom("test", "test@new.com"),
        to = "email@address.com",
        subject = "Test Email Subject",
        body = "This is the first ever email"
      )
    )
  }
}
