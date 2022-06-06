package infrastructure.pubsub

import domain.events.EmailVerificationTokenCreatedEvent
import infrastructure.contexts.PubSubExecutionContext
import io.fitcentive.sdk.gcp.pubsub.{PubSubPublisher, PubSubSubscriber}
import io.fitcentive.sdk.logging.AppLogger
import services.{MessageBusService, SettingsService}

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps

@Singleton
// todo - revert back to pubSubManager if modules work
// todo - shutdown hooks
class PubSubMessageBusService @Inject() (
  override val publisher: PubSubPublisher,
  override val subscriber: PubSubSubscriber,
  settingsService: SettingsService
)(implicit ec: PubSubExecutionContext)
  extends MessageBusService
  with AppLogger {

  private val config = settingsService.pubSubConfig
  private val environment = settingsService.envConfig.environment

  override def init: Future[Unit] = {
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
              Future.unit
            }
          }
      _ = logInfo("Started listening to subscription successfully")
    } yield ()
  }
}
