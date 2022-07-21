package io.fitcentive.notification.services

import io.fitcentive.notification.domain.user.UserProfile

import java.util.UUID
import scala.concurrent.Future

trait UserService {
  def getUserProfile(userId: UUID): Future[UserProfile]
}
