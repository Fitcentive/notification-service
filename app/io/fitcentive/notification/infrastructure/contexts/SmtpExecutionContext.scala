package io.fitcentive.notification.infrastructure.contexts

import play.api.libs.concurrent.CustomExecutionContext

import javax.inject.{Inject, Singleton}

@Singleton
class SmtpExecutionContext @Inject() (actorSystem: akka.actor.ActorSystem)
  extends CustomExecutionContext(actorSystem, "contexts.smtp-execution-context")
