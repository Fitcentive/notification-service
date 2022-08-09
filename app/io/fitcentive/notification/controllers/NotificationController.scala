package io.fitcentive.notification.controllers

import io.fitcentive.notification.api.AsyncNotificationApi
import io.fitcentive.notification.domain.notification.NotificationData
import io.fitcentive.notification.domain.payloads.NotificationIdsPayload
import io.fitcentive.notification.domain.push.NotificationDevice
import io.fitcentive.notification.infrastructure.utils.ServerErrorHandler
import io.fitcentive.sdk.play.{InternalAuthAction, UserAuthAction}
import io.fitcentive.sdk.utils.PlayControllerOps
import play.api.libs.json.Json
import play.api.mvc._

import java.util.UUID
import javax.inject._
import scala.concurrent.ExecutionContext

@Singleton
class NotificationController @Inject() (
  notificationApi: AsyncNotificationApi,
  userAuthAction: UserAuthAction,
  internalAuthAction: InternalAuthAction,
  cc: ControllerComponents,
)(implicit exec: ExecutionContext)
  extends AbstractController(cc)
  with PlayControllerOps
  with ServerErrorHandler {

  def getUserNotifications(implicit userId: UUID): Action[AnyContent] =
    userAuthAction.async { implicit userRequest =>
      rejectIfNotEntitled {
        notificationApi
          .getUserNotifications(userId)
          .map(notifications => Ok(Json.toJson(notifications)))
      }
    }

  def updateUserNotification(userId: UUID, notificationId: UUID): Action[AnyContent] =
    userAuthAction.async { implicit userRequest =>
      rejectIfNotEntitled {
        validateJson[NotificationData.Patch](userRequest.body.asJson) { notificationData =>
          notificationApi
            .updateUserNotificationData(userId, notificationId, notificationData)
            .map(handleEitherResult(_)(notification => Ok(Json.toJson(notification))))
        }
      }(userRequest, userId)
    }

  def markNotificationsAsViewed(userId: UUID): Action[AnyContent] =
    userAuthAction.async { implicit userRequest =>
      rejectIfNotEntitled {
        validateJson[NotificationIdsPayload](userRequest.body.asJson) { payload =>
          notificationApi
            .markNotificationsAsViewed(userId, payload.notificationIds)
            .map(handleEitherResult(_)(_ => Ok))
        }
      }(userRequest, userId)
    }

  def getUnreadNotificationCount(userId: UUID): Action[AnyContent] =
    userAuthAction.async { implicit userRequest =>
      rejectIfNotEntitled {
        notificationApi
          .getUnreadNotificationCount(userId)
          .map(count => Ok(Json.toJson(count)))
      }(userRequest, userId)
    }

  def registerDevice(implicit userId: UUID): Action[AnyContent] =
    userAuthAction.async { implicit userRequest =>
      rejectIfNotEntitled {
        validateJson[NotificationDevice](userRequest.body.asJson) { notificationDevice =>
          notificationApi
            .upsertDevice(notificationDevice)
            .map(_ => NoContent)
        }
      }
    }

  def unregisterDevice(implicit userId: UUID, registrationToken: String): Action[AnyContent] =
    userAuthAction.async { implicit userRequest =>
      rejectIfNotEntitled {
        notificationApi
          .unregisterDevice(registrationToken)
          .map(_ => NoContent)
      }
    }

}
