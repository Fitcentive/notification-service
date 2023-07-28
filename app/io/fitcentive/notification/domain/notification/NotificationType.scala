package io.fitcentive.notification.domain.notification

import play.api.libs.json.{JsString, Writes}

trait NotificationType {
  def stringValue: String
}

object NotificationType {

  def apply(notificationType: String): NotificationType =
    notificationType match {
      case UserFriendRequest.stringValue                    => UserFriendRequest
      case UserCommentedOnPost.stringValue                  => UserCommentedOnPost
      case UserLikedPost.stringValue                        => UserLikedPost
      case MeetupDecision.stringValue                       => MeetupDecision
      case ParticipantAddedToMeetup.stringValue             => ParticipantAddedToMeetup
      case ParticipantAddedAvailabilityToMeetup.stringValue => ParticipantAddedAvailabilityToMeetup
      case MeetupLocationChanged.stringValue                => MeetupLocationChanged
      case UserAttainedNewAchievementMilestone.stringValue  => UserAttainedNewAchievementMilestone
      case _                                                => throw new Exception("Unexpected notification type")
    }

  implicit lazy val writes: Writes[NotificationType] = {
    {
      case UserFriendRequest                    => JsString(UserFriendRequest.stringValue)
      case UserCommentedOnPost                  => JsString(UserCommentedOnPost.stringValue)
      case UserLikedPost                        => JsString(UserLikedPost.stringValue)
      case MeetupDecision                       => JsString(MeetupDecision.stringValue)
      case ParticipantAddedToMeetup             => JsString(ParticipantAddedToMeetup.stringValue)
      case ParticipantAddedAvailabilityToMeetup => JsString(ParticipantAddedAvailabilityToMeetup.stringValue)
      case MeetupLocationChanged                => JsString(MeetupLocationChanged.stringValue)
      case UserAttainedNewAchievementMilestone  => JsString(UserAttainedNewAchievementMilestone.stringValue)
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

  case object MeetupDecision extends NotificationType {
    val stringValue: String = "MeetupDecision"
  }

  case object ParticipantAddedToMeetup extends NotificationType {
    val stringValue: String = "ParticipantAddedToMeetup"
  }

  case object ParticipantAddedAvailabilityToMeetup extends NotificationType {
    val stringValue: String = "ParticipantAddedAvailabilityToMeetup"
  }

  case object MeetupLocationChanged extends NotificationType {
    val stringValue: String = "MeetupLocationChanged"
  }

  case object UserAttainedNewAchievementMilestone extends NotificationType {
    val stringValue: String = "UserAttainedNewAchievementMilestone"
  }

}
