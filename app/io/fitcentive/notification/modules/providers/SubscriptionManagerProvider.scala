package io.fitcentive.notification.modules.providers

import io.fitcentive.notification.api.AsyncNotificationApi
import io.fitcentive.notification.infrastructure.contexts.PubSubExecutionContext
import io.fitcentive.notification.infrastructure.handlers.MessageEventHandlers
import io.fitcentive.notification.infrastructure.pubsub.SubscriptionManager
import io.fitcentive.sdk.gcp.pubsub.{PubSubPublisher, PubSubSubscriber}
import io.fitcentive.notification.services.SettingsService

import javax.inject.{Inject, Provider}
import scala.concurrent.ExecutionContext

class SubscriptionManagerProvider @Inject() (
  publisher: PubSubPublisher,
  settingsService: SettingsService,
  _notificationApi: AsyncNotificationApi
)(implicit ec: PubSubExecutionContext)
  extends Provider[SubscriptionManager] {

  trait SubscriptionEventHandlers extends MessageEventHandlers {
    override def notificationApi: AsyncNotificationApi = _notificationApi
    override implicit def executionContext: ExecutionContext = ec
  }

  override def get(): SubscriptionManager = {
    new SubscriptionManager(
      publisher = publisher,
      subscriber = new PubSubSubscriber(settingsService.gcpConfig.credentials, settingsService.gcpConfig.project),
      config = settingsService.pubSubConfig,
      environment = settingsService.envConfig.environment
    ) with SubscriptionEventHandlers
  }
}