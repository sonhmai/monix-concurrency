package concurrency.mdc

import monix.execution.Scheduler
import monix.execution.misc.Local
import org.log4s.MDC

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object FutureWithLocalAndTracingSched extends App {
  private val logger = org.log4s.getLogger
  implicit val s: Scheduler = Scheduler.traced
  private val requestIdKey = "requestId"

  def req(requestId: String, userName: String): Future[Unit] = Local.isolate {
    Future {
      MDC.put(requestIdKey, requestId)
      logger.info(
        s"Request $requestId: Received a request to create a user $userName"
      )
      // more flatmaps to add async boundaries
    }.flatMap(_ => Future(()).flatMap(_ => Future()))
      .flatMap(_ => registerUser(userName))
  }

  def registerUser(name: String): Future[Unit] = Future {
    // business logic
    logger.info(
      s"Registering a new user named $name"
    )
  }

  val requests = List(
    req("1", "Clark"),
    req("2a", "Bruce"),
    req("2b", "Daniel"),
    req("3", "Diana")
  )

  Await.result(Future.sequence(requests), Duration.Inf)
}
