package scala.concurrency

import monix.execution.Cancelable
import monix.execution.ExecutionModel.AlwaysAsyncExecution
import monix.reactive.Observable
import monix.execution.Scheduler


object Application {
  def main(args: Array[String]): Unit = {
    val observable: Observable[Int] = Observable.fromIterable(List(1, 2, 3))

    val defaultScheduler = Scheduler.Implicits.global
    val forkJoinScheduler = Scheduler.computation(parallelism = 2)
    val ioScheduler = Scheduler.io(executionModel = AlwaysAsyncExecution)

    val cancellable: Cancelable = observable
      .map(mapper)
      .map(mapper)
      .subscribeOn(ioScheduler)
      .subscribe()(ioScheduler)

    Thread.sleep(1000)
  }

  private def mapper(i: Any): String = {
    val output = s"x$i"
    println(s"Thread-${Thread.currentThread().getName}. input: $i, output: $output")
    output
  }
}

