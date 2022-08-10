package io.fitcentive.notification.api

import cats.data.EitherT
import cats.implicits.toTraverseOps
import io.fitcentive.notification.domain.email.{EmailContents, EmailFrom}
import io.fitcentive.notification.domain.errors.EmailError
import io.fitcentive.notification.domain.notification.{NotificationData, NotificationType}
import io.fitcentive.notification.domain.push.{NotificationDevice, PushNotificationResponse}
import io.fitcentive.notification.domain.push.messages.{ChatRoomMessageSentMessage, UserFollowRequestedMessage}
import io.fitcentive.notification.services.{EmailService, PushNotificationService, UserService}
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
)(implicit ec: ExecutionContext) {

  // todo - make this a config option
  val imageHostBaseUrl: String = "https://api.vid.app/api/gateway/image"

  def sendEmail(emailId: String, token: String): Future[Either[EmailError, Unit]] =
    emailService.sendEmail(
      EmailContents(
        from = EmailFrom("Fitcentive", "no-reply@fitcentive.io"),
        to = emailId,
        subject = "Email Verification Token",
        body = s"Here is your token: $token"
      )
    )

  def updateUserFollowRequestNotificationData(
    targetUserId: UUID,
    isApproved: Boolean
  ): Future[Either[DomainError, NotificationData]] = {
    (for {
      mostRecentUserFollowRequestNotification <- EitherT[Future, DomainError, NotificationData](
        notificationDataRepository
          .getMostRecentNotificationOfTypeForUser(targetUserId, NotificationType.UserFollowRequest)
          .map(_.map(Right.apply).getOrElse(Left(EntityNotFoundError("Notification not found!"))))
      )
      newData = Json.obj(
        "requestingUser" -> (mostRecentUserFollowRequestNotification.data \ "requestingUser").get,
        "targetUser" -> targetUserId,
        "isApproved" -> isApproved,
      )
      notificationDataUpsert = NotificationData.Upsert(
        id = mostRecentUserFollowRequestNotification.id,
        targetUser = targetUserId,
        isInteractive = mostRecentUserFollowRequestNotification.isInteractive,
        notificationType = mostRecentUserFollowRequestNotification.notificationType,
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

  def getUserNotifications(userId: UUID): Future[Seq[NotificationData]] =
    notificationDataRepository.getUserNotifications(userId)

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

  def sendUserFollowRequestNotification(requestingUser: UUID, targetUser: UUID): Future[PushNotificationResponse] =
    for {
      _ <- Future.unit
      data = Json.obj("requestingUser" -> requestingUser, "targetUser" -> targetUser)
      notificationData = NotificationData.Upsert(
        id = UUID.randomUUID(),
        targetUser = targetUser,
        isInteractive = true,
        hasBeenInteractedWith = false,
        hasBeenViewed = false,
        notificationType = NotificationType.UserFollowRequest,
        data = data,
      )
      _ <- notificationDataRepository.upsertNotification(notificationData)
      result <- pushNotificationService.sendUserFollowRequestNotification(
        UserFollowRequestedMessage(requestingUser, targetUser)
      )
    } yield result

  def sendChatRoomMessageSentNotification(
    sendingUser: UUID,
    targetUserId: UUID,
    roomId: UUID,
    message: String
  ): Future[PushNotificationResponse] = {
    for {
      targetUserProfile <- userService.getUserProfile(targetUserId)
      chatMessage = ChatRoomMessageSentMessage(
        sendingUser = sendingUser,
        targetUser = targetUserId,
        roomId = roomId,
        targetUserFirstName = targetUserProfile.firstName.getOrElse(""),
        targetUserLastName = targetUserProfile.lastName.getOrElse(""),
        targetUserProfileImageUri = targetUserProfile.photoUrl.map(url => s"$imageHostBaseUrl/$url").getOrElse(""),
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
            val alreadyCommentedUsersSet = (unreadNotification.data \ "commentingUser").get.as[List[String]].toSet
            val newCommentedUsersSet: Set[String] = alreadyCommentedUsersSet + commentingUser.toString
            val data = Json.obj(
              "commentingUsers" -> newCommentedUsersSet.toSeq,
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
}
