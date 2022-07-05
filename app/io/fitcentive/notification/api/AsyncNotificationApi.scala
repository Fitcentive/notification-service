package io.fitcentive.notification.api

import io.fitcentive.notification.domain.email.{EmailContents, EmailFrom}
import io.fitcentive.notification.domain.errors.EmailError
import io.fitcentive.notification.domain.push.{NotificationDevice, PushNotificationResponse}
import io.fitcentive.notification.domain.push.messages.UserFollowRequestedMessage
import io.fitcentive.notification.services.{EmailService, PushNotificationService}
import io.fitcentive.notification.repositories.NotificationDeviceRepository

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AsyncNotificationApi @Inject() (
  emailService: EmailService,
  notificationDeviceRepository: NotificationDeviceRepository,
  pushNotificationService: PushNotificationService,
)(implicit ec: ExecutionContext) {

  def sendEmail(emailId: String, token: String): Future[Either[EmailError, Unit]] =
    emailService.sendEmail(
      EmailContents(
        from = EmailFrom("Fitcentive", "no-reply@fitcentive.io"),
        to = emailId,
        subject = "Email Verification Token",
        body = s"Here is your token: $token"
      )
    )

  def upsertDevice(device: NotificationDevice): Future[NotificationDevice] =
    notificationDeviceRepository.upsertDevice(device)

  def unregisterDevice(registrationToken: String): Future[Unit] =
    notificationDeviceRepository.deleteFcmToken(registrationToken)

  def sendUserFollowRequestNotification(requestingUser: UUID, targetUser: UUID): Future[PushNotificationResponse] =
    pushNotificationService.sendUserFollowRequestNotification(UserFollowRequestedMessage(requestingUser, targetUser))

}
