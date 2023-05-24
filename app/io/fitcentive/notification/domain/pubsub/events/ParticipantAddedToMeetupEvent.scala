package io.fitcentive.notification.domain.pubsub.events

import java.util.UUID

case class ParticipantAddedToMeetupEvent(meetupName: String, meetupId: UUID, ownerId: UUID, participantId: UUID)
  extends EventMessage
