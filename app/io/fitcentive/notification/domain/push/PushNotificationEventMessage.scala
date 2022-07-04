package io.fitcentive.notification.domain.push

import com.google.firebase.messaging.Notification

trait PushNotificationEventMessage {
  def notification: Notification
}
