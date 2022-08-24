package io.fitcentive.notification.services

import com.google.inject.ImplementedBy
import io.fitcentive.notification.infrastructure.settings.AppHealthService

import scala.concurrent.Future

@ImplementedBy(classOf[AppHealthService])
trait HealthService {
  def isSqlDatabaseAvailable: Future[Boolean]
}
