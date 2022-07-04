package io.fitcentive.notification.domain.push

import com.google.firebase.messaging.Message

case class PushNotificationMessage(registrationToken: String, message: Message)
