# Welcome to the Atmosph4rX Framework!
## The Reactive Streams Framework for Java

SpringBoot 2 + Project Reactor + Atmosphere = *Atmosph4rX*. [Reactive Streams](http://www.reactive-streams.org/) made easy!

Atmosph4rX is a complete rewrite of the [Atmosphere Framework](https://github.com/Atmosphere/atmosphere). All the functionalities are or will be ported to Atmosph4rX.

## ROADMAP
* Work in Progress

## As simple as
```java    
       @ReactTo("/mySubscriber")
       public final static class RxTest9 implements AxSubscriber<String> {
   
           @Topic("/message")
           private MultiLinkProcessor<String> processor;
   
           @Override
           public void onSubscribe(AxSubscription s) {
               // Add a Link to the Processor
               // A Link represent a unique connection.
               processor.subscribe(s.link());
           }
   
           @Override
           public void onNext(String next) {
                // Push data to all Link that subscribed to the Processor
                processor.publish(next);
           }
   
           @Override
           public void onComplete() {
           }
   
           @Override
           public void onError(Throwable throwable) {
           }
   
       }
```

## How to install

```xml
     <dependency>
         <groupId>org.atmosphere</groupId>
         <artifactId>atmosph4rx</artifactId>
         <version>4.0.0-SNAPSHOT</version>
      </dependency>
```
