package io.fitcentive.notification.domain.push.messages

import com.google.firebase.messaging.Notification
import io.fitcentive.notification.domain.push.PushNotificationEventMessage
import scala.jdk.CollectionConverters._

import java.util
import java.util.UUID

case class UserFollowRequestedMessage(requestingUser: UUID, targetUser: UUID, sendingUserProfileImageUri: String)
  extends PushNotificationEventMessage {
  val notification: Notification =
    Notification
      .builder()
      .setTitle("A user has requested to connect with you")
      .setBody("Click here to approve or deny this request")
      .build()

  def toJavaMap: util.Map[String, String] =
    Seq(
      "type" -> "user_follow_request",
      "requestingUserId" -> requestingUser.toString,
      "targetUserId" -> targetUser.toString,
      "requestingUserPhotoUrl" -> sendingUserProfileImageUri,
    ).toMap.asJava
}
