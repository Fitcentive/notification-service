package infrastructure.settings

import com.google.auth.Credentials
import com.typesafe.config.Config
import domain.config.{EnvironmentConfig, GcpConfig, PubSubConfig, SmtpConfig, SubscriptionsConfig, TopicsConfig}
import play.api.Configuration
import services.SettingsService

import javax.inject.{Inject, Singleton}

@Singleton
class AppConfigService @Inject() (config: Configuration, gcpCredentials: Credentials) extends SettingsService {

  override def envConfig: EnvironmentConfig =
    EnvironmentConfig.fromConfig(config.get[Config]("environment"))

  override def smtpConfig: SmtpConfig =
    SmtpConfig.fromConfig(config.get[Config]("smtp"))

  override def gcpConfig: GcpConfig =
    GcpConfig(credentials = gcpCredentials, project = config.get[String]("gcp.project"))

  override def pubSubConfig: PubSubConfig =
    PubSubConfig(
      topicsConfig = TopicsConfig.fromConfig(config.get[Config]("gcp.pubsub.topics")),
      subscriptionsConfig = SubscriptionsConfig.fromConfig(config.get[Config]("gcp.pubsub.subscriptions"))
    )
}
