package reactive

import monix.eval.Task
import monix.execution.ExecutionModel.AlwaysAsyncExecution
import monix.execution.Scheduler
import monix.execution.schedulers.TracingScheduler
import monix.reactive.Observable

object ObservableExample extends App {

  val globalScheduler = Scheduler.Implicits.global
  val ioScheduler = Scheduler.io()
  val computationScheduler = Scheduler.computation()

  private val sched = Scheduler.io(
    "tracing",
    daemonic = false,
    executionModel = AlwaysAsyncExecution
  )
  val tracingScheduler = TracingScheduler(sched)

  println(
    s"We begin in: ${Thread.currentThread().getThreadGroup}-${Thread.currentThread().getName}"
  )

  val observable1: Observable[Long] = Observable.range(1, 5)
  val observable2: Observable[Long] = Observable.range(5, 10)

  observable1
    .flatMap(getCustomerId)
    .doOnNext(str =>
      Task.eval(
        Task
          .eval {
            Thread.sleep(3000)
            printElementAndThread(str, "doOnNext")
          }
      )
    )
    .flatMap(getDataFromNetwork)
    .subscribe()(tracingScheduler)

  observable2
    .flatMap(getCustomerId)
    .executeAsync
    .doOnNext(str =>
      Task.eval {
        Thread.sleep(3000)
        printElementAndThread(str, "doOnNext")
      }
    )
    .executeAsync
    .flatMap(getDataFromNetwork)
    .subscribe()(tracingScheduler)

  private def printElementAndThread(in: Any, prefix: String = ""): Unit =
    println(
      s"Group(${Thread.currentThread().getThreadGroup.getName})-" +
        s"Thread(${Thread.currentThread().getName})-Action<$prefix>: $in"
    )

  private def getDataFromNetwork(in: Any): Observable[String] = {
    printElementAndThread(in, "getDataFromNetwork") // side-effect
    Thread.sleep(500) // side-effect
    Observable.eval(in.toString)
  }

  private def getCustomerId(in: Any): Observable[String] = {
    printElementAndThread(in, "getCustomerId") // side-effect
    Thread.sleep(1000) // side-effect
    Observable.eval(in.toString)
  }

  Thread.sleep(50000)
}
