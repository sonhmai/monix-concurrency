package reactive

import monix.execution.Scheduler
import monix.reactive.Observer
import monix.reactive.observers.Subscriber

object SubscriberExample extends App {
  // convert plain observer to subscriber
  def convertObserser[T](observer: Observer[T], scheduler: Scheduler): Subscriber[T] =
    Subscriber(observer, scheduler)

  val globalSched = monix.execution.Scheduler.Implicits.global
  val observer = Observer.dump("0")
  val subscriber = convertObserser(observer, globalSched)

  // build a Subscriber that does nothing but logs onError
  val subscriberDummy = Subscriber.empty[Int](globalSched)



}
