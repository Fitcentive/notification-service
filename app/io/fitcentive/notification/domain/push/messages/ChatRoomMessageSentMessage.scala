package io.fitcentive.notification.domain.push.messages

import com.google.firebase.messaging.Notification
import io.fitcentive.notification.domain.push.PushNotificationEventMessage
import scala.jdk.CollectionConverters._

import java.util
import java.util.UUID

case class ChatRoomMessageSentMessage(
  sendingUser: UUID,
  targetUser: UUID,
  roomId: UUID,
  sendingUserFirstName: String,
  sendingUserLastName: String,
  targetUserProfileImageUri: String,
  sendingUserProfileImageUri: String,
  message: String
) extends PushNotificationEventMessage {

  val notification: Notification =
    Notification
      .builder()
      .setTitle(s"$sendingUserFirstName $sendingUserLastName")
      .setBody(message)
      .setImage(targetUserProfileImageUri)
      .build()

  def toJavaMap: util.Map[String, String] =
    Seq(
      "type" -> "chat_message",
      "roomId" -> roomId.toString,
      "targetUserId" -> targetUser.toString,
      "sendingUserId" -> sendingUser.toString,
      "sendingUserPhotoUrl" -> sendingUserProfileImageUri,
    ).toMap.asJava
}
