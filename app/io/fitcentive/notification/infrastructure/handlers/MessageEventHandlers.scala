package io.fitcentive.notification.infrastructure.handlers

import io.fitcentive.notification.api.AsyncNotificationApi
import io.fitcentive.notification.domain.errors.EmailError
import io.fitcentive.notification.domain.events.{EmailVerificationTokenCreatedEvent, EventHandlers, EventMessage}
import io.fitcentive.sdk.error.DomainError

import scala.concurrent.{ExecutionContext, Future}

trait MessageEventHandlers extends EventHandlers {

  def notificationApi: AsyncNotificationApi
  implicit def executionContext: ExecutionContext

  override def handleEvent(event: EventMessage): Future[Unit] =
    event match {
      case event: EmailVerificationTokenCreatedEvent =>
        notificationApi
          .sendEmail(event.emailId, event.token)
          .flatMap(handleEitherResult(_))

      case _ =>
        Future.failed(new Exception("Unrecognized event"))
    }

  def handleEitherResult[A](either: Either[DomainError, A]): Future[A] =
    either match {
      case Left(err)     => Future.failed(errorToException(err))
      case Right(result) => Future.successful(result)
    }

  def errorToException: PartialFunction[DomainError, Throwable] = {
    case err: EmailError => new Exception(err.reason)
    case _               => new Exception("Unexpected error occurred")
  }

}
