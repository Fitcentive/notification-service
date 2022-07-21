package io.fitcentive.notification.domain.config

import com.typesafe.config.Config
import io.fitcentive.sdk.config.PubSubTopicConfig

case class TopicsConfig(
  emailVerificationTokenCreatedTopic: String,
  userFollowRequestedTopic: String,
  userFollowRequestDecisionTopic: String,
  chatRoomMessageSentTopic: String
) extends PubSubTopicConfig {

  val topics: Seq[String] =
    Seq(
      emailVerificationTokenCreatedTopic,
      userFollowRequestedTopic,
      userFollowRequestDecisionTopic,
      chatRoomMessageSentTopic
    )

}

object TopicsConfig {
  def fromConfig(config: Config): TopicsConfig =
    TopicsConfig(
      config.getString("email-verification-token-created"),
      config.getString("user-follow-requested"),
      config.getString("user-follow-request-decision"),
      config.getString("chat-room-message-sent"),
    )
}
