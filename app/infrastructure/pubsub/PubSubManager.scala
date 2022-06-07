package infrastructure.pubsub

import domain.config.PubSubConfig
import domain.events.{EmailVerificationTokenCreatedEvent, EventHandlers, EventMessage}
import infrastructure.contexts.PubSubExecutionContext
import io.fitcentive.sdk.gcp.pubsub.{PubSubPublisher, PubSubSubscriber}
import io.fitcentive.sdk.logging.AppLogger

import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps

class PubSubManager(
  publisher: PubSubPublisher,
  subscriber: PubSubSubscriber,
  config: PubSubConfig,
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
