package io.fitcentive.notification.domain.pubsub.events

import java.util.UUID

case class UserLikedPostEvent(likingUser: UUID, targetUser: UUID, postId: UUID) extends EventMessage
