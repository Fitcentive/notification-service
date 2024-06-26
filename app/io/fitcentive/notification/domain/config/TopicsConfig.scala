package io.fitcentive.notification.domain.config

import com.typesafe.config.Config
import io.fitcentive.sdk.config.PubSubTopicConfig

case class TopicsConfig(
  emailVerificationTokenCreatedTopic: String,
  userFriendRequestedTopic: String,
  userFriendRequestDecisionTopic: String,
  userCommentedOnPostTopic: String,
  userLikedPostTopic: String,
  chatRoomMessageSentTopic: String,
  meetupDecisionTopic: String,
  meetupReminderTopic: String,
  meetupLocationChangedTopic: String,
  participantAddedToMeetupTopic: String,
  participantAddedAvailabilityToMeetupTopic: String,
  flushStaleNotificationsTopic: String,
  userAttainedNewAchievementMilestoneTopic: String,
  promptUserToLogWeightTopic: String,
  promptUserToLogDiaryEntryTopic: String,
) extends PubSubTopicConfig {

  val topics: Seq[String] =
    Seq(
      emailVerificationTokenCreatedTopic,
      userFriendRequestedTopic,
      userFriendRequestDecisionTopic,
      chatRoomMessageSentTopic,
      userCommentedOnPostTopic,
      userLikedPostTopic,
      meetupDecisionTopic,
      meetupReminderTopic,
      meetupLocationChangedTopic,
      participantAddedToMeetupTopic,
      participantAddedAvailabilityToMeetupTopic,
      flushStaleNotificationsTopic,
      userAttainedNewAchievementMilestoneTopic,
      promptUserToLogWeightTopic,
      promptUserToLogDiaryEntryTopic,
    )

}

object TopicsConfig {
  def fromConfig(config: Config): TopicsConfig =
    TopicsConfig(
      config.getString("email-verification-token-created"),
      config.getString("user-friend-requested"),
      config.getString("user-friend-request-decision"),
      config.getString("user-commented-on-post"),
      config.getString("user-liked-post"),
      config.getString("chat-room-message-sent"),
      config.getString("meetup-decision"),
      config.getString("meetup-reminder"),
      config.getString("meetup-location-changed"),
      config.getString("participant-added-to-meetup"),
      config.getString("participant-added-availability-to-meetup"),
      config.getString("flush-stale-notifications"),
      config.getString("user-attained-new-achievement-milestone"),
      config.getString("prompt-user-to-log-weight"),
      config.getString("prompt-user-to-log-diary-entry"),
    )
}
