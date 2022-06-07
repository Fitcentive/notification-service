package io.fitcentive.notification.domain.config

import com.typesafe.config.Config

case class SubscriptionsConfig(emailVerificationTokenCreatedSubscription: String)

object SubscriptionsConfig {
  def fromConfig(config: Config): SubscriptionsConfig =
    SubscriptionsConfig(emailVerificationTokenCreatedSubscription =
      config.getString("email-verification-token-created")
    )
}
