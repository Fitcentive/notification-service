package domain

import scala.concurrent.Future

trait EventHandlers {
  def handleEmailTokenReceived(token: String): Future[Unit]
}
