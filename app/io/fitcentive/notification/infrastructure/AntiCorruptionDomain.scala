package io.fitcentive.notification.infrastructure

import io.fitcentive.notification.domain.pubsub.events._
import io.fitcentive.registry.events.email.EmailVerificationTokenCreated
import io.fitcentive.registry.events.push.{ChatRoomMessageSent, UserFriendRequested}
import io.fitcentive.registry.events.social.{UserCommentedOnPost, UserLikedPost}
import io.fitcentive.registry.events.meetup.{MeetupDecision, ParticipantAddedToMeetup}
import io.fitcentive.registry.events.user.UserFriendRequestDecision

trait AntiCorruptionDomain {

  implicit class EmailVerificationTokenCreatedEvent2Domain(event: EmailVerificationTokenCreated) {
    def toDomain: EmailVerificationTokenCreatedEvent =
      EmailVerificationTokenCreatedEvent(event.emailId, event.token, event.expiry)
  }

  implicit class UserFriendRequestedEvent2Domain(event: UserFriendRequested) {
    def toDomain: UserFriendRequestedEvent =
      UserFriendRequestedEvent(event.requestingUser, event.targetUser)
  }

  implicit class UserFriendRequestDecisionEvent2Domain(event: UserFriendRequestDecision) {
    def toDomain: UserFriendRequestDecisionEvent =
      UserFriendRequestDecisionEvent(event.targetUser, event.isApproved)
  }

  implicit class UserCommentedOnPostEvent2Domain(event: UserCommentedOnPost) {
    def toDomain: UserCommentedOnPostEvent =
      UserCommentedOnPostEvent(event.commentingUser, event.targetUser, event.postId, event.postCreatorId)
  }

  implicit class UserLikedPostEvent2Domain(event: UserLikedPost) {
    def toDomain: UserLikedPostEvent =
      UserLikedPostEvent(event.likingUser, event.targetUser, event.postId)
  }

  implicit class ChatRoomMessageSentEvent2Domain(event: ChatRoomMessageSent) {
    def toDomain: ChatRoomMessageSentEvent =
      ChatRoomMessageSentEvent(event.sendingUser, event.targetUser, event.roomId, event.message)
  }

  implicit class ParticipantAddedToMeetupEvent2Domain(event: ParticipantAddedToMeetup) {
    def toDomain: ParticipantAddedToMeetupEvent =
      ParticipantAddedToMeetupEvent(event.meetupId, event.ownerId, event.participantId)
  }

  implicit class MeetupDecisionEvent2Domain(event: MeetupDecision) {
    def toDomain: MeetupDecisionEvent =
      MeetupDecisionEvent(event.meetupId, event.meetupOwnerId, event.participantId, event.hasAccepted)
  }

}
