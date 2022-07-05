package io.fitcentive.notification.repositories

import com.google.inject.ImplementedBy
import io.fitcentive.notification.domain.notification.NotificationData
import io.fitcentive.notification.infrastructure.database.AnormNotificationDataRepository

import java.util.UUID
import scala.concurrent.Future

@ImplementedBy(classOf[AnormNotificationDataRepository])
trait NotificationDataRepository {
  def getNotificationById(userId: UUID, notificationId: UUID): Future[Option[NotificationData]]
  def getUserNotifications(userId: UUID): Future[Seq[NotificationData]]
  def upsertNotification(notificationData: NotificationData.Upsert): Future[NotificationData]
}
