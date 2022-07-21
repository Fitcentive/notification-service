package io.fitcentive.notification.infrastructure.rest

import io.fitcentive.notification.domain.user.UserProfile
import io.fitcentive.notification.infrastructure.utils.ServiceSecretSupport
import io.fitcentive.notification.services.{SettingsService, UserService}
import io.fitcentive.sdk.config.ServerConfig
import play.api.libs.ws.WSClient

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RestUserService @Inject() (wsClient: WSClient, settingsService: SettingsService)(implicit ec: ExecutionContext)
  extends UserService
  with ServiceSecretSupport {

  val userServiceConfig: ServerConfig = settingsService.userServiceConfig
  val baseUrl: String = userServiceConfig.serverUrl

  override def getUserProfile(userId: UUID): Future[UserProfile] =
    wsClient
      .url(s"$baseUrl/api/internal/user/$userId/profile")
      .addHttpHeaders("Accept" -> "application/json")
      .addServiceSecret(settingsService)
      .get()
      .map(_.json.as[UserProfile])
}
