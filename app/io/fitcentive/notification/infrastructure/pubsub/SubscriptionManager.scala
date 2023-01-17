package io.fitcentive.notification.infrastructure.pubsub

import io.fitcentive.notification.domain.config.AppPubSubConfig
import io.fitcentive.notification.domain.pubsub.events.EventHandlers
import io.fitcentive.notification.infrastructure.AntiCorruptionDomain
import io.fitcentive.notification.infrastructure.contexts.PubSubExecutionContext
import io.fitcentive.registry.events.email.EmailVerificationTokenCreated
import io.fitcentive.registry.events.push.{ChatRoomMessageSent, UserFriendRequested}
import io.fitcentive.registry.events.social.{UserCommentedOnPost, UserLikedPost}
import io.fitcentive.registry.events.user.UserFriendRequestDecision
import io.fitcentive.sdk.gcp.pubsub.{PubSubPublisher, PubSubSubscriber}
import io.fitcentive.sdk.logging.AppLogger

import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps

class SubscriptionManager(
  publisher: PubSubPublisher,
  subscriber: PubSubSubscriber,
  config: AppPubSubConfig,
  environment: String
)(implicit ec: PubSubExecutionContext)
  extends AppLogger
  with AntiCorruptionDomain {

  self: EventHandlers =>

  initializeSubscriptions

  final def initializeSubscriptions: Future[Unit] = {
    for {
      _ <- Future.sequence(config.topicsConfig.topics.map(publisher.createTopic))
      _ <- subscribeToEmailVerificationTokenCreatedEvent
      _ <- subscribeToUserFriendRequestedEvent
      _ <- subscribeToUserFriendRequestDecisionEvent
      _ <- subscribeToChatRoomMessageSentEvent
      _ <- subscribeToUserCommentedOnPostEvent
      _ <- subscribeToUserLikedPostEvent
      _ = logInfo("Subscriptions set up successfully!")
    } yield ()
  }

  private def subscribeToEmailVerificationTokenCreatedEvent: Future[Unit] =
    subscriber
      .subscribe[EmailVerificationTokenCreated](
        environment,
        config.subscriptionsConfig.emailVerificationTokenCreatedSubscription,
        config.topicsConfig.emailVerificationTokenCreatedTopic
      )(_.payload.toDomain.pipe(handleEvent))

  private def subscribeToUserFriendRequestedEvent: Future[Unit] =
    subscriber
      .subscribe[UserFriendRequested](
        environment,
        config.subscriptionsConfig.userFriendRequestedSubscription,
        config.topicsConfig.userFriendRequestedTopic
      )(_.payload.toDomain.pipe(handleEvent))

  private def subscribeToUserFriendRequestDecisionEvent: Future[Unit] =
    subscriber
      .subscribe[UserFriendRequestDecision](
        environment,
        config.subscriptionsConfig.userFriendRequestDecisionSubscription,
        config.topicsConfig.userFriendRequestDecisionTopic
      )(_.payload.toDomain.pipe(handleEvent))

  private def subscribeToChatRoomMessageSentEvent: Future[Unit] =
    subscriber
      .subscribe[ChatRoomMessageSent](
        environment,
        config.subscriptionsConfig.chatRoomMessageSentSubscription,
        config.topicsConfig.chatRoomMessageSentTopic
      )(_.payload.toDomain.pipe(handleEvent))

  private def subscribeToUserCommentedOnPostEvent: Future[Unit] =
    subscriber
      .subscribe[UserCommentedOnPost](
        environment,
        config.subscriptionsConfig.userCommentedOnPostSubscription,
        config.topicsConfig.userCommentedOnPostTopic
      )(_.payload.toDomain.pipe(handleEvent))

  private def subscribeToUserLikedPostEvent: Future[Unit] =
    subscriber
      .subscribe[UserLikedPost](
        environment,
        config.subscriptionsConfig.userLikedPostSubscription,
        config.topicsConfig.userLikedPostTopic
      )(_.payload.toDomain.pipe(handleEvent))
}
