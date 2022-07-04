package io.fitcentive.notification.infrastructure.pubsub

import io.fitcentive.notification.domain.config.AppPubSubConfig
import io.fitcentive.notification.domain.pubsub.events.EventHandlers
import io.fitcentive.notification.infrastructure.AntiCorruptionDomain
import io.fitcentive.notification.infrastructure.contexts.PubSubExecutionContext
import io.fitcentive.registry.events.email.EmailVerificationTokenCreated
import io.fitcentive.registry.events.push.UserFollowRequested
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
      _ <- subscribeToUserFollowRequestedEvent
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

  private def subscribeToUserFollowRequestedEvent: Future[Unit] =
    subscriber
      .subscribe[UserFollowRequested](
        environment,
        config.subscriptionsConfig.userFollowRequestedSubscription,
        config.topicsConfig.userFollowRequestedTopic
      )(_.payload.toDomain.pipe(handleEvent))
}
