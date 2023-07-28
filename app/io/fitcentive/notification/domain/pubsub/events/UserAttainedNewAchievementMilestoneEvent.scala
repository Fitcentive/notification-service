package io.fitcentive.notification.domain.pubsub.events

import java.util.UUID

case class UserAttainedNewAchievementMilestoneEvent(
  userId: UUID,
  milestoneName: String,
  milestoneCategory: String,
  attainedAtInMillis: Long
) extends EventMessage
