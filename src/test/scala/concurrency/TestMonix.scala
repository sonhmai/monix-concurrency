package concurrency

import monix.execution.Cancelable
import monix.reactive.Observable
import org.scalatest.wordspec.AnyWordSpec


class TestMonix extends AnyWordSpec {
  "should be able to run" in {
    val observable: Observable[Int] = Observable.fromIterable(List(1, 2, 3))

    import monix.execution.Scheduler.Implicits.global

    val cancellable: Cancelable = observable
      .map(_ + 1)
      .dump(s"${Thread.currentThread().getName}. out: ")
      .subscribe()

  }
}

