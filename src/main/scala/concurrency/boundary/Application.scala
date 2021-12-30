package concurrency.boundary

import monix.eval.Task
import monix.execution.ExecutionModel.AlwaysAsyncExecution
import monix.execution.{Cancelable, Scheduler}
import monix.reactive.Observable

object Application {
  def main(args: Array[String]): Unit = {
    val observable: Observable[Int] = Observable.fromIterable(List(1, 2, 3))

    val defaultScheduler = Scheduler.Implicits.global
    val forkJoinScheduler = Scheduler.computation(parallelism = 2)
    val ioScheduler = Scheduler.io(executionModel = AlwaysAsyncExecution)

    val cancellable: Cancelable = observable
      .map(mapper)
      .executeAsync
      .mapEval(mapperEval)
      .subscribeOn(ioScheduler)
      .subscribe()(ioScheduler)

    Thread.sleep(1000)
  }

  private def mapper(i: Any): String = {
    val output = s"x$i"
    println(
      s"Thread-${Thread.currentThread().getName}. input: $i, output: $output"
    )
    output
  }

  private def mapperEval(i: Any): Task[String] = {
    Task.evalAsync {
      println(s"Thread-${Thread.currentThread().getName}. $i - running async")
      s"$i - running async"
    }
  }
}
