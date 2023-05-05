package io.fitcentive.notification.infrastructure.firebase

import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import io.fitcentive.notification.domain.push.messages.{
  ChatRoomMessageSentMessage,
  MeetupReminderMessage,
  ParticipantAddedToMeetupMessage,
  UserFriendRequestedMessage
}
import io.fitcentive.notification.domain.push.{
  DevicePushNotificationResponse,
  PushNotificationMessage,
  PushNotificationResponse
}
import io.fitcentive.notification.infrastructure.contexts.FirebaseExecutionContext
import io.fitcentive.notification.repositories.NotificationDeviceRepository
import io.fitcentive.notification.services.PushNotificationService
import io.fitcentive.sdk.logging.AppLogger

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class FirebaseCloudMessagingService @Inject() (
  firebaseApp: FirebaseApp,
  notificationDeviceRepository: NotificationDeviceRepository
)(implicit ec: FirebaseExecutionContext)
  extends PushNotificationService
  with FirebaseMessageSerialization
  with AppLogger {

  private lazy val messaging: FirebaseMessaging = FirebaseMessaging.getInstance(firebaseApp)

  override def sendMeetupReminderNotification(
    meetupReminderMessage: MeetupReminderMessage
  ): Future[PushNotificationResponse] = {
    for {
      deviceList <- notificationDeviceRepository.getDevicesForUser(meetupReminderMessage.targetUser)
      deviceMessages = deviceList.map(createMeetupReminder(_, meetupReminderMessage))
      devicePushResponses <- Future.sequence(deviceMessages.map(sendDeviceMessage)).map(_.flatten.toList)
    } yield PushNotificationResponse(meetupReminderMessage.targetUser, devicePushResponses)
  }

  override def sendChatRoomMessageSentNotification(
    chatMessage: ChatRoomMessageSentMessage
  ): Future[PushNotificationResponse] = {
    for {
      deviceList <- notificationDeviceRepository.getDevicesForUser(chatMessage.targetUser)
      deviceMessages = deviceList.map(createChatRoomMessageSent(_, chatMessage))
      devicePushResponses <- Future.sequence(deviceMessages.map(sendDeviceMessage)).map(_.flatten.toList)
    } yield PushNotificationResponse(chatMessage.sendingUser, devicePushResponses)
  }

  override def sendUserFriendRequestNotification(
    userFriendRequest: UserFriendRequestedMessage
  ): Future[PushNotificationResponse] = {
    for {
      deviceList <- notificationDeviceRepository.getDevicesForUser(userFriendRequest.targetUser)
      deviceMessages = deviceList.map(createUserFriendRequest(_, userFriendRequest))
      devicePushResponses <- Future.sequence(deviceMessages.map(sendDeviceMessage)).map(_.flatten.toList)
    } yield PushNotificationResponse(userFriendRequest.requestingUser, devicePushResponses)
  }

  override def sendParticipantAddedToMeetupNotification(
    participantAddedToMeetupMessage: ParticipantAddedToMeetupMessage
  ): Future[PushNotificationResponse] = {
    for {
      deviceList <- notificationDeviceRepository.getDevicesForUser(participantAddedToMeetupMessage.participantId)
      deviceMessages = deviceList.map(createParticipantAddedToMeetupNotification(_, participantAddedToMeetupMessage))
      devicePushResponses <- Future.sequence(deviceMessages.map(sendDeviceMessage)).map(_.flatten.toList)
    } yield PushNotificationResponse(participantAddedToMeetupMessage.participantId, devicePushResponses)
  }

  private def sendDeviceMessage(
    deviceMessage: PushNotificationMessage
  ): Future[Option[DevicePushNotificationResponse]] =
    Future(messaging.send(deviceMessage.message))
      .map(DevicePushNotificationResponse(deviceMessage.registrationToken, _))
      .map(Some.apply)
      .recoverWith {
        case ex => cleanUpOnError(deviceMessage)(ex)
      }

  private def cleanUpOnError(
    message: PushNotificationMessage
  ): Throwable => Future[Option[DevicePushNotificationResponse]] = {
    case exception if exception.getMessage == "Requested entity was not found." =>
      logInfo(
        s"Could not send push notification to device '${message.registrationToken}', it was not found. Removing device entry."
      )
      removeDevice(message.registrationToken).map(_ => None)

    case exception if exception.getMessage == "SenderId mismatch" =>
      logWarning(
        s"Error sending push notification to device '${message.registrationToken}'- ${exception.getMessage}. Removed device entry."
      )
      removeDevice(message.registrationToken).map(_ => None)

    case exception =>
      logWarning(
        s"An unexpected error occurred while sending a notification to device ignoring. '${message.registrationToken}' - ${exception.getMessage}"
      )
      Future.successful(None)
  }

  private def removeDevice(token: String): Future[Unit] = {
    notificationDeviceRepository
      .deleteFcmToken(token)
      .recover {
        case exc: Throwable =>
          logError(s"An error occurred while trying to remove registration token '$token' from the DB - $exc")
      }
  }
}
