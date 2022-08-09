package io.fitcentive.notification.domain.config

import com.typesafe.config.Config
import io.fitcentive.sdk.config.PubSubSubscriptionConfig

case class SubscriptionsConfig(
  emailVerificationTokenCreatedSubscription: String,
  userFollowRequestedSubscription: String,
  userFollowRequestDecisionSubscription: String,
  userCommentedOnPostSubscription: String,
  userLikedPostSubscription: String,
  chatRoomMessageSentSubscription: String
) extends PubSubSubscriptionConfig {

  val subscriptions: Seq[String] = Seq(
    emailVerificationTokenCreatedSubscription,
    userFollowRequestedSubscription,
    userFollowRequestDecisionSubscription,
    chatRoomMessageSentSubscription,
    userCommentedOnPostSubscription,
    userLikedPostSubscription,
  )
}

object SubscriptionsConfig {
  def fromConfig(config: Config): SubscriptionsConfig =
    SubscriptionsConfig(
      emailVerificationTokenCreatedSubscription = config.getString("email-verification-token-created"),
      userFollowRequestedSubscription = config.getString("user-follow-requested"),
      userFollowRequestDecisionSubscription = config.getString("user-follow-request-decision"),
      userCommentedOnPostSubscription = config.getString("user-commented-on-post"),
      userLikedPostSubscription = config.getString("user-liked-post"),
      chatRoomMessageSentSubscription = config.getString("chat-room-message-sent"),
    )
}
