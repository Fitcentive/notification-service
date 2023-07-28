package io.fitcentive.notification.domain.push.messages

import com.google.firebase.messaging.Notification
import io.fitcentive.notification.domain.push.PushNotificationEventMessage

import scala.jdk.CollectionConverters._
import java.util
import java.util.UUID

case class UserAttainedNewAchievementMilestoneMessage(
  targetUser: UUID,
  milestoneName: String,
  milestoneCategory: String
) extends PushNotificationEventMessage {

  val notification: Notification =
    Notification
      .builder()
      .setTitle(s"You have achieved a new milestone!")
      .setBody("Click here to find out more")
      .build()

  def toJavaMap: util.Map[String, String] =
    Seq(
      "type" -> "user_attained_new_achievement_milestone",
      "targetUser" -> targetUser.toString,
      "milestoneName" -> milestoneName,
      "milestoneCategory" -> milestoneCategory,
    ).toMap.asJava
}
