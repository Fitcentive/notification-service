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

  override def getUnreadNotificationForPostWithType(
    userId: UUID,
    postId: UUID,
    notificationType: NotificationType
  ): Future[Option[NotificationData]] =
    Future {
      getRecordOpt(
        SQL_GET_USER_UNREAD_NOTIFICATIONS_FOR_POST_AND_TYPE,
        "userId" -> userId,
        "postId" -> postId,
        "notificationType" -> notificationType.stringValue
      )(notificationDataRowParser).map(_.toDomain)
    }

  override def getUnreadNotifications(userId: UUID): Future[Seq[NotificationData]] =
    Future {
      getRecords(SQL_GET_USER_UNREAD_NOTIFICATIONS, "userId" -> userId)(notificationDataRowParser).map(_.toDomain)
    }

  override def updateNotificationAsViewed(notificationId: UUID): Future[NotificationData] =
    Future {
      Instant.now.pipe { now =>
        executeSqlWithExpectedReturn[NotificationDataRow](
          SQL_UPDATE_NOTIFICATION_AS_VIEWED,
          Seq("notificationId" -> notificationId, "hasBeenViewed" -> true)
        )(notificationDataRowParser).toDomain
      }
    }

  override def getUserNotifications(userId: UUID, limit: Int, offset: Int): Future[Seq[NotificationData]] =
    Future {
      getRecords(SQL_GET_USER_NOTIFICATIONS, "userId" -> userId, "limit" -> limit, "offset" -> offset)(
        notificationDataRowParser
      ).map(_.toDomain)
    }

  override def getNotificationById(userId: UUID, notificationId: UUID): Future[Option[NotificationData]] =
    Future {
      getRecordOpt(SQL_GET_NOTIFICATION_BY_ID, "notificationId" -> notificationId, "userId" -> userId)(
        notificationDataRowParser
      ).map(_.toDomain)
    }

  override def deleteDataForUser(userId: UUID): Future[Unit] =
    Future {
      executeSqlWithoutReturning(SQL_DELETE_DATA_FOR_USER, Seq("userId" -> userId))
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
            "hasBeenViewed" -> notificationData.hasBeenViewed,
            "hasBeenInteractedWith" -> notificationData.hasBeenInteractedWith,
            "jsonData" -> notificationData.data.toString(),
            "now" -> now
          )
        )(notificationDataRowParser).toDomain
      }
    }

  override def getMostRecentNotificationOfTypeForUser(
    userId: UUID,
    notificationType: NotificationType
  ): Future[Option[NotificationData]] =
    Future {
      getRecordOpt(
        SQL_GET_MOST_RECENT_USER_NOTIFICATION_OF_TYPE,
        "userId" -> userId,
        "notificationType" -> notificationType.stringValue
      )(notificationDataRowParser).map(_.toDomain)
    }

}

object AnormNotificationDataRepository {

  private val SQL_DELETE_DATA_FOR_USER: String =
    """
      |delete from notification_data
      |where target_user = {userId}::uuid
      |""".stripMargin

  private val SQL_UPDATE_NOTIFICATION_AS_VIEWED: String =
    """
      |update notification_data
      |set has_been_viewed={hasBeenViewed}
      |where id = {notificationId}::uuid
      |returning * ;
      |""".stripMargin

  private val SQL_UPSERT_NOTIFICATION_DATA: String =
    """
      |insert into notification_data (id, target_user, notification_type, is_interactive, has_been_interacted_with, has_been_viewed, data, created_at, updated_at)
      |values ({id}::uuid, {targetUser}::uuid, {notificationType}, {isInteractive}, {hasBeenInteractedWith}, {hasBeenViewed}, {jsonData}::jsonb, {now}, {now})
      |on conflict (id)
      |do
      |update set 
      |  has_been_viewed={hasBeenViewed},
      |  has_been_interacted_with={hasBeenInteractedWith},
      |  data={jsonData}::jsonb,
      |  updated_at={now}
      |returning * ;
      |""".stripMargin

  private val SQL_GET_USER_NOTIFICATIONS: String =
    """
      |select * from notification_data
      |where target_user = {userId}::uuid 
      |order by updated_at desc 
      |limit {limit} 
      |offset {offset} ;
      |""".stripMargin

  private val SQL_GET_USER_UNREAD_NOTIFICATIONS: String =
    """
      |select * from notification_data
      |where target_user = {userId}::uuid
      |and has_been_viewed = false
      |order by updated_at desc ;
      |""".stripMargin

  private val SQL_GET_USER_UNREAD_NOTIFICATIONS_FOR_POST_AND_TYPE: String =
    """
      |select * from notification_data
      |where target_user = {userId}::uuid
      |and has_been_viewed = false
      |and notification_type = {notificationType}
      |and data::jsonb->>'postId' = {postId} ;
      |""".stripMargin

  private val SQL_GET_MOST_RECENT_USER_NOTIFICATION_OF_TYPE: String =
    """
      |select * from notification_data
      |where target_user = {userId}::uuid
      |and notification_type = {notificationType}
      |order by updated_at desc 
      |limit 1;
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
    has_been_viewed: Boolean,
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
        hasBeenViewed = has_been_viewed,
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
