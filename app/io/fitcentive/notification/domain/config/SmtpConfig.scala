package io.fitcentive.notification.domain.config

import com.typesafe.config.Config
import io.fitcentive.notification.domain.BasicCredentials

case class SmtpConfig(
  host: String,
  port: String,
  appName: String,
  noReplyEmail: String,
  startTls: Boolean,
  auth: Option[BasicCredentials]
)

object SmtpConfig {
  def fromConfig(config: Config): SmtpConfig =
    SmtpConfig(
      host = config.getString("host"),
      port = config.getString("port"),
      appName = config.getString("app-name"),
      noReplyEmail = config.getString("no-reply-email"),
      startTls = config.getBoolean("startTls"),
      auth = Some(
        BasicCredentials(username = config.getString("auth.user"), password = config.getString("auth.password"))
      ).filter(_.username.nonEmpty)
    )
}
