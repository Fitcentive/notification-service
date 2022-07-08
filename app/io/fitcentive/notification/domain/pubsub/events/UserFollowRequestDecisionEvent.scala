package io.fitcentive.notification.domain.pubsub.events

import java.util.UUID

case class UserFollowRequestDecisionEvent(targetUser: UUID, isApproved: Boolean) extends EventMessage
