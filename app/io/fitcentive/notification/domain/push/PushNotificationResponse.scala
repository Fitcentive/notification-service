package io.fitcentive.notification.domain.push

import java.util.UUID

case class PushNotificationResponse(userId: UUID, results: List[DevicePushNotificationResponse])
