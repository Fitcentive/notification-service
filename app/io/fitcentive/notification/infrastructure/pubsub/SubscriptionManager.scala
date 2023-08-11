package io.fitcentive.notification.infrastructure.pubsub

import io.fitcentive.notification.domain.config.AppPubSubConfig
import io.fitcentive.notification.domain.pubsub.events.{
  EventHandlers,
  FlushStaleNotificationsEvent,
  PromptUserWeightEntryEvent
}
import io.fitcentive.notification.infrastructure.AntiCorruptionDomain
import io.fitcentive.notification.infrastructure.contexts.PubSubExecutionContext
import io.fitcentive.registry.events.achievements.UserAttainedNewAchievementMilestone
import io.fitcentive.registry.events.email.EmailVerificationTokenCreated
import io.fitcentive.registry.events.meetup.{
  MeetupDecision,
  MeetupLocationChanged,
  MeetupReminder,
  ParticipantAddedAvailabilityToMeetup,
  ParticipantAddedToMeetup
}
import io.fitcentive.registry.events.push.{
  ChatRoomMessageSent,
  PromptUserToLogDiaryEntry,
  PromptUserToLogWeight,
  UserFriendRequested
}
import io.fitcentive.registry.events.social.{UserCommentedOnPost, UserLikedPost}
import io.fitcentive.registry.events.user.UserFriendRequestDecision
import io.fitcentive.sdk.gcp.pubsub.{PubSubPublisher, PubSubSubscriber}
import io.fitcentive.sdk.logging.AppLogger

import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps

