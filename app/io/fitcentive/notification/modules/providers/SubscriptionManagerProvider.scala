package io.fitcentive.notification.modules.providers

import com.google.auth.oauth2.ServiceAccountCredentials
import io.fitcentive.notification.api.AsyncNotificationApi
import io.fitcentive.notification.infrastructure.contexts.PubSubExecutionContext
import io.fitcentive.notification.infrastructure.handlers.MessageEventHandlers
import io.fitcentive.notification.infrastructure.pubsub.SubscriptionManager
import io.fitcentive.sdk.gcp.pubsub.{PubSubPublisher, PubSubSubscriber}
import io.fitcentive.notification.services.SettingsService

import java.io.ByteArrayInputStream
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
    val credentials =
      ServiceAccountCredentials
        .fromStream(new ByteArrayInputStream(settingsService.pubSubServiceAccountStringCredentials.getBytes()))
        .createScoped()
    new SubscriptionManager(
      publisher = publisher,
      subscriber = new PubSubSubscriber(credentials, settingsService.gcpConfig.project),
      config = settingsService.pubSubConfig,
      environment = settingsService.envConfig.environment
    ) with SubscriptionEventHandlers
  }
}
