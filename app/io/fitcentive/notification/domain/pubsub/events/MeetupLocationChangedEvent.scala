package io.fitcentive.notification.domain.pubsub.events

import java.util.UUID

case class MeetupLocationChangedEvent(meetupId: UUID, meetupOwnerId: UUID, meetupName: String, targetUser: UUID)
  extends EventMessage
