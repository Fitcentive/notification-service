package io.fitcentive.notification.infrastructure.database

import anorm.{Macro, RowParser}
import io.fitcentive.notification.domain.push.NotificationDevice
import io.fitcentive.notification.infrastructure.database.AnormFirebaseDeviceRepository._
import io.fitcentive.notification.repositories.NotificationDeviceRepository
import io.fitcentive.sdk.infrastructure.contexts.DatabaseExecutionContext
import io.fitcentive.sdk.infrastructure.database.DatabaseClient
import play.api.db.Database

import java.time.Instant
import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps

@Singleton
class AnormFirebaseDeviceRepository @Inject() (val db: Database)(implicit val dbec: DatabaseExecutionContext)
  extends NotificationDeviceRepository
  with DatabaseClient {

  override def upsertDevice(device: NotificationDevice): Future[NotificationDevice] =
    Future {
      Instant.now.pipe { now =>
        executeSqlWithExpectedReturn[FcmDevicesRow](
          SQL_UPSERT_DEVICE,
          Seq(
            "registrationToken" -> device.registrationToken,
            "userId" -> device.userId,
            "manufacturer" -> device.manufacturer,
            "model" -> device.model,
            "isPhysicalDevice" -> device.isPhysicalDevice,
            "now" -> now
          )
        )(fcmDeviceRowParser).toDomain
      }
    }

  override def getDevicesForUser(userId: UUID): Future[Seq[NotificationDevice]] =
    Future {
      getRecords(SQL_GET_DEVICES_FOR_USER, "userId" -> userId)(fcmDeviceRowParser).map(_.toDomain)
    }

  override def deleteFcmToken(registrationToken: String): Future[Unit] =
    Future {
      deleteRecords(SQL_DELETE_TOKEN, "registrationToken" -> registrationToken)
    }

  override def deleteDevicesForUser(userId: UUID): Future[Unit] =
    Future {
      executeSqlWithoutReturning(SQL_DELETE_DEVICES_FOR_USER, Seq("userId" -> userId))
    }
}

object AnormFirebaseDeviceRepository {

  private case class FcmDevicesRow(
    registration_token: String,
    user_id: UUID,
    manufacturer: String,
    model: String,
    is_physical_device: Boolean,
    created_at: Instant,
    updated_at: Instant,
  ) {
    def toDomain: NotificationDevice =
      NotificationDevice(
        userId = user_id,
        registrationToken = registration_token,
        manufacturer = manufacturer,
        model = model,
        isPhysicalDevice = is_physical_device
      )
  }

  private val fcmDeviceRowParser: RowParser[FcmDevicesRow] = Macro.namedParser[FcmDevicesRow]

  private val SQL_DELETE_DEVICES_FOR_USER: String =
    """
      |delete from fcm_devices
      |where user_id = {userId}::uuid ;
      |""".stripMargin

  private val SQL_GET_DEVICES_FOR_USER: String =
    """
      |select *
      |from fcm_devices f
      |where f.user_id = {userId}::uuid ;
      |""".stripMargin

  private val SQL_DELETE_TOKEN: String =
    """
      |delete from fcm_devices
      |where registration_token = {registrationToken} ;
      |""".stripMargin

  private val SQL_UPSERT_DEVICE: String =
    """
      |insert into fcm_devices (registration_token, user_id, manufacturer, model, is_physical_device, created_at, updated_at)
      |values ({registrationToken}, {userId}::uuid, {manufacturer}, {model}, {isPhysicalDevice}, {now}, {now})
      |on conflict (registration_token)
      |do
      |update set 
      |  registration_token={registrationToken}, 
      |  user_id={userId}::uuid, 
      |  manufacturer={manufacturer}, 
      |  model={model}, 
      |  is_physical_device={isPhysicalDevice},
      |  updated_at={now}
      |returning * ;
      |""".stripMargin

}
