package io.fitcentive.notification.domain.pubsub.events

import java.util.UUID

case class MeetupDecisionEvent(meetupId: UUID, meetupOwnerId: UUID, participantId: UUID, hasAccepted: Boolean)
  extends EventMessage
