package controllers

import javax.inject._
import akka.actor.ActorSystem
import api.NotificationApi
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future, Promise}

/**
  * This controller creates an `Action` that demonstrates how to write
  * simple asynchronous code in a controller. It uses a timer to
  * asynchronously delay sending a response for 1 second.
  *
  * @param cc standard controller components
  * @param actorSystem We need the `ActorSystem`'s `Scheduler` to
  * run code after a delay.
  * @param exec We need an `ExecutionContext` to execute our
  * asynchronous code.  When rendering content, you should use Play's
  * default execution context, which is dependency injected.  If you are
  * using blocking operations, such as database or network access, then you should
  * use a different custom execution context that has a thread pool configured for
  * a blocking API.
  */
@Singleton
class MainController @Inject() (notificationApi: NotificationApi, cc: ControllerComponents, actorSystem: ActorSystem)(
  implicit exec: ExecutionContext
) extends AbstractController(cc) {}
