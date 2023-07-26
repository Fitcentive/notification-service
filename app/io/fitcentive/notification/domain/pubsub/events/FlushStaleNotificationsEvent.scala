package io.fitcentive.notification.domain.pubsub.events

import com.google.pubsub.v1.PubsubMessage
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import io.fitcentive.sdk.gcp.pubsub.PubSubMessageConverter
import io.fitcentive.sdk.utils.PubSubOps

case class FlushStaleNotificationsEvent(message: String) extends EventMessage

object FlushStaleNotificationsEvent extends PubSubOps {

  implicit val codec: Codec[FlushStaleNotificationsEvent] =
    deriveCodec[FlushStaleNotificationsEvent]

  implicit val converter: PubSubMessageConverter[FlushStaleNotificationsEvent] =
    (message: PubsubMessage) => message.decodeUnsafe[FlushStaleNotificationsEvent]
}
