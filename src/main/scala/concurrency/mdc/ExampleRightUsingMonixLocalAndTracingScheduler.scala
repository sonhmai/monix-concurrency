package concurrency.mdc

import monix.execution.Scheduler
import org.log4s.MDC

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object ExampleRightUsingMonixLocalAndTracingScheduler extends App {
  private val logger = org.log4s.getLogger
  implicit val s: ExecutionContext = Scheduler.traced
  private val requestIdKey = "requestId"

  def req(requestId: String, userName: String): Future[Unit] = Future {
    MDC.put(requestIdKey, requestId)
    logger.info(
      s"Request $requestId: Received a request to create a user $userName"
    )
    // more flatmaps to add async boundaries
  }.flatMap(_ => Future(()).flatMap(_ => Future()))
    .flatMap(_ => registerUser(userName))

  def registerUser(name: String): Future[Unit] = Future {
    // business logic
    logger.info(
      s"Registering a new user named $name"
    )
  }

  val requests = List(
    req("1", "Clark"),
    req("2a", "Bruce"),
    req("2b", "Bruce"),
    req("3", "Diana"))
  Await.result(Future.sequence(requests), Duration.Inf)
}
