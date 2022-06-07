package io.fitcentive.notification.services

import com.google.inject.ImplementedBy
import io.fitcentive.notification.domain.config.{EnvironmentConfig, GcpConfig, PubSubConfig, SmtpConfig}
import io.fitcentive.notification.infrastructure.settings.AppConfigService

@ImplementedBy(classOf[AppConfigService])
trait SettingsService {
  def smtpConfig: SmtpConfig
  def gcpConfig: GcpConfig
  def pubSubConfig: PubSubConfig
  def envConfig: EnvironmentConfig
}
