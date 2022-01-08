package reactive

import monix.eval.Task
import monix.execution.ExecutionModel.AlwaysAsyncExecution
import monix.execution.Scheduler
import monix.execution.schedulers.TracingScheduler
import monix.reactive.Observable

object ObservableDownstream extends App {

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

  /**
   * difference btw executeOn and subscribeOn?
   *
   *
   */
  observable1
    .executeOn(tracingScheduler)

  observable2
    .subscribeOn(tracingScheduler)

}
