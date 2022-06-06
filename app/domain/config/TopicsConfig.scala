package domain.config

import com.typesafe.config.Config

case class TopicsConfig(emailVerificationTokenCreatedTopic: String) {

  val topics: Seq[String] = Seq(emailVerificationTokenCreatedTopic)

}

object TopicsConfig {
  def fromConfig(config: Config): TopicsConfig =
    TopicsConfig(config.getString("email-verification-token-created"))
}
