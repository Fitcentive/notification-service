package io.fitcentive.notification.domain.pubsub.events

import java.util.UUID

case class MeetupReminderEvent(meetupId: UUID, meetupName: String, targetUser: UUID) extends EventMessage
