package io.fitcentive.notification.repositories

import com.google.inject.ImplementedBy
import io.fitcentive.notification.domain.notification.{NotificationData, NotificationType}
import io.fitcentive.notification.infrastructure.database.AnormNotificationDataRepository

import java.util.UUID
import scala.concurrent.Future

@ImplementedBy(classOf[AnormNotificationDataRepository])
trait NotificationDataRepository {
  def deleteDataForUser(userId: UUID): Future[Unit]
  def getNotificationById(userId: UUID, notificationId: UUID): Future[Option[NotificationData]]
  def getMostRecentNotificationOfTypeForUser(
    userId: UUID,
    notificationType: NotificationType
  ): Future[Option[NotificationData]]
  def getUserNotifications(userId: UUID, limit: Int, offset: Int): Future[Seq[NotificationData]]
  def upsertNotification(notificationData: NotificationData.Upsert): Future[NotificationData]
  def updateNotificationAsViewed(notificationId: UUID): Future[NotificationData]
  def getUnreadNotifications(userId: UUID): Future[Seq[NotificationData]]
  def getUnreadNotificationForPostWithType(
    userId: UUID,
    postId: UUID,
    notificationType: NotificationType
  ): Future[Option[NotificationData]]
}
