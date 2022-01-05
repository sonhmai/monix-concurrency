# monix-concurrency
Exploring monix and concurrency

# FAQ

- What is the difference between Subscriber (Observable.subscribe...) and Consumer (Observable.consume...)?
   - https://monix.io/docs/current/reactive/consumer.html 
   - A Consumer specifies how to consumer observables, is a factory of Subscribers with a completion callback attached
   - there is no standard way to describe observers that will produce a final result.
   - Subscriber is a data type that’s an Observer with a Scheduler attached because in order to do anything with an Observer, we always need a Scheduler
   
