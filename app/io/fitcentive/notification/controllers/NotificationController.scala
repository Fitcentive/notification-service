package io.fitcentive.notification.controllers

import akka.actor.ActorSystem
import io.fitcentive.notification.api.AsyncNotificationApi
import io.fitcentive.notification.domain.push.NotificationDevice
import io.fitcentive.notification.infrastructure.utils.ServerErrorHandler
import io.fitcentive.sdk.play.{InternalAuthAction, UserAuthAction}
import io.fitcentive.sdk.utils.PlayControllerOps
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
  actorSystem: ActorSystem
)(implicit exec: ExecutionContext)
  extends AbstractController(cc)
  with PlayControllerOps
  with ServerErrorHandler {

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

}
