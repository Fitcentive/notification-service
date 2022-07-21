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
  targetUserFirstName: String,
  targetUserLastName: String,
  targetUserProfileImageUri: String,
  message: String
) extends PushNotificationEventMessage {

  val notification: Notification =
    Notification
      .builder()
      .setTitle(s"$targetUserFirstName $targetUserLastName")
      .setBody(message)
      .setImage(targetUserProfileImageUri)
      .build()

  def toJavaMap: util.Map[String, String] =
    Seq("type" -> "chat_message", "roomId" -> roomId.toString, "targetUserId" -> targetUser.toString).toMap.asJava
}
