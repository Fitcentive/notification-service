package io.fitcentive.notification.domain.notification

import play.api.libs.json.{JsString, Writes}

trait NotificationType {
  def stringValue: String
}

object NotificationType {

  def apply(notificationType: String): NotificationType =
    notificationType match {
      case UserFriendRequest.stringValue   => UserFriendRequest
      case UserCommentedOnPost.stringValue => UserCommentedOnPost
      case UserLikedPost.stringValue       => UserLikedPost
      case _                               => throw new Exception("Unexpected notification type")
    }

  implicit lazy val writes: Writes[NotificationType] = {
    {
      case UserFriendRequest   => JsString(UserFriendRequest.stringValue)
      case UserCommentedOnPost => JsString(UserCommentedOnPost.stringValue)
      case UserLikedPost       => JsString(UserLikedPost.stringValue)
    }
  }

  case object UserFriendRequest extends NotificationType {
    val stringValue: String = "UserFriendRequest"
  }

  case object UserCommentedOnPost extends NotificationType {
    val stringValue: String = "UserCommentedOnPost"
  }

  case object UserLikedPost extends NotificationType {
    val stringValue: String = "UserLikedPost"
  }

}
