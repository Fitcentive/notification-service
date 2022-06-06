package modules

import com.google.auth.Credentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.inject.{AbstractModule, Provides}
import infrastructure.contexts.PubSubExecutionContext
import infrastructure.pubsub.PubSubManager
import io.fitcentive.sdk.gcp.pubsub.{PubSubPublisher, PubSubSubscriber}
import services.SettingsService

import javax.inject.Singleton

class PubSubModule extends AbstractModule {

  @Provides
  @Singleton
  def provideGcpCredentials: Credentials =
    ServiceAccountCredentials
      .fromStream(getClass.getResourceAsStream("/fitcentive-1210-d5d261de704e.json"))
      .createScoped()

  @Provides
  @Singleton
  def providerPubSubManager(settingsService: SettingsService)(implicit ec: PubSubExecutionContext): PubSubManager =
    new PubSubManager(
      publisher = new PubSubPublisher(settingsService.gcpConfig.credentials, settingsService.gcpConfig.project),
      subscriber = new PubSubSubscriber(settingsService.gcpConfig.credentials, settingsService.gcpConfig.project),
      config = settingsService.pubSubConfig,
      environment = settingsService.envConfig.environment
    )

}
