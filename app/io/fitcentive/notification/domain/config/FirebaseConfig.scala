package io.fitcentive.notification.domain.config

import com.typesafe.config.Config

case class FirebaseConfig(serviceAccountCredentials: String, databaseUrl: String)

object FirebaseConfig {
  def fromConfig(config: Config): FirebaseConfig =
    FirebaseConfig(
      serviceAccountCredentials = config.getString("service-account-string-credentials"),
      databaseUrl = config.getString("database-url")
    )
}
