package io.fitcentive.notification.domain.config

import com.typesafe.config.Config
import io.fitcentive.sdk.config.PubSubTopicConfig

case class TopicsConfig(emailVerificationTokenCreatedTopic: String, userFollowRequestedTopic: String)
  extends PubSubTopicConfig {

  val topics: Seq[String] = Seq(emailVerificationTokenCreatedTopic, userFollowRequestedTopic)

}

object TopicsConfig {
  def fromConfig(config: Config): TopicsConfig =
    TopicsConfig(config.getString("email-verification-token-created"), config.getString("user-follow-requested"))
}
