package io.fitcentive.notification.services

import com.google.inject.ImplementedBy
import io.fitcentive.notification.domain.push.messages.{ChatRoomMessageSentMessage, UserFollowRequestedMessage}
import io.fitcentive.notification.domain.push.PushNotificationResponse
import io.fitcentive.notification.infrastructure.firebase.FirebaseCloudMessagingService

import scala.concurrent.Future

@ImplementedBy(classOf[FirebaseCloudMessagingService])
trait PushNotificationService {
  def sendUserFollowRequestNotification(userFollowRequest: UserFollowRequestedMessage): Future[PushNotificationResponse]
  def sendChatRoomMessageSentNotification(
    userFollowRequest: ChatRoomMessageSentMessage
  ): Future[PushNotificationResponse]
}
