package io.fitcentive.notification.services

import com.google.inject.ImplementedBy
import io.fitcentive.notification.domain.push.messages.{
  ChatRoomMessageSentMessage,
  MeetupReminderMessage,
  ParticipantAddedAvailabilityToMeetupMessage,
  ParticipantAddedToMeetupMessage,
  UserFriendRequestedMessage
}
import io.fitcentive.notification.domain.push.PushNotificationResponse
import io.fitcentive.notification.infrastructure.firebase.FirebaseCloudMessagingService

import scala.concurrent.Future

@ImplementedBy(classOf[FirebaseCloudMessagingService])
trait PushNotificationService {
  def sendParticipantAddedToMeetupNotification(
    participantAddedToMeetupMessage: ParticipantAddedToMeetupMessage
  ): Future[PushNotificationResponse]
  def sendParticipantAddedAvailabilityToMeetupNotification(
    participantAddedAvailabilityToMeetupMessage: ParticipantAddedAvailabilityToMeetupMessage
  ): Future[PushNotificationResponse]
  def sendUserFriendRequestNotification(userFriendRequest: UserFriendRequestedMessage): Future[PushNotificationResponse]
  def sendChatRoomMessageSentNotification(chatMessage: ChatRoomMessageSentMessage): Future[PushNotificationResponse]
  def sendMeetupReminderNotification(chatMessage: MeetupReminderMessage): Future[PushNotificationResponse]
}
