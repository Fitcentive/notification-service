package io.fitcentive.notification.domain.config

import com.typesafe.config.Config
import io.fitcentive.sdk.config.PubSubSubscriptionConfig

case class SubscriptionsConfig(
  emailVerificationTokenCreatedSubscription: String,
  userFriendRequestedSubscription: String,
  userFriendRequestDecisionSubscription: String,
  userCommentedOnPostSubscription: String,
  userLikedPostSubscription: String,
  chatRoomMessageSentSubscription: String,
  meetupDecisionSubscription: String,
  meetupReminderSubscription: String,
  meetupLocationChangedSubscription: String,
  participantAddedToMeetupSubscription: String,
  participantAddedAvailabilityToMeetupSubscription: String,
  flushStaleNotificationsSubscription: String,
) extends PubSubSubscriptionConfig {

  val subscriptions: Seq[String] = Seq(
    emailVerificationTokenCreatedSubscription,
    userFriendRequestedSubscription,
    userFriendRequestDecisionSubscription,
    chatRoomMessageSentSubscription,
    userCommentedOnPostSubscription,
    userLikedPostSubscription,
    meetupDecisionSubscription,
    meetupReminderSubscription,
    meetupLocationChangedSubscription,
    participantAddedToMeetupSubscription,
    participantAddedAvailabilityToMeetupSubscription,
    flushStaleNotificationsSubscription
  )
}

object SubscriptionsConfig {
  def fromConfig(config: Config): SubscriptionsConfig =
    SubscriptionsConfig(
      emailVerificationTokenCreatedSubscription = config.getString("email-verification-token-created"),
      userFriendRequestedSubscription = config.getString("user-friend-requested"),
      userFriendRequestDecisionSubscription = config.getString("user-friend-request-decision"),
      userCommentedOnPostSubscription = config.getString("user-commented-on-post"),
      userLikedPostSubscription = config.getString("user-liked-post"),
      chatRoomMessageSentSubscription = config.getString("chat-room-message-sent"),
      meetupDecisionSubscription = config.getString("meetup-decision"),
      meetupReminderSubscription = config.getString("meetup-reminder"),
      meetupLocationChangedSubscription = config.getString("meetup-location-changed"),
      participantAddedToMeetupSubscription = config.getString("participant-added-to-meetup"),
      participantAddedAvailabilityToMeetupSubscription = config.getString("participant-added-availability-to-meetup"),
      flushStaleNotificationsSubscription = config.getString("flush-stale-notifications"),
    )
}
