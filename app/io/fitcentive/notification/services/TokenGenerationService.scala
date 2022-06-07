package io.fitcentive.notification.services

import com.google.inject.ImplementedBy
import io.fitcentive.notification.domain.email.EmailVerificationToken
import io.fitcentive.notification.infrastructure.verification.VerificationTokenGenerationService

import java.util.UUID

@ImplementedBy(classOf[VerificationTokenGenerationService])
trait TokenGenerationService {
  def generateEmailVerificationToken(userId: UUID, emailId: String): EmailVerificationToken
}
