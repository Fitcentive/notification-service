package io.fitcentive.notification.services

import com.google.inject.ImplementedBy
import io.fitcentive.notification.domain.push.messages.{ChatRoomMessageSentMessage, UserFriendRequestedMessage}
import io.fitcentive.notification.domain.push.PushNotificationResponse
import io.fitcentive.notification.infrastructure.firebase.FirebaseCloudMessagingService

import scala.concurrent.Future

@ImplementedBy(classOf[FirebaseCloudMessagingService])
trait PushNotificationService {
  def sendUserFriendRequestNotification(userFriendRequest: UserFriendRequestedMessage): Future[PushNotificationResponse]
  def sendChatRoomMessageSentNotification(chatMessage: ChatRoomMessageSentMessage): Future[PushNotificationResponse]
}
