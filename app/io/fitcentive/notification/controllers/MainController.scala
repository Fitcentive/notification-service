package io.fitcentive.notification.controllers

import akka.actor.ActorSystem
import play.api.mvc._

import javax.inject._
import scala.concurrent.ExecutionContext

@Singleton
class MainController @Inject() (cc: ControllerComponents, actorSystem: ActorSystem)(implicit exec: ExecutionContext)
  extends AbstractController(cc) {}
