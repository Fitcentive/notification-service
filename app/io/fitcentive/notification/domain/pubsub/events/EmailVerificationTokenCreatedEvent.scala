package io.fitcentive.notification.domain.pubsub.events

case class EmailVerificationTokenCreatedEvent(emailId: String, token: String, expiry: Option[Long]) extends EventMessage
