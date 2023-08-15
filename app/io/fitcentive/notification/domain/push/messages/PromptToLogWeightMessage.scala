package io.fitcentive.notification.domain.push.messages

import com.google.firebase.messaging.Notification
import io.fitcentive.notification.domain.push.PushNotificationEventMessage

import scala.jdk.CollectionConverters._
import java.util
import java.util.UUID

case class PromptToLogWeightMessage(targetUser: UUID) extends PushNotificationEventMessage {

  val notification: Notification =
    Notification
      .builder()
      .setTitle(s"You have not logged your weight for today")
      .setBody("Tap here to do so!")
      .build()

  def toJavaMap: util.Map[String, String] =
    Seq("type" -> "weight_log_reminder", "targetUser" -> targetUser.toString).toMap.asJava
}
