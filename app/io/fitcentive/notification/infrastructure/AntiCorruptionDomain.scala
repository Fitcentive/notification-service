package io.fitcentive.notification.infrastructure

import io.fitcentive.notification.domain.pubsub.events.{EmailVerificationTokenCreatedEvent, UserFollowRequestedEvent}
import io.fitcentive.registry.events.email.EmailVerificationTokenCreated
import io.fitcentive.registry.events.push.UserFollowRequested

trait AntiCorruptionDomain {

  implicit class EmailVerificationTokenCreatedEvent2Domain(event: EmailVerificationTokenCreated) {
    def toDomain: EmailVerificationTokenCreatedEvent =
      EmailVerificationTokenCreatedEvent(event.emailId, event.token, event.expiry)
  }

  implicit class UserFollowRequestedEvent2Domain(event: UserFollowRequested) {
    def toDomain: UserFollowRequestedEvent =
      UserFollowRequestedEvent(event.requestingUser, event.targetUser)
  }

}
