package io.fitcentive.notification.domain.pubsub.events

import com.google.pubsub.v1.PubsubMessage
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import io.fitcentive.sdk.gcp.pubsub.PubSubMessageConverter
import io.fitcentive.sdk.utils.PubSubOps

import java.util.UUID

case class PromptUserToLogWeightEvent(targetUser: UUID) extends EventMessage

object PromptUserWeightEntryEvent extends PubSubOps {

  implicit val codec: Codec[PromptUserToLogWeightEvent] =
    deriveCodec[PromptUserToLogWeightEvent]

  implicit val converter: PubSubMessageConverter[PromptUserToLogWeightEvent] =
    (message: PubsubMessage) => message.decodeUnsafe[PromptUserToLogWeightEvent]
}
