package infrastructure.handlers

import api.AsyncNotificationApi
import domain.events.{EmailVerificationTokenCreatedEvent, EventHandlers, EventMessage}
import scala.concurrent.{ExecutionContext, Future}

trait MessageEventHandlers extends EventHandlers {

  def notificationApi: AsyncNotificationApi
  implicit def executionContext: ExecutionContext

  override def handleEvent(event: EventMessage): Future[Unit] =
    event match {
      case event: EmailVerificationTokenCreatedEvent =>
        notificationApi.sendEmail.map(_.map(identity))

      case _ =>
        Future.failed(new Exception("Unrecognized event"))
    }

}
