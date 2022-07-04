package io.fitcentive.notification.domain.push

import play.api.libs.json.{Json, Reads, Writes}

import java.util.UUID

case class NotificationDevice(
  userId: UUID,
  registrationToken: String,
  manufacturer: String,
  model: String,
  isPhysicalDevice: Boolean
)

object NotificationDevice {
  implicit lazy val writes: Writes[NotificationDevice] = Json.writes[NotificationDevice]
  implicit lazy val reads: Reads[NotificationDevice] = Json.reads[NotificationDevice]
}
