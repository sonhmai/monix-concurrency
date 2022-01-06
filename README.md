# monix-concurrency
Exploring monix and concurrency

## Concurrency Articles
- https://blog.ometer.com/2011/07/24/callbacks-synchronous-and-asynchronous/

## ThreadLocal
- [ThreadLocal deep dive](https://titanwolf.org/Network/Articles/Article?AID=eec617f7-8a0c-4adc-becf-b898c5d1bb5b)

ThreadLocal
- avoid synchronization by increasing memory usage (maintain copy of concurrently
accessed data) for each thread.

How does ThreadLocal maintain a copy of variables for each thread? 
- In fact, the implementation idea is very simple. There is a Map in the ThreadLocal class, which is used to store a copy of each thread's variables.

# FAQ

- What is the difference between Subscriber (Observable.subscribe...) and Consumer (Observable.consume...)?
   - https://monix.io/docs/current/reactive/consumer.html 
   - A Consumer specifies how to consumer observables, is a factory of Subscribers with a completion callback attached
   - there is no standard way to describe observers that will produce a final result.
   - Subscriber is a data type that’s an Observer with a Scheduler attached because in order to do anything with an Observer, we always need a Scheduler
   
- What are the Observable APIs that we can use Observer and Subscriber on?

# Sinks

1. An observer can subscribe to at most one observable and cannot subscribe to multiple observables.
2. Observer (and Subscriber) instances are stateful. For example, an Observer sum all the Ints
in the stream and produce a Long, it must maintain a private var as state of the sum.
3. Consumer = factory(Subscriber + attached callback)
4. 