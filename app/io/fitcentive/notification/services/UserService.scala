package io.fitcentive.notification.services

import com.google.inject.ImplementedBy
import io.fitcentive.notification.domain.user.UserProfile
import io.fitcentive.notification.infrastructure.rest.RestUserService

import java.util.UUID
import scala.concurrent.Future

@ImplementedBy(classOf[RestUserService])
trait UserService {
  def getUserProfile(userId: UUID): Future[UserProfile]
}
