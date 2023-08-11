package io.fitcentive.notification.domain.pubsub.events

import com.google.pubsub.v1.PubsubMessage
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import io.fitcentive.sdk.gcp.pubsub.PubSubMessageConverter
import io.fitcentive.sdk.utils.PubSubOps

import java.util.UUID

case class PromptUserToLogDiaryEntryEvent(targetUser: UUID) extends EventMessage

object PromptUserToLogDiaryEntryEvent extends PubSubOps {

  implicit val codec: Codec[PromptUserToLogDiaryEntryEvent] =
    deriveCodec[PromptUserToLogDiaryEntryEvent]

  implicit val converter: PubSubMessageConverter[PromptUserToLogDiaryEntryEvent] =
    (message: PubsubMessage) => message.decodeUnsafe[PromptUserToLogDiaryEntryEvent]
}
