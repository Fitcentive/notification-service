package infrastructure.pubsub

import domain.EventHandlers
import domain.config.PubSubConfig
import domain.events.EmailVerificationTokenCreatedEvent
import infrastructure.contexts.PubSubExecutionContext
import io.fitcentive.sdk.gcp.pubsub.{PubSubPublisher, PubSubSubscriber}
import io.fitcentive.sdk.logging.AppLogger

import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps

// todo - how do we call api methods now?
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
      // todo - need a registry for common models
      _ <-
        subscriber
          .subscribe[EmailVerificationTokenCreatedEvent](
            environment,
            config.subscriptionsConfig.emailVerificationTokenCreatedSubscription,
            config.topicsConfig.emailVerificationTokenCreatedTopic
          ) {
            _.payload.pipe { event =>
              println(s"Received the event: ${event}")
              handleEmailTokenReceived(event.message)
            }
          }
      _ = logInfo("Started listening to subscription successfully")
    } yield ()
  }
}
