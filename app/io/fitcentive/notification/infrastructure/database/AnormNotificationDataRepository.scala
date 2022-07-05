package io.fitcentive.notification.infrastructure.database

import anorm.{Column, Macro, MetaDataItem, RowParser, TypeDoesNotMatch}
import io.fitcentive.notification.domain.notification.{NotificationData, NotificationType}
import io.fitcentive.notification.repositories.NotificationDataRepository
import io.fitcentive.sdk.infrastructure.contexts.DatabaseExecutionContext
import io.fitcentive.sdk.infrastructure.database.DatabaseClient
import play.api.db.Database
import play.api.libs.json.{JsValue, Json}

import java.time.Instant
import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps

@Singleton
class AnormNotificationDataRepository @Inject() (val db: Database)(implicit val dbec: DatabaseExecutionContext)
  extends NotificationDataRepository
  with DatabaseClient {

  import AnormNotificationDataRepository._

  override def getUserNotifications(userId: UUID): Future[Seq[NotificationData]] =
    Future {
      getRecords(SQL_GET_USER_NOTIFICATIONS, "userId" -> userId)(notificationDataRowParser).map(_.toDomain)
    }

  override def getNotificationById(userId: UUID, notificationId: UUID): Future[Option[NotificationData]] =
    Future {
      getRecordOpt(SQL_GET_NOTIFICATION_BY_ID, "notificationId" -> notificationId, "userId" -> userId)(
        notificationDataRowParser
      ).map(_.toDomain)
    }

  override def upsertNotification(notificationData: NotificationData.Upsert): Future[NotificationData] =
    Future {
      Instant.now.pipe { now =>
        executeSqlWithExpectedReturn[NotificationDataRow](
          SQL_UPSERT_NOTIFICATION_DATA,
          Seq(
            "id" -> notificationData.id,
            "targetUser" -> notificationData.targetUser,
            "notificationType" -> notificationData.notificationType.stringValue,
            "isInteractive" -> notificationData.isInteractive,
            "hasBeenInteractedWith" -> notificationData.hasBeenInteractedWith,
            "jsonData" -> notificationData.data.toString(),
            "now" -> now
          )
        )(notificationDataRowParser).toDomain
      }
    }
}

object AnormNotificationDataRepository {
  private val SQL_UPSERT_NOTIFICATION_DATA: String =
    """
      |insert into notification_data (id, target_user, notification_type, is_interactive, has_been_interacted_with, data, created_at, updated_at)
      |values ({id}::uuid, {targetUser}::uuid, {notificationType}, {isInteractive}, {hasBeenInteractedWith}, {jsonData}::jsonb, {now}, {now})
      |on conflict (id)
      |do
      |update set 
      |  has_been_interacted_with={hasBeenInteractedWith},
      |  data={jsonData}::jsonb,
      |  updated_at={now}
      |returning * ;
      |""".stripMargin

  private val SQL_GET_USER_NOTIFICATIONS: String =
    """
      |select * from notification_data
      |where target_user = {userId}::uuid ;
      |""".stripMargin

  private val SQL_GET_NOTIFICATION_BY_ID: String =
    """
      |select * from notification_data
      |where id = {notificationId}::uuid and target_user = {userId}::uuid ;
      |""".stripMargin

  private case class NotificationDataRow(
    id: UUID,
    target_user: UUID,
    is_interactive: Boolean,
    has_been_interacted_with: Boolean,
    notification_type: String,
    data: JsValue,
    created_at: Instant,
    updated_at: Instant,
  ) {
    def toDomain: NotificationData =
      NotificationData(
        id = id,
        targetUser = target_user,
        isInteractive = is_interactive,
        hasBeenInteractedWith = has_been_interacted_with,
        data = data,
        notificationType = NotificationType(notification_type),
        createdAt = created_at,
        updatedAt = updated_at
      )
  }

  implicit val jsValueColumnParser: Column[JsValue] =
    anorm.Column.nonNull[JsValue] { (value, meta) =>
      val MetaDataItem(qualified, _, _) = meta
      value match {
        case json: org.postgresql.util.PGobject =>
          Right(Json.parse(json.getValue))
        case _ =>
          Left(
            TypeDoesNotMatch(
              s"Cannot convert $value: ${value.asInstanceOf[AnyRef].getClass} to Json for column $qualified"
            )
          )
      }
    }

  private val notificationDataRowParser: RowParser[NotificationDataRow] = Macro.namedParser[NotificationDataRow]
}
