package io.fitcentive.notification.domain.push.messages

import com.google.firebase.messaging.Notification
import io.fitcentive.notification.domain.push.PushNotificationEventMessage
import scala.jdk.CollectionConverters._

import java.util
import java.util.UUID

case class ParticipantAddedToMeetupMessage(
  meetupId: UUID,
  meetupOwnerId: UUID,
  participantId: UUID,
  meetupOwnerPhotoUrl: String
) extends PushNotificationEventMessage {
  val notification: Notification =
    Notification
      .builder()
      .setTitle("You have been added to a meetup!")
      .setBody("Click here to view more")
      .build()

  def toJavaMap: util.Map[String, String] =
    Seq(
      "type" -> "participant_added_to_meetup",
      "meetupOwnerId" -> meetupOwnerId.toString,
      "meetupId" -> meetupId.toString,
      "participantId" -> participantId.toString,
      "meetupOwnerPhotoUrl" -> meetupOwnerPhotoUrl,
    ).toMap.asJava
}
