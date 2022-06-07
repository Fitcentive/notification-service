package io.fitcentive.notification.infrastructure.pubsub

import io.fitcentive.notification.domain.config.AppPubSubConfig
import io.fitcentive.notification.domain.events.{EmailVerificationTokenCreatedEvent, EventHandlers}
import io.fitcentive.notification.infrastructure.contexts.PubSubExecutionContext
import io.fitcentive.sdk.gcp.pubsub.{PubSubPublisher, PubSubSubscriber}
import io.fitcentive.sdk.logging.AppLogger

import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps

class PubSubManager(
  publisher: PubSubPublisher,
  subscriber: PubSubSubscriber,
  config: AppPubSubConfig,
  environment: String
)(implicit ec: PubSubExecutionContext)
  extends AppLogger {

  self: EventHandlers =>

  initializeSubscriptions

  final def initializeSubscriptions: Future[Unit] = {
    for {
      _ <- Future.sequence(config.topicsConfig.topics.map(publisher.createTopic))
      _ <-
        subscriber
          .subscribe[EmailVerificationTokenCreatedEvent](
            environment,
            config.subscriptionsConfig.emailVerificationTokenCreatedSubscription,
            config.topicsConfig.emailVerificationTokenCreatedTopic
          )(_.payload.pipe(handleEvent))
      _ = logInfo("Subscriptions set up successfully!")
    } yield ()
  }
}
