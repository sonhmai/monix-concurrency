package concurrency.mdc

import org.log4s.MDC

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object ExampleWrongLog4s extends App {
  private val logger = org.log4s.getLogger
  implicit val ec: ExecutionContext = ExecutionContext.global
  private val requestIdKey = "requestId"

  def req(requestId: String, userName: String): Future[Unit] = Future {
    MDC.put(requestIdKey, requestId)
    logger.info(
      s"Request ${MDC.get(requestIdKey)}: Received a request to create a user $userName"
    )
    // more flatmaps to add async boundaries
  }.flatMap(_ => Future(()).flatMap(_ => Future()))
    .flatMap(_ => registerUser(userName))

  def registerUser(name: String): Future[Unit] = Future {
    // business logic
    logger.info(
      s"Request ${MDC.get(requestIdKey)}: Registering a new user named $name"
    )
  }

  val requests = List(req("1", "Clark"), req("2", "Bruce"), req("3", "Diana"))
  Await.result(Future.sequence(requests), Duration.Inf)

  //=> 3: Received a request to create a user Diana
  //=> 2: Received a request to create a user Bruce
  //=> 1: Received a request to create a user Clark
  //=> 1: Registering a new user named Clark
  //=> 2: Registering a new user named Bruce
  //=> 2: Registering a new user named Diana
}
