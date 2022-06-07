package modules.providers

import api.AsyncNotificationApi
import infrastructure.contexts.PubSubExecutionContext
import infrastructure.handlers.MessageEventHandlers
import infrastructure.pubsub.PubSubManager
import io.fitcentive.sdk.gcp.pubsub.{PubSubPublisher, PubSubSubscriber}
import services.SettingsService

import javax.inject.{Inject, Provider}
import scala.concurrent.ExecutionContext

class PubSubManagerProvider @Inject() (settingsService: SettingsService, _notificationApi: AsyncNotificationApi)(
  implicit ec: PubSubExecutionContext
) extends Provider[PubSubManager] {

  trait SubscriptionEventHandlers extends MessageEventHandlers {
    override def notificationApi: AsyncNotificationApi = _notificationApi
    override implicit def executionContext: ExecutionContext = ec
  }

  override def get(): PubSubManager = {
    new PubSubManager(
      publisher = new PubSubPublisher(settingsService.gcpConfig.credentials, settingsService.gcpConfig.project),
      subscriber = new PubSubSubscriber(settingsService.gcpConfig.credentials, settingsService.gcpConfig.project),
      config = settingsService.pubSubConfig,
      environment = settingsService.envConfig.environment
    ) with SubscriptionEventHandlers
  }
}
