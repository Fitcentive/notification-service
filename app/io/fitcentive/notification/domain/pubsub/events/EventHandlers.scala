package io.fitcentive.notification.domain.pubsub.events

import scala.concurrent.Future

trait EventHandlers {
  def handleEvent(event: EventMessage): Future[Unit]
}
