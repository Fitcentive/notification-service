package io.fitcentive.notification.domain.pubsub.events

import java.util.UUID

case class UserCommentedOnPostEvent(commentingUser: UUID, targetUser: UUID, postId: UUID) extends EventMessage
