package io.fitcentive.notification.services

import com.google.inject.ImplementedBy
import io.fitcentive.notification.domain.config.{AppPubSubConfig, EnvironmentConfig, FirebaseConfig, SmtpConfig}
import io.fitcentive.notification.infrastructure.settings.AppConfigService
import io.fitcentive.sdk.config.{GcpConfig, JwtConfig, SecretConfig, ServerConfig}

@ImplementedBy(classOf[AppConfigService])
trait SettingsService {
  def pubSubServiceAccountStringCredentials: String
  def imageHostBaseUrl: String
  def firebaseConfig: FirebaseConfig
  def smtpConfig: SmtpConfig
  def gcpConfig: GcpConfig
  def pubSubConfig: AppPubSubConfig
  def envConfig: EnvironmentConfig
  def keycloakServerUrl: String
  def jwtConfig: JwtConfig
  def secretConfig: SecretConfig
  def userServiceConfig: ServerConfig
}
