package concurrency.mdc

import monix.eval.{Task, TaskLocal}
import monix.execution.ExecutionModel.AlwaysAsyncExecution
import monix.execution.Scheduler
import monix.execution.schedulers.TracingScheduler
import org.log4s.MDC

object TaskWithLocalAndIOTracingSched extends App {
  private val logger = org.log4s.getLogger
  implicit val s: Scheduler = TracingScheduler(
    Scheduler.io(
      name = "IO-Bounded",
      daemonic = false,
      executionModel = AlwaysAsyncExecution
    )
  )
  private val requestIdKey = "requestId"

  def req(requestId: String, userName: String): Task[Unit] = TaskLocal.isolate {
    Task {
      MDC.put(requestIdKey, requestId)
      logger.info(
        s"Request $requestId: Received a request to create a user $userName"
      )
      // more flatmaps to add async boundaries
    }
      .flatMap(_ =>
        Task(())
          .flatMap(_ => Task())
      )
      .flatMap(_ => registerUser(userName))
  }

  def registerUser(name: String): Task[Unit] = Task {
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

  Task.parSequence(requests).runSyncUnsafe()
}
