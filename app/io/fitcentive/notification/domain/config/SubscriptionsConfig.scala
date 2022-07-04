package io.fitcentive.notification.domain.config

import com.typesafe.config.Config
import io.fitcentive.sdk.config.PubSubSubscriptionConfig

case class SubscriptionsConfig(
  emailVerificationTokenCreatedSubscription: String,
  userFollowRequestedSubscription: String
) extends PubSubSubscriptionConfig {

  val subscriptions: Seq[String] = Seq(emailVerificationTokenCreatedSubscription, userFollowRequestedSubscription)
}

object SubscriptionsConfig {
  def fromConfig(config: Config): SubscriptionsConfig =
    SubscriptionsConfig(
      emailVerificationTokenCreatedSubscription = config.getString("email-verification-token-created"),
      userFollowRequestedSubscription = config.getString("user-follow-requested")
    )
}
