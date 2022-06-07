package domain.events

import com.google.pubsub.v1.PubsubMessage
import io.circe.generic.semiauto.deriveCodec
import io.circe.Codec
import io.fitcentive.sdk.gcp.pubsub.PubSubMessageConverter
import io.fitcentive.sdk.utils.PubSubOps

case class EmailVerificationTokenCreatedEvent(emailId: String, token: String, expiry: Option[Long]) extends EventMessage

object EmailVerificationTokenCreatedEvent extends PubSubOps {

  implicit val codec: Codec[EmailVerificationTokenCreatedEvent] =
    deriveCodec[EmailVerificationTokenCreatedEvent]

  implicit val converter: PubSubMessageConverter[EmailVerificationTokenCreatedEvent] =
    (message: PubsubMessage) => message.decodeUnsafe[EmailVerificationTokenCreatedEvent]
}
