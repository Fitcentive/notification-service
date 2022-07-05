package io.fitcentive.notification.infrastructure.settings

import com.google.auth.Credentials
import com.typesafe.config.Config
import io.fitcentive.notification.domain.config.{
  AppPubSubConfig,
  EnvironmentConfig,
  SmtpConfig,
  SubscriptionsConfig,
  TopicsConfig
}
import play.api.Configuration
import io.fitcentive.notification.services.SettingsService
import io.fitcentive.sdk.config.{GcpConfig, JwtConfig, PubSubConfig, SecretConfig}

import javax.inject.{Inject, Singleton}

@Singleton
class AppConfigService @Inject() (config: Configuration, gcpCredentials: Credentials) extends SettingsService {

  override def envConfig: EnvironmentConfig =
    EnvironmentConfig.fromConfig(config.get[Config]("environment"))

  override def smtpConfig: SmtpConfig =
    SmtpConfig.fromConfig(config.get[Config]("smtp"))

  override def gcpConfig: GcpConfig =
    GcpConfig(credentials = gcpCredentials, project = config.get[String]("gcp.project"))

  override def pubSubConfig: AppPubSubConfig =
    AppPubSubConfig(
      topicsConfig = TopicsConfig.fromConfig(config.get[Config]("gcp.pubsub.topics")),
      subscriptionsConfig = SubscriptionsConfig.fromConfig(config.get[Config]("gcp.pubsub.subscriptions"))
    )

  override def secretConfig: SecretConfig = SecretConfig.fromConfig(config.get[Config]("services"))

  override def keycloakServerUrl: String = config.get[String]("keycloak.server-url")

  override def jwtConfig: JwtConfig = JwtConfig.apply(config.get[Config]("jwt"))
}
