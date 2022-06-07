package io.fitcentive.notification.modules

import com.google.auth.Credentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.inject.{AbstractModule, Provides}
import io.fitcentive.notification.infrastructure.pubsub.PubSubManager
import io.fitcentive.notification.modules.providers.PubSubManagerProvider

import javax.inject.Singleton

class PubSubModule extends AbstractModule {

  @Provides
  @Singleton
  def provideGcpCredentials: Credentials =
    ServiceAccountCredentials
      .fromStream(getClass.getResourceAsStream("/fitcentive-1210-d5d261de704e.json"))
      .createScoped()

  override def configure(): Unit = {
    bind(classOf[PubSubManager]).toProvider(classOf[PubSubManagerProvider]).asEagerSingleton()
  }

}
