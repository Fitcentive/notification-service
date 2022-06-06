package api

import domain.email.{EmailContents, EmailFrom}
import domain.errors.EmailError
import services.{EmailService, MessageBusService}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class NotificationApi @Inject() (emailService: EmailService, messageBus: MessageBusService)(implicit
  ec: ExecutionContext
) {

  def startPubSubService: Future[Unit] = {
    println("IN THE API LAYER")
    messageBus.init
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
