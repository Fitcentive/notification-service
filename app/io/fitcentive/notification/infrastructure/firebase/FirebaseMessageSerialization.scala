package io.fitcentive.notification.infrastructure.firebase

import com.google.firebase.messaging.{AndroidConfig, AndroidNotification, ApnsConfig, Aps, Message}
import io.fitcentive.notification.domain.push.{NotificationDevice, PushNotificationMessage}
import io.fitcentive.notification.domain.push.messages.UserFollowRequestedMessage

trait FirebaseMessageSerialization {
  private val notificationSound = "notification1.mp3"
  private val appBasicTheme = "#009688"

  def createUserFollowRequest(
    device: NotificationDevice,
    userFollowRequest: UserFollowRequestedMessage
  ): PushNotificationMessage = {
    val msg = Message
      .builder()
      .setAndroidConfig(
        AndroidConfig
          .builder()
          .setNotification(
            AndroidNotification
              .builder()
              .setSound(notificationSound)
              .setColor(appBasicTheme)
              .build()
          )
          .build()
      )
      .setApnsConfig(
        ApnsConfig
          .builder()
          .setAps(
            Aps
              .builder()
              .setSound(notificationSound)
              .build()
          )
          .build()
      )
      .setNotification(userFollowRequest.notification)
      .putAllData(userFollowRequest.toJavaMap)
      .setToken(device.registrationToken)
      .build()

    PushNotificationMessage(device.registrationToken, msg)
  }

}
