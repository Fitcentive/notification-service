package io.fitcentive.notification.domain.pubsub.events

import java.util.UUID

case class ChatRoomMessageSentEvent(sendingUser: UUID, targetUser: UUID, roomId: UUID, message: String)
  extends EventMessage
