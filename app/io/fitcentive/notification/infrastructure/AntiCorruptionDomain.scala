package io.fitcentive.notification.infrastructure

import io.fitcentive.notification.domain.events.EmailVerificationTokenCreatedEvent
import io.fitcentive.registry.events.email.EmailVerificationTokenCreated

trait AntiCorruptionDomain {

  implicit class EmailVerificationTokenCreatedEvent2Domain(event: EmailVerificationTokenCreated) {
    def toDomain: EmailVerificationTokenCreatedEvent =
      EmailVerificationTokenCreatedEvent(event.emailId, event.token, event.expiry)
  }

}