package modules.providers

import api.NotificationApi
import domain.EventHandlers
import infrastructure.contexts.PubSubExecutionContext
import infrastructure.pubsub.PubSubManager
import io.fitcentive.sdk.gcp.pubsub.{PubSubPublisher, PubSubSubscriber}
import services.SettingsService

import javax.inject.{Inject, Provider}
import scala.concurrent.Future

class PubSubManagerProvider @Inject() (settingsService: SettingsService, notificationApi: NotificationApi)(implicit
  ec: PubSubExecutionContext
) extends Provider[PubSubManager] {

  trait SubscriptionEventHandlers extends EventHandlers {
    override def handleEmailTokenReceived(token: String): Future[Unit] = {
      println("IN HANDLE EMAIL TOKEN RECEIVED ABOUT TO CALL API METHOD")
      notificationApi.sendEmail.map(_.map(identity))
    }
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
