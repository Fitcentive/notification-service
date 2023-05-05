package io.fitcentive.notification.infrastructure.handlers

import io.fitcentive.notification.api.AsyncNotificationApi
import io.fitcentive.notification.domain.errors.EmailError
import io.fitcentive.notification.domain.pubsub.events.{
  ChatRoomMessageSentEvent,
  EmailVerificationTokenCreatedEvent,
  EventHandlers,
  EventMessage,
  MeetupDecisionEvent,
  MeetupReminderEvent,
  ParticipantAddedToMeetupEvent,
  UserCommentedOnPostEvent,
  UserFriendRequestDecisionEvent,
  UserFriendRequestedEvent,
  UserLikedPostEvent
}
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

      case event: UserFriendRequestedEvent =>
        notificationApi
          .sendUserFriendRequestNotification(event.requestingUser, event.targetUser)
          .map(_ => ())

      case event: UserFriendRequestDecisionEvent =>
        notificationApi
          .updateUserFriendRequestNotificationData(event.targetUser, event.isApproved)
          .map(_ => ())

      case event: ChatRoomMessageSentEvent =>
        notificationApi
          .sendChatRoomMessageSentNotification(event.sendingUser, event.targetUser, event.roomId, event.message)
          .map(_ => ())

      case event: UserCommentedOnPostEvent =>
        notificationApi
          .addUserCommentedOnPostNotification(event.commentingUser, event.targetUser, event.postId, event.postCreatorId)
          .map(_ => ())

      case event: UserLikedPostEvent =>
        notificationApi
          .addUserLikedPostNotification(event.likingUser, event.targetUser, event.postId)
          .map(_ => ())

      case event: ParticipantAddedToMeetupEvent =>
        notificationApi
          .addParticipantAddedToMeetupNotification(event.meetupId, event.ownerId, event.participantId)
          .map(_ => ())

      case event: MeetupDecisionEvent =>
        notificationApi
          .addMeetupDecisionNotification(event.meetupId, event.meetupOwnerId, event.participantId, event.hasAccepted)
          .map(_ => ())

      case event: MeetupReminderEvent =>
        notificationApi
          .meetupReminderNotification(event.meetupId, event.meetupName, event.targetUser)
          .map(_ => ())

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