class SubscriptionManager(
  publisher: PubSubPublisher,
  subscriber: PubSubSubscriber,
  config: AppPubSubConfig,
  environment: String
)(implicit ec: PubSubExecutionContext)
  extends AppLogger
  with AntiCorruptionDomain {

  self: EventHandlers =>

  initializeSubscriptions

  final def initializeSubscriptions: Future[Unit] = {
    for {
      _ <- Future.sequence(config.topicsConfig.topics.map(publisher.createTopic))
      _ <- subscribeToEmailVerificationTokenCreatedEvent
      _ <- subscribeToUserFriendRequestedEvent
      _ <- subscribeToUserFriendRequestDecisionEvent
      _ <- subscribeToChatRoomMessageSentEvent
      _ <- subscribeToUserCommentedOnPostEvent
      _ <- subscribeToUserLikedPostEvent
      _ <- subscribeToMeetupDecisionEvent
      _ <- subscribeToMeetupReminderEvent
      _ <- subscribeToMeetupLocationChangedEvent
      _ <- subscribeToParticipantAddedToMeetupEvent
      _ <- subscribeToParticipantAvailabilityAddedToMeetupEvent
      _ <- subscribeToFlushStaleNotificationsEvent
      _ <- subscribeToUserAttainedNewAchievementMilestoneEvent
      _ <- subscribeToPromptUserWeightEntryEvent
      _ <- subscribeToPromptUserDiaryEntryEvent
      _ = logInfo("Subscriptions set up successfully!")
    } yield ()
  }

  private def subscribeToPromptUserDiaryEntryEvent: Future[Unit] =
    subscriber
      .subscribe[PromptUserToLogDiaryEntry](
        environment,
        config.subscriptionsConfig.promptUserToLogDiaryEntrySubscription,
        config.topicsConfig.promptUserToLogDiaryEntryTopic
      )(_.payload.toDomain.pipe(handleEvent))

  private def subscribeToPromptUserWeightEntryEvent: Future[Unit] =
    subscriber
      .subscribe[PromptUserToLogWeight](
        environment,
        config.subscriptionsConfig.promptUserToLogWeightSubscription,
        config.topicsConfig.promptUserToLogWeightTopic
      )(_.payload.toDomain.pipe(handleEvent))

  private def subscribeToUserAttainedNewAchievementMilestoneEvent: Future[Unit] =
    subscriber
      .subscribe[UserAttainedNewAchievementMilestone](
        environment,
        config.subscriptionsConfig.userAttainedNewAchievementMilestoneSubscription,
        config.topicsConfig.userAttainedNewAchievementMilestoneTopic
      )(_.payload.toDomain.pipe(handleEvent))

  private def subscribeToEmailVerificationTokenCreatedEvent: Future[Unit] =
    subscriber
      .subscribe[EmailVerificationTokenCreated](
        environment,
        config.subscriptionsConfig.emailVerificationTokenCreatedSubscription,
        config.topicsConfig.emailVerificationTokenCreatedTopic
      )(_.payload.toDomain.pipe(handleEvent))

  private def subscribeToUserFriendRequestedEvent: Future[Unit] =
    subscriber
      .subscribe[UserFriendRequested](
        environment,
        config.subscriptionsConfig.userFriendRequestedSubscription,
        config.topicsConfig.userFriendRequestedTopic
      )(_.payload.toDomain.pipe(handleEvent))

  private def subscribeToUserFriendRequestDecisionEvent: Future[Unit] =
    subscriber
      .subscribe[UserFriendRequestDecision](
        environment,
        config.subscriptionsConfig.userFriendRequestDecisionSubscription,
        config.topicsConfig.userFriendRequestDecisionTopic
      )(_.payload.toDomain.pipe(handleEvent))

  private def subscribeToChatRoomMessageSentEvent: Future[Unit] =
    subscriber
      .subscribe[ChatRoomMessageSent](
        environment,
        config.subscriptionsConfig.chatRoomMessageSentSubscription,
        config.topicsConfig.chatRoomMessageSentTopic
      )(_.payload.toDomain.pipe(handleEvent))

  private def subscribeToUserCommentedOnPostEvent: Future[Unit] =
    subscriber
      .subscribe[UserCommentedOnPost](
        environment,
        config.subscriptionsConfig.userCommentedOnPostSubscription,
        config.topicsConfig.userCommentedOnPostTopic
      )(_.payload.toDomain.pipe(handleEvent))

  private def subscribeToUserLikedPostEvent: Future[Unit] =
    subscriber
      .subscribe[UserLikedPost](
        environment,
        config.subscriptionsConfig.userLikedPostSubscription,
        config.topicsConfig.userLikedPostTopic
      )(_.payload.toDomain.pipe(handleEvent))

  private def subscribeToMeetupDecisionEvent: Future[Unit] =
    subscriber
      .subscribe[MeetupDecision](
        environment,
        config.subscriptionsConfig.meetupDecisionSubscription,
        config.topicsConfig.meetupDecisionTopic
      )(_.payload.toDomain.pipe(handleEvent))

  private def subscribeToMeetupReminderEvent: Future[Unit] =
    subscriber
      .subscribe[MeetupReminder](
        environment,
        config.subscriptionsConfig.meetupReminderSubscription,
        config.topicsConfig.meetupReminderTopic
      )(_.payload.toDomain.pipe(handleEvent))

  private def subscribeToMeetupLocationChangedEvent: Future[Unit] =
    subscriber
      .subscribe[MeetupLocationChanged](
        environment,
        config.subscriptionsConfig.meetupLocationChangedSubscription,
        config.topicsConfig.meetupLocationChangedTopic
      )(_.payload.toDomain.pipe(handleEvent))

  private def subscribeToParticipantAddedToMeetupEvent: Future[Unit] =
    subscriber
      .subscribe[ParticipantAddedToMeetup](
        environment,
        config.subscriptionsConfig.participantAddedToMeetupSubscription,
        config.topicsConfig.participantAddedToMeetupTopic
      )(_.payload.toDomain.pipe(handleEvent))

  private def subscribeToParticipantAvailabilityAddedToMeetupEvent: Future[Unit] =
    subscriber
      .subscribe[ParticipantAddedAvailabilityToMeetup](
        environment,
        config.subscriptionsConfig.participantAddedAvailabilityToMeetupSubscription,
        config.topicsConfig.participantAddedAvailabilityToMeetupTopic
      )(_.payload.toDomain.pipe(handleEvent))

  private def subscribeToFlushStaleNotificationsEvent: Future[Unit] =
    subscriber
      .subscribe[FlushStaleNotificationsEvent](
        environment,
        config.subscriptionsConfig.flushStaleNotificationsSubscription,
        config.topicsConfig.flushStaleNotificationsTopic
      )(_.payload.pipe(handleEvent))
}
