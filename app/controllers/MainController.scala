package controllers

import javax.inject._
import akka.actor.ActorSystem
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class MainController @Inject() (cc: ControllerComponents, actorSystem: ActorSystem)(implicit exec: ExecutionContext)
  extends AbstractController(cc) {}
