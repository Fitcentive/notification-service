package io.fitcentive.notification.api

import cats.data.EitherT
import cats.implicits.toTraverseOps
import io.fitcentive.notification.domain.email.{EmailContents, EmailFrom}
import io.fitcentive.notification.domain.errors.EmailError
import io.fitcentive.notification.domain.notification.{NotificationData, NotificationType}
import io.fitcentive.notification.domain.push.{NotificationDevice, PushNotificationResponse}
import io.fitcentive.notification.domain.push.messages.{
  ChatRoomMessageSentMessage,
  MeetupReminderMessage,
  ParticipantAddedAvailabilityToMeetupMessage,
  ParticipantAddedToMeetupMessage,
  UserFriendRequestedMessage
}
import io.fitcentive.notification.services.{EmailService, PushNotificationService, SettingsService, UserService}
import io.fitcentive.notification.repositories.{NotificationDataRepository, NotificationDeviceRepository}
import io.fitcentive.sdk.error.{DomainError, EntityNotFoundError}
import play.api.libs.json.Json

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AsyncNotificationApi @Inject() (
  emailService: EmailService,
  notificationDeviceRepository: NotificationDeviceRepository,
  notificationDataRepository: NotificationDataRepository,
  pushNotificationService: PushNotificationService,
  userService: UserService,
  settingsService: SettingsService
)(implicit ec: ExecutionContext) {

  val defaultLimit = 50
  val defaultOffset = 0

  // Note we have made a conscious decision here to not modify/delete notification data in which `userId` appears as a supporting field
  // For example, in UserFollowRequest/UserLikedPost/UserCommentedOnPost notifications, we are not modifying the JSON data field
  // The responsibility is delegated to the client app
  def deleteUserData(userId: UUID): Future[Unit] =
    for {
      _ <- notificationDeviceRepository.deleteDevicesForUser(userId)
      _ <- notificationDataRepository.deleteDataForUser(userId)
    } yield ()

  def sendEmail(emailId: String, token: String): Future[Either[EmailError, Unit]] =
    emailService.sendEmail(
      EmailContents(
        from = EmailFrom(settingsService.smtpConfig.appName, settingsService.smtpConfig.noReplyEmail),
        to = emailId,
        subject = "Email Verification Token",
        body = s"Here is your token: $token"
      )
    )

  def updateUserFriendRequestNotificationData(
    targetUserId: UUID,
    isApproved: Boolean
  ): Future[Either[DomainError, NotificationData]] = {
    (for {
      mostRecentUserFriendRequestNotification <- EitherT[Future, DomainError, NotificationData](
        notificationDataRepository
          .getMostRecentNotificationOfTypeForUser(targetUserId, NotificationType.UserFriendRequest)
          .map(_.map(Right.apply).getOrElse(Left(EntityNotFoundError("Notification not found!"))))
      )
      newData = Json.obj(
        "requestingUser" -> (mostRecentUserFriendRequestNotification.data \ "requestingUser").get,
        "targetUser" -> targetUserId,
        "isApproved" -> isApproved,
      )
      notificationDataUpsert = NotificationData.Upsert(
        id = mostRecentUserFriendRequestNotification.id,
        targetUser = targetUserId,
        isInteractive = mostRecentUserFriendRequestNotification.isInteractive,
        notificationType = mostRecentUserFriendRequestNotification.notificationType,
        hasBeenInteractedWith = true,
        hasBeenViewed = true,
        data = newData
      )
      updatedNotificationData <-
        EitherT.right[DomainError](notificationDataRepository.upsertNotification(notificationDataUpsert))
    } yield updatedNotificationData).value
  }

  def upsertDevice(device: NotificationDevice): Future[NotificationDevice] =
    notificationDeviceRepository.upsertDevice(device)

  def unregisterDevice(registrationToken: String): Future[Unit] =
    notificationDeviceRepository.deleteFcmToken(registrationToken)

  def getUserNotifications(
    userId: UUID,
    limit: Option[Int] = None,
    offset: Option[Int] = None
  ): Future[Seq[NotificationData]] =
    notificationDataRepository.getUserNotifications(
      userId,
      limit.fold(defaultLimit)(identity),
      offset.fold(defaultOffset)(identity)
    )

  def getUnreadNotificationCount(userId: UUID): Future[Int] =
    notificationDataRepository
      .getUnreadNotifications(userId)
      .map(_.size)

  def markNotificationsAsViewed(userId: UUID, notificationIds: Seq[UUID]): Future[Either[DomainError, Unit]] =
    (for {
      originalNotifications <- EitherT(
        Future
          .sequence(
            notificationIds.map(
              notificationDataRepository
                .getNotificationById(userId, _)
                .map(_.map(Right.apply).getOrElse(Left(EntityNotFoundError("Notification not found!"))))
            )
          )
          .map(_.sequence)
      )
      _ <- EitherT.right[DomainError](
        Future
          .sequence(originalNotifications.map(n => notificationDataRepository.updateNotificationAsViewed(n.id)))
      )
    } yield ()).value

  def updateUserNotificationData(
    userId: UUID,
    notificationId: UUID,
    notificationDataPatch: NotificationData.Patch
  ): Future[Either[DomainError, NotificationData]] =
    (for {
      originalNotification <- EitherT[Future, DomainError, NotificationData](
        notificationDataRepository
          .getNotificationById(userId, notificationId)
          .map(_.map(Right.apply).getOrElse(Left(EntityNotFoundError("Notification not found!"))))
      )
      notificationDataUpsert = NotificationData.Upsert(
        id = originalNotification.id,
        targetUser = originalNotification.targetUser,
        isInteractive = originalNotification.isInteractive,
        notificationType = originalNotification.notificationType,
        hasBeenInteractedWith = notificationDataPatch.hasBeenInteractedWith,
        hasBeenViewed = notificationDataPatch.hasBeenViewed,
        data = notificationDataPatch.data
      )
      updatedNotificationData <-
        EitherT.right[DomainError](notificationDataRepository.upsertNotification(notificationDataUpsert))
    } yield updatedNotificationData).value

  def sendUserFriendRequestNotification(requestingUser: UUID, targetUser: UUID): Future[PushNotificationResponse] =
    for {
      _ <- Future.unit
      data = Json.obj("requestingUser" -> requestingUser, "targetUser" -> targetUser)
      notificationData = NotificationData.Upsert(
        id = UUID.randomUUID(),
        targetUser = targetUser,
        isInteractive = true,
        hasBeenInteractedWith = false,
        hasBeenViewed = false,
        notificationType = NotificationType.UserFriendRequest,
        data = data,
      )
      sendingUserProfile <- userService.getUserProfile(requestingUser)
      _ <- notificationDataRepository.upsertNotification(notificationData)
      result <- pushNotificationService.sendUserFriendRequestNotification(
        UserFriendRequestedMessage(
          requestingUser,
          targetUser,
          sendingUserProfile.photoUrl.map(url => s"${settingsService.imageHostBaseUrl}/$url").getOrElse("")
        )
      )
    } yield result

  def sendChatRoomMessageSentNotification(
    sendingUserId: UUID,
    targetUserId: UUID,
    roomId: UUID,
    message: String
  ): Future[PushNotificationResponse] = {
    for {
      targetUserProfile <- userService.getUserProfile(targetUserId)
      sendingUserProfile <- userService.getUserProfile(sendingUserId)
      chatMessage = ChatRoomMessageSentMessage(
        sendingUser = sendingUserId,
        targetUser = targetUserId,
        roomId = roomId,
        sendingUserFirstName = sendingUserProfile.firstName.getOrElse(""),
        sendingUserLastName = sendingUserProfile.lastName.getOrElse(""),
        targetUserProfileImageUri =
          targetUserProfile.photoUrl.map(url => s"${settingsService.imageHostBaseUrl}/$url").getOrElse(""),
        sendingUserProfileImageUri =
          sendingUserProfile.photoUrl.map(url => s"${settingsService.imageHostBaseUrl}/$url").getOrElse(""),
        message = message
      )
      result <- pushNotificationService.sendChatRoomMessageSentNotification(chatMessage)
    } yield result
  }

  def addUserCommentedOnPostNotification(
    commentingUser: UUID,
    targetUser: UUID,
    postId: UUID,
    postCreatorId: UUID
  ): Future[Unit] = {
    for {
      unreadNotificationOpt <- notificationDataRepository.getUnreadNotificationForPostWithType(
        targetUser,
        postId,
        NotificationType.UserCommentedOnPost
      )
      _ <- {
        unreadNotificationOpt
          .map { unreadNotification =>
            val alreadyCommentedUsersSet = (unreadNotification.data \ "commentingUsers").get.as[List[String]].toSet
            val newCommentedUsersSet: Set[String] = alreadyCommentedUsersSet + commentingUser.toString
            val data = Json.obj(
              "commentingUsers" -> newCommentedUsersSet.toSeq,
              "targetUser" -> targetUser,
              "postId" -> postId,
              "postCreatorId" -> postCreatorId
            )
            val notificationData = NotificationData.Upsert(
              id = unreadNotification.id,
              targetUser = targetUser,
              isInteractive = false,
              hasBeenInteractedWith = false,
              hasBeenViewed = false,
              notificationType = NotificationType.UserCommentedOnPost,
              data = data,
            )
            notificationDataRepository.upsertNotification(notificationData)
          }
          .getOrElse {
            val data = Json.obj(
              "commentingUsers" -> Seq(commentingUser),
              "targetUser" -> targetUser,
              "postId" -> postId,
              "postCreatorId" -> postCreatorId
            )
            val notificationData = NotificationData.Upsert(
              id = UUID.randomUUID(),
              targetUser = targetUser,
              isInteractive = false,
              hasBeenInteractedWith = false,
              hasBeenViewed = false,
              notificationType = NotificationType.UserCommentedOnPost,
              data = data,
            )
            notificationDataRepository.upsertNotification(notificationData)
          }
      }
    } yield ()
  }

  def addUserLikedPostNotification(likingUser: UUID, targetUser: UUID, postId: UUID): Future[Unit] = {
    for {
      unreadNotificationOpt <- notificationDataRepository.getUnreadNotificationForPostWithType(
        targetUser,
        postId,
        NotificationType.UserLikedPost
      )
      _ <- {
        unreadNotificationOpt
          .map { unreadNotification =>
            val alreadyLikedUsersSet = (unreadNotification.data \ "likingUsers").get.as[List[String]].toSet
            val newLikedUsersSet: Set[String] = alreadyLikedUsersSet + likingUser.toString
            val data = Json.obj("likingUsers" -> newLikedUsersSet.toSeq, "targetUser" -> targetUser, "postId" -> postId)
            val notificationData = NotificationData.Upsert(
              id = unreadNotification.id,
              targetUser = targetUser,
              isInteractive = false,
              hasBeenInteractedWith = false,
              hasBeenViewed = false,
              notificationType = NotificationType.UserLikedPost,
              data = data,
            )
            notificationDataRepository.upsertNotification(notificationData)
          }
          .getOrElse {
            val data = Json.obj("likingUsers" -> Seq(likingUser), "targetUser" -> targetUser, "postId" -> postId)
            val notificationData = NotificationData.Upsert(
              id = UUID.randomUUID(),
              targetUser = targetUser,
              isInteractive = false,
              hasBeenInteractedWith = false,
              hasBeenViewed = false,
              notificationType = NotificationType.UserLikedPost,
              data = data,
            )
            notificationDataRepository.upsertNotification(notificationData)
          }
      }
    } yield ()
  }

  // Send push notification as well as regular notification
  def addParticipantAddedToMeetupNotification(meetupId: UUID, meetupOwnerId: UUID, participantId: UUID): Future[Unit] =
    for {
      _ <- Future.unit
      data = Json.obj("meetupId" -> meetupId, "meetupOwnerId" -> meetupOwnerId, "participantId" -> participantId)
      notificationData = NotificationData.Upsert(
        id = UUID.randomUUID(),
        targetUser = participantId,
        isInteractive = false,
        hasBeenInteractedWith = false,
        hasBeenViewed = false,
        notificationType = NotificationType.ParticipantAddedToMeetup,
        data = data,
      )
      sendingUserProfile <- userService.getUserProfile(participantId)
      _ <- notificationDataRepository.upsertNotification(notificationData)
      result <- pushNotificationService.sendParticipantAddedToMeetupNotification(
        ParticipantAddedToMeetupMessage(
          meetupId,
          meetupOwnerId,
          participantId,
          sendingUserProfile.photoUrl.map(url => s"${settingsService.imageHostBaseUrl}/$url").getOrElse("")
        )
      )
    } yield result

  // Send push notification as well as regular notification
  def addParticipantAddedAvailabilityToMeetupNotification(
    meetupId: UUID,
    meetupOwnerId: UUID,
    participantId: UUID,
    targetUserId: UUID
  ): Future[Unit] =
    for {
      _ <- Future.unit
      data = Json.obj(
        "meetupId" -> meetupId,
        "meetupOwnerId" -> meetupOwnerId,
        "participantId" -> participantId,
        "targetUserId" -> targetUserId
      )
      notificationData = NotificationData.Upsert(
        id = UUID.randomUUID(),
        targetUser = targetUserId,
        isInteractive = false,
        hasBeenInteractedWith = false,
        hasBeenViewed = false,
        notificationType = NotificationType.ParticipantAddedAvailabilityToMeetup,
        data = data,
      )
      sendingUserProfile <- userService.getUserProfile(participantId)
      _ <- notificationDataRepository.upsertNotification(notificationData)
      result <- pushNotificationService.sendParticipantAddedAvailabilityToMeetupNotification(
        ParticipantAddedAvailabilityToMeetupMessage(
          meetupId,
          meetupOwnerId,
          targetUserId,
          s"${sendingUserProfile.firstName.getOrElse("")} ${sendingUserProfile.lastName.getOrElse("")}",
          participantId,
          sendingUserProfile.photoUrl.map(url => s"${settingsService.imageHostBaseUrl}/$url").getOrElse("")
        )
      )
    } yield result

  def addMeetupDecisionNotification(
    meetupId: UUID,
    meetupOwnerId: UUID,
    participantId: UUID,
    hasAccepted: Boolean
  ): Future[Unit] =
    for {
      _ <- {
        val data = Json.obj(
          "meetupId" -> meetupId,
          "meetupOwnerId" -> meetupOwnerId,
          "participantId" -> participantId,
          "hasAccepted" -> hasAccepted
        )
        val notificationData = NotificationData.Upsert(
          id = UUID.randomUUID(),
          targetUser = meetupOwnerId,
          isInteractive = false,
          hasBeenInteractedWith = false,
          hasBeenViewed = false,
          notificationType = NotificationType.MeetupDecision,
          data = data,
        )
        notificationDataRepository.upsertNotification(notificationData)
      }
    } yield ()

  def meetupReminderNotification(meetupId: UUID, meetupName: String, targetUser: UUID): Future[Unit] =
    for {
      _ <- Future.unit
      meetupReminderMessage = MeetupReminderMessage(targetUser, meetupId, meetupName)
      result <- pushNotificationService.sendMeetupReminderNotification(meetupReminderMessage)
    } yield result
}
