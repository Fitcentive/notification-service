package io.fitcentive.notification.domain.notification

import play.api.libs.json.{JsString, Writes}

trait NotificationType {
  def stringValue: String
}

object NotificationType {

  def apply(notificationType: String): NotificationType =
    notificationType match {
      case UserFollowRequest.stringValue => UserFollowRequest
      case _                             => throw new Exception("Unexpected notification type")
    }

  implicit lazy val writes: Writes[NotificationType] = {
    {
      case UserFollowRequest => JsString(UserFollowRequest.stringValue)
    }
  }

  case object UserFollowRequest extends NotificationType {
    val stringValue: String = "UserFollowRequest"
  }

}
