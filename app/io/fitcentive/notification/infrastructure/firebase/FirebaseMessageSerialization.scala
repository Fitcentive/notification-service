package io.fitcentive.notification.infrastructure.firebase

import com.google.firebase.messaging.{AndroidConfig, AndroidNotification, ApnsConfig, ApnsFcmOptions, Aps, Message}
import io.fitcentive.notification.domain.push.{NotificationDevice, PushNotificationMessage}
import io.fitcentive.notification.domain.push.messages.{
  ChatRoomMessageSentMessage,
  MeetupReminderMessage,
  ParticipantAddedAvailabilityToMeetupMessage,
  ParticipantAddedToMeetupMessage,
  PromptToLogWeightMessage,
  UserAttainedNewAchievementMilestoneMessage,
  UserFriendRequestedMessage
}

trait FirebaseMessageSerialization {
  private val notificationSound = "notification1.mp3"
  private val appBasicTheme = "#009688"

  def createUserAttainedNewAchievementMilestone(
    device: NotificationDevice,
    milestoneMessage: UserAttainedNewAchievementMilestoneMessage
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
              .build()
          )
          .build()
      )
      .setNotification(milestoneMessage.notification)
      .putAllData(milestoneMessage.toJavaMap)
      .setToken(device.registrationToken)
      .build()

    PushNotificationMessage(device.registrationToken, msg)
  }

  def createMeetupReminder(
    device: NotificationDevice,
    meetupReminderMessage: MeetupReminderMessage
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
              .build()
          )
          .build()
      )
      .setNotification(meetupReminderMessage.notification)
      .putAllData(meetupReminderMessage.toJavaMap)
      .setToken(device.registrationToken)
      .build()

    PushNotificationMessage(device.registrationToken, msg)
  }

  def createWeightLogReminder(
    device: NotificationDevice,
    message: PromptToLogWeightMessage
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
              .build()
          )
          .build()
      )
      .setNotification(message.notification)
      .putAllData(message.toJavaMap)
      .setToken(device.registrationToken)
      .build()

    PushNotificationMessage(device.registrationToken, msg)
  }

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

  def createUserFriendRequest(
    device: NotificationDevice,
    userFriendRequest: UserFriendRequestedMessage
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
              .setImage(userFriendRequest.sendingUserProfileImageUri)
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
              .setImage(userFriendRequest.sendingUserProfileImageUri)
              .build()
          )
          .build()
      )
      .setNotification(userFriendRequest.notification)
      .putAllData(userFriendRequest.toJavaMap)
      .setToken(device.registrationToken)
      .build()

    PushNotificationMessage(device.registrationToken, msg)
  }

  def createParticipantAddedToMeetupNotification(
    device: NotificationDevice,
    participantAddedToMeetupMessage: ParticipantAddedToMeetupMessage
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
              .setImage(participantAddedToMeetupMessage.meetupOwnerPhotoUrl)
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
              .setImage(participantAddedToMeetupMessage.meetupOwnerPhotoUrl)
              .build()
          )
          .build()
      )
      .setNotification(participantAddedToMeetupMessage.notification)
      .putAllData(participantAddedToMeetupMessage.toJavaMap)
      .setToken(device.registrationToken)
      .build()

    PushNotificationMessage(device.registrationToken, msg)
  }

  def createParticipantAddedAvailabilityToMeetupNotification(
    device: NotificationDevice,
    participantAddedAvailabilityToMeetupMessage: ParticipantAddedAvailabilityToMeetupMessage
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
              .setImage(participantAddedAvailabilityToMeetupMessage.participantPhotoUrl)
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
              .setImage(participantAddedAvailabilityToMeetupMessage.participantPhotoUrl)
              .build()
          )
          .build()
      )
      .setNotification(participantAddedAvailabilityToMeetupMessage.notification)
      .putAllData(participantAddedAvailabilityToMeetupMessage.toJavaMap)
      .setToken(device.registrationToken)
      .build()

    PushNotificationMessage(device.registrationToken, msg)
  }

}
