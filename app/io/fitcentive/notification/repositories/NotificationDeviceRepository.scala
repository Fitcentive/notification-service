package io.fitcentive.notification.repositories

import com.google.inject.ImplementedBy
import io.fitcentive.notification.domain.push.NotificationDevice
import io.fitcentive.notification.infrastructure.database.AnormFirebaseDeviceRepository

import java.util.UUID
import scala.concurrent.Future

@ImplementedBy(classOf[AnormFirebaseDeviceRepository])
trait NotificationDeviceRepository {
  def upsertDevice(device: NotificationDevice): Future[NotificationDevice]
  def getDevicesForUser(userId: UUID): Future[Seq[NotificationDevice]]
  def deleteFcmToken(fcmToken: String): Future[Unit]
  def deleteDevicesForUser(userId: UUID): Future[Unit]
}
