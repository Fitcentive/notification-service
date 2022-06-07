package io.fitcentive.notification.domain.config

import com.typesafe.config.Config
import io.fitcentive.sdk.config.PubSubTopicConfig

case class TopicsConfig(emailVerificationTokenCreatedTopic: String) extends PubSubTopicConfig {

  val topics: Seq[String] = Seq(emailVerificationTokenCreatedTopic)

}

object TopicsConfig {
  def fromConfig(config: Config): TopicsConfig =
    TopicsConfig(config.getString("email-verification-token-created"))
}
