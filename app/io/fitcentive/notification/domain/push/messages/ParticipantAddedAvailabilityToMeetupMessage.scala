package io.fitcentive.notification.domain.push.messages

import com.google.firebase.messaging.Notification
import io.fitcentive.notification.domain.push.PushNotificationEventMessage
import scala.jdk.CollectionConverters._

import java.util
import java.util.UUID

case class ParticipantAddedAvailabilityToMeetupMessage(
  meetupId: UUID,
  meetupOwnerId: UUID,
  targetUserId: UUID,
  participantName: String,
  participantId: UUID,
  participantPhotoUrl: String
) extends PushNotificationEventMessage {
  val notification: Notification =
    Notification
      .builder()
      .setTitle(s"$participantName has added their availability!")
      .setBody("Click here to view more")
      .build()

  def toJavaMap: util.Map[String, String] =
    Seq(
      "type" -> "participant_added_availability_to_meetup",
      "meetupId" -> meetupId.toString,
      "participantId" -> participantId.toString,
      "participantPhotoUrl" -> participantPhotoUrl,
      "participantName" -> participantName,
      "meetupOwnerId" -> meetupOwnerId.toString,
      "targetUserId" -> targetUserId.toString
    ).toMap.asJava
}
