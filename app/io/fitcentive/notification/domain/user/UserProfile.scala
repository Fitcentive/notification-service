package io.fitcentive.notification.domain.user

import play.api.libs.json.{Json, Reads, Writes}

import java.time.LocalDate
import java.util.UUID

case class UserProfile(
  userId: UUID,
  firstName: Option[String],
  lastName: Option[String],
  photoUrl: Option[String],
  dateOfBirth: Option[LocalDate]
)

object UserProfile {
  implicit lazy val writes: Writes[UserProfile] = Json.writes[UserProfile]
  implicit lazy val reads: Reads[UserProfile] = Json.reads[UserProfile]
}
