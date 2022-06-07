package io.fitcentive.notification.domain.errors
import io.fitcentive.sdk.error.DomainError

import java.util.UUID

sealed trait EmailError extends DomainError

case class EmailValidationError(reason: String) extends EmailError {
  override def code: UUID = UUID.fromString("df13e68b-fcdd-4313-9693-3130e65467c2")
}

case class EmailDeliveryError(reason: String) extends EmailError {
  override def code: UUID = UUID.fromString("60c3c673-1462-4b4e-9f3e-2bec7e8f247e")
}
