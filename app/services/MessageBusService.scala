package services

import com.google.inject.ImplementedBy
import io.fitcentive.sdk.gcp.pubsub.{PubSubPublisher, PubSubSubscriber}
import infrastructure.pubsub.PubSubMessageBusService

import scala.concurrent.Future

@ImplementedBy(classOf[PubSubMessageBusService])
trait MessageBusService {
  def init: Future[Unit]
  def publisher: PubSubPublisher
  def subscriber: PubSubSubscriber
}
