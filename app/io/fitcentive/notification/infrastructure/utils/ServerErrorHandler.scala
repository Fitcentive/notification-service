package io.fitcentive.notification.infrastructure.utils

import io.fitcentive.sdk.error.{DomainError, EntityConflictError, EntityNotFoundError}
import io.fitcentive.sdk.logging.AppLogger
import io.fitcentive.sdk.utils.DomainErrorHandler
import play.api.mvc.Result
import play.api.mvc.Results._

trait ServerErrorHandler extends DomainErrorHandler with AppLogger {

  override def resultErrorAsyncHandler: PartialFunction[Throwable, Result] = {
    case e: Exception =>
      logError(s"${e.getMessage}", e)
      InternalServerError(e.getMessage)
  }

  override def domainErrorHandler: PartialFunction[DomainError, Result] = {
    case EntityNotFoundError(reason) => NotFound(reason)
    case EntityConflictError(reason) => Conflict(reason)
    case _                           => InternalServerError("Unexpected error occurred ")
  }

}
