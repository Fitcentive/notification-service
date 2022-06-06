package services

import com.google.inject.ImplementedBy
import domain.email.EmailVerificationToken
import infrastructure.verification.VerificationTokenGenerationService

import java.util.UUID

@ImplementedBy(classOf[VerificationTokenGenerationService])
trait TokenGenerationService {
  def generateEmailVerificationToken(userId: UUID, emailId: String): EmailVerificationToken
}
