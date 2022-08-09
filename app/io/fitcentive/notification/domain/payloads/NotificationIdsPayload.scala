package io.fitcentive.notification.domain.payloads

import play.api.libs.json.{Json, Reads, Writes}

import java.util.UUID

case class NotificationIdsPayload(notificationIds: Seq[UUID])

object NotificationIdsPayload {
  implicit lazy val writes: Writes[NotificationIdsPayload] = Json.writes[NotificationIdsPayload]
  implicit lazy val reads: Reads[NotificationIdsPayload] = Json.reads[NotificationIdsPayload]
}
