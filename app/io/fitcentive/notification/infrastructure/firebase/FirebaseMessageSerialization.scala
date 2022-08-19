package io.fitcentive.notification.infrastructure.firebase

import com.google.firebase.messaging.{
  AndroidConfig,
  AndroidNotification,
  ApnsConfig,
  ApnsFcmOptions,
  Aps,
  FcmOptions,
  Message
}
import io.fitcentive.notification.domain.push.{NotificationDevice, PushNotificationMessage}
import io.fitcentive.notification.domain.push.messages.{ChatRoomMessageSentMessage, UserFollowRequestedMessage}

trait FirebaseMessageSerialization {
  private val notificationSound = "notification1.mp3"
  private val appBasicTheme = "#009688"

  def createChatRoomMessageSent(
    device: NotificationDevice,
    chatMessage: ChatRoomMessageSentMessage
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
              // todo - this fails unless URL is publicly accessible
              .setImage(chatMessage.sendingUserProfileImageUri)
              .setIcon("app_icon")
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
              .setMutableContent(true)
              .build()
          )
          .setFcmOptions(
            ApnsFcmOptions
              .builder()
              // todo - this fails unless URL is publicly accessible
              .setImage(chatMessage.sendingUserProfileImageUri)
              .build()
          )
          .build()
      )
      .setNotification(chatMessage.notification)
      .putAllData(chatMessage.toJavaMap)
      .setToken(device.registrationToken)
      .build()

    PushNotificationMessage(device.registrationToken, msg)
  }

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
              // todo - this fails unless URL is publicly accessible
              .setImage(userFollowRequest.sendingUserProfileImageUri)
              .setIcon("app_icon")
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
              .setMutableContent(true)
              .build()
          )
          .setFcmOptions(
            ApnsFcmOptions
              .builder()
              // todo - this fails unless URL is publicly accessible
              .setImage(userFollowRequest.sendingUserProfileImageUri)
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
