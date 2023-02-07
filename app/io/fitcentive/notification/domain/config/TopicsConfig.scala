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
  participantAddedToMeetupTopic: String,
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
      participantAddedToMeetupTopic,
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
      config.getString("participant-added-to-meetup"),
    )
}
