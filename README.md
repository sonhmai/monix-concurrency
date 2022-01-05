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