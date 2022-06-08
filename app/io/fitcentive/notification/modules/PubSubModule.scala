package io.fitcentive.notification.modules

import com.google.auth.Credentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.inject.{AbstractModule, Provides}
import io.fitcentive.notification.infrastructure.pubsub.SubscriptionManager
import io.fitcentive.notification.modules.providers.SubscriptionManagerProvider
import io.fitcentive.notification.services.SettingsService
import io.fitcentive.sdk.gcp.pubsub.PubSubPublisher

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
  def providePubSubPublisher(settingsService: SettingsService): PubSubPublisher =
    new PubSubPublisher(settingsService.gcpConfig.credentials, settingsService.gcpConfig.project)

  override def configure(): Unit = {
    bind(classOf[SubscriptionManager]).toProvider(classOf[SubscriptionManagerProvider]).asEagerSingleton()
  }

}
