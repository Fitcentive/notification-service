package io.fitcentive.notification.infrastructure.verification

import cats.data.EitherT
import io.fitcentive.notification.domain.BasicCredentials
import io.fitcentive.notification.domain.config.SmtpConfig
import io.fitcentive.notification.domain.email.{EmailContents, EmailFrom}
import io.fitcentive.notification.domain.errors.{EmailDeliveryError, EmailError}
import io.fitcentive.notification.infrastructure.contexts.SmtpExecutionContext
import io.fitcentive.sdk.logging.AppLogger
import io.fitcentive.notification.services.{EmailService, SettingsService, ValidationService}

import java.util.{Date, Properties}
import javax.inject.Inject
import javax.mail.{Authenticator, Message, PasswordAuthentication, Session, Transport}
import javax.mail.internet.{InternetAddress, MimeBodyPart, MimeMessage, MimeMultipart}
import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps

class SmtpEmailService @Inject() (validationService: ValidationService, settingsService: SettingsService)(implicit
  ec: SmtpExecutionContext
) extends EmailService
  with AppLogger {

  private val smtpConfig: SmtpConfig = settingsService.smtpConfig

  private val smtpProperties =
    new Properties()
      .tap(_.put("mail.smtp.host", smtpConfig.host))
      .tap(_.put("mail.smtp.port", smtpConfig.port))
      .tap(_.put("mail.smtp.auth", smtpConfig.auth.map(_ => "true").getOrElse("false")))
      .tap(_.put("mail.smtp.starttls.enable", smtpConfig.startTls))
      .tap(properties => smtpConfig.auth.foreach(a => properties.put("mail.smtp.user", a.username)))

  private def getPasswordAuthentication(user: String, pass: String): Authenticator =
    new javax.mail.Authenticator {
      override def getPasswordAuthentication: PasswordAuthentication =
        new PasswordAuthentication(user, pass)
    }

  private def session =
    Session.getInstance(
      smtpProperties,
      smtpConfig.auth.map(a => getPasswordAuthentication(a.username, a.password)).orNull
    )

  override def sendEmail(emailContents: EmailContents): Future[Either[EmailError, Unit]] = {
    val from = emailContents.from
    val email = emailContents.to
    val subject = emailContents.subject
    val body = emailContents.body
    (for {
      emailAddress <-
        EitherT[Future, EmailError, InternetAddress](Future.successful(validationService.validateEmail(email)))
      _ <- EitherT[Future, EmailError, Unit] {
        val bodyPart = new MimeBodyPart()
        bodyPart.setContent(body, "text/html; charset=utf-8")
        val bodyParts = List(bodyPart)
        val recipients = List(emailAddress)

        createEmailAndSend(from, bodyParts, recipients, subject)
          .map { _ =>
            logInfo(s"Successfully sent email to $email")
            Right(())
          }
          .recoverWith { ex =>
            logError(s"Failed to send email to '$email' with subject '$subject'", ex)
            Future.successful(Left(EmailDeliveryError(ex.getMessage)))
          }
      }
    } yield ()).value
  }

  private def createEmailAndSend(
    from: EmailFrom,
    bodyParts: => List[MimeBodyPart],
    recipients: => List[InternetAddress],
    subject: => String
  ): Future[Unit] =
    Future {
      val multipart = new MimeMultipart()
      bodyParts.foreach(multipart.addBodyPart)

      val message = prepareMessage(from, subject)
      recipients.foreach(r => message.addRecipient(Message.RecipientType.TO, r))
      message.setContent(multipart)
      send(message)
    }

  private def prepareMessage(from: EmailFrom, subject: String) = {
    val message = new MimeMessage(session)
    message.setFrom(new InternetAddress(from.email, from.name))
    message.setSentDate(new Date())
    message.setSubject(subject, "utf-8")
    message
  }

  private def send(message: MimeMessage): Unit =
    smtpConfig.auth match {
      case Some(BasicCredentials(user, pass)) =>
        Transport.send(message, message.getAllRecipients, user, pass)
        logInfo(s"Email sent to: ${message.getAllRecipients.mkString(", ")}")
      case None =>
        Transport.send(message)
    }
}
