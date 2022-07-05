package io.fitcentive.notification.domain.notification

import play.api.libs.json.{JsValue, Json, Reads, Writes}

import java.time.Instant
import java.util.UUID

case class NotificationData(
  id: UUID,
  targetUser: UUID,
  notificationType: NotificationType,
  isInteractive: Boolean,
  hasBeenInteractedWith: Boolean,
  data: JsValue,
  createdAt: Instant,
  updatedAt: Instant
)

object NotificationData {
  implicit lazy val writes: Writes[NotificationData] = Json.writes[NotificationData]
  implicit lazy val reads: Reads[NotificationData] = Json.reads[NotificationData]

  case class Upsert(id: UUID, targetUser: UUID, isInteractive: Boolean, hasBeenInteractedWith: Boolean, data: JsValue)
  object Upsert {
    implicit lazy val writes: Writes[Upsert] = Json.writes[Upsert]
    implicit lazy val reads: Reads[Upsert] = Json.reads[Upsert]
  }

  case class Patch(hasBeenInteractedWith: Boolean, data: JsValue)
  object Patch {
    implicit lazy val writes: Writes[Patch] = Json.writes[Patch]
    implicit lazy val reads: Reads[Patch] = Json.reads[Patch]
  }
}
