package services

import com.google.inject.ImplementedBy
import domain.config.{EnvironmentConfig, GcpConfig, PubSubConfig, SmtpConfig}
import infrastructure.settings.AppConfigService

@ImplementedBy(classOf[AppConfigService])
trait SettingsService {
  def smtpConfig: SmtpConfig
  def gcpConfig: GcpConfig
  def pubSubConfig: PubSubConfig
  def envConfig: EnvironmentConfig
}
