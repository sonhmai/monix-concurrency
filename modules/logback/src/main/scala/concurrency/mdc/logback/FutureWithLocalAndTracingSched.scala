package concurrency.mdc.logback

import concurrency.mdc.adapter.MonixLogbackMDCAdapter
import monix.execution.Scheduler
import monix.execution.misc.Local
import org.log4s.MDC

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object FutureWithLocalAndTracingSched extends App {

  MonixLogbackMDCAdapter.initialize()

  private val logger = org.log4s.getLogger
  implicit val s: Scheduler = Scheduler.traced
  private val requestIdKey = "requestId"

  def req(requestId: String, userName: String): Future[Unit] = Local.isolate {
    Future {
      MDC.put(requestIdKey, requestId)
      logger.info(
        s"Request $requestId, MDC ${MDC.get(requestIdKey)}:: Received a request to create a user $userName"
      )
      // more flatmaps to add async boundaries
    }.flatMap(_ => Future(()).flatMap(_ => Future()))
      .flatMap(_ => registerUser(userName))
  }


  def registerUser(name: String): Future[Unit] = Future {
    // business logic
    logger.info(
      s"MDC ${MDC.get(requestIdKey)}: Registering a new user named $name"
    )
  }

  val requests = List(
    req("1", "Clark"),
    req("2a", "Bruce"),
    req("2b", "Daniel"),
    req("3", "Diana"))

  Await.result(Future.sequence(requests), Duration.Inf)
}
