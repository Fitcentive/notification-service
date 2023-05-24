package io.fitcentive.notification.domain.pubsub.events

import java.util.UUID

case class ParticipantAddedAvailabilityToMeetupEvent(
  meetupName: String,
  meetupId: UUID,
  meetupOwnerId: UUID,
  participantId: UUID,
  targetUserId: UUID
) extends EventMessage
