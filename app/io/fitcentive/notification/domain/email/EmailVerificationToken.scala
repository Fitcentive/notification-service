package io.fitcentive.notification.domain.email

import java.util.UUID

case class EmailVerificationToken(userId: UUID, emailId: String, token: String, expiry: Long)
