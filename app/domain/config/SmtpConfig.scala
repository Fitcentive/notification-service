package domain.config

import com.typesafe.config.Config
import domain.BasicCredentials

case class SmtpConfig(host: String, port: String, startTls: Boolean, auth: Option[BasicCredentials])

object SmtpConfig {
  def fromConfig(config: Config): SmtpConfig =
    SmtpConfig(
      host = config.getString("host"),
      port = config.getString("port"),
      startTls = config.getBoolean("startTls"),
      auth = Some(
        BasicCredentials(username = config.getString("auth.user"), password = config.getString("auth.password"))
      ).filter(_.username.nonEmpty)
    )
}
