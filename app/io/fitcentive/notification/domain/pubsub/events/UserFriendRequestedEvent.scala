package io.fitcentive.notification.domain.pubsub.events

import java.util.UUID

case class UserFriendRequestedEvent(requestingUser: UUID, targetUser: UUID) extends EventMessage
