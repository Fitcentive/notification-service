package io.fitcentive.notification.domain.email

case class EmailContents(from: EmailFrom, to: String, subject: String, body: String)
