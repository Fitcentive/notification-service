package io.fitcentive.notification.domain.push.messages

import com.google.firebase.messaging.Notification
import io.fitcentive.notification.domain.push.PushNotificationEventMessage

import scala.jdk.CollectionConverters._
import java.util
import java.util.UUID

case class MeetupReminderMessage(targetUser: UUID, meetupId: UUID, meetupName: String)
  extends PushNotificationEventMessage {

  val notification: Notification =
    Notification
      .builder()
      .setTitle(s"$meetupName is about to start soon!")
      .setBody("Click here to view meetup")
      .build()

  def toJavaMap: util.Map[String, String] =
    Seq("type" -> "meetup_reminder", "meetupId" -> meetupId.toString, "targetUser" -> targetUser.toString).toMap.asJava
}
