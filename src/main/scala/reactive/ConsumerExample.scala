package reactive

import monix.execution.Ack.{Continue, Stop}
import monix.execution.cancelables.AssignableCancelable
import monix.execution.{Ack, Callback, Cancelable, Scheduler}
import monix.reactive.{Consumer, Observable, Observer}
import monix.reactive.observers.Subscriber

object ConsumerExample extends App {
  import monix.reactive.Observable

  private def func(scheduler: Scheduler, cancelable: Cancelable,
                   cb: Callback[Throwable, Long]): Observer[Int] = {
    new Observer.Sync[Int] {
      override def onNext(elem: Int): Ack = {
        println(elem)
        if (elem == 10) Stop else Continue
      }

      override def onError(ex: Throwable): Unit =
        println(ex)

      override def onComplete(): Unit =
        println("completed")
    }
  }

  val sumConsumer: Consumer[Int, Long] =
    Consumer.create[Int, Long](func)

  private val globalScheduler = monix.execution.Scheduler.Implicits.global

  Observable.fromIterable(0 until 10000)
    .consumeWith(sumConsumer)
    .runToFuture(globalScheduler)
    .foreach(r => println(s"Result: $r"))(globalScheduler)

}


object CreatingConsumer {

  // low-level way of implementing the Consumer is to simply implement that trait.
  // implement a Consumer that calculates the sum of all the Int elements of a stream
  object ImplementingLowLevelInterface {
    import monix.execution.Scheduler.Implicits.global

    val sumConsumer: Consumer[Int,Long] =
      new Consumer[Int,Long] {
        def createSubscriber(cb: Callback[Throwable, Long], s: Scheduler) = {
          val out = new Subscriber.Sync[Int] {
            private var sum = 0L

            def onNext(elem: Int): Ack = {
              sum += elem
              Continue
            }

            def onComplete(): Unit = {
              // We are done so we can signal the final result
              cb.onSuccess(sum)
            }

            def onError(ex: Throwable): Unit = {
              // Error happened, so we signal the error
              cb.onError(ex)
            }

            override implicit def scheduler: Scheduler = s
          }

          // Returning a tuple of our subscriber and a dummy
          // AssignableCancelable because we don't intend to use it
          (out, AssignableCancelable.dummy)
        }
      }

    // USAGE:
    import monix.reactive.Observable

    Observable.fromIterable(0 until 10000)
      .consumeWith(sumConsumer)
      .runToFuture
      .foreach(r => println(s"Result: $r"))
    //=> Result: 49995000
  }



  /**
   * For a more refined experience when creating consumers,
   *  one can use the Consumer.create builder
   *
   * Using the create builder is similar to implementing the Consumer trait directly.
   * Differences:
   *  1. the factory function gets surrounded with try/catch and in case of failure,
   *    the error is raised by means of the callback and the stream gets canceled
   *  2. a cancelable instance gets automatically injected; calling cancel on it
   *    will attempt to cancel the stream, following the rules of cancelables returned by observable subscriptions and its usage remains optional
   */
  object Create {
    val sumConsumer: Consumer[Int,Long] =
      Consumer.create[Int,Long] { (scheduler, cancelable, callback) =>
        new Observer.Sync[Int] {
          private var sum = 0L

          def onNext(elem: Int): Ack = {
            sum += elem
            Continue
          }

          def onComplete(): Unit = {
            // We are done so we can signal the final result
            callback.onSuccess(sum)
          }

          def onError(ex: Throwable): Unit = {
            // Error happened, so we signal the error
            callback.onError(ex)
          }
        }
      }
  }

  object FromObserver {

  }
}



object PrebuiltConsumers {
  def consumeUntilCompletion(): Unit = {
    import monix.execution.Scheduler.Implicits.global

    Observable.range(0, 4)
      .dump("O")
      .consumeWith(Consumer.complete)
      .runToFuture
      .foreach(_ => println("Consumer completed"))
    //=> 0: O-->0
    //=> 1: O-->1
    //=> 2: O-->2
    //=> 3: O-->3
    //=> 4: O completed
    //=> Consumer completed
  }
}