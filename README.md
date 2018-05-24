# Welcome to the Atmosph4rX Framework!
## The WebSocket Reactive Streams Framework for Java

[SpringBoot 2](https://projects.spring.io/spring-boot/) + [Project Reactor](https://projectreactor.io/) + [Atmosphere](https://github.com/Atmosphere/atmosphere) = **Atmosph4rX**. 

[Reactive Streams](http://www.reactive-streams.org/) made easy!

Atmosph4rX is a complete rewrite of the [Atmosphere Framework](https://github.com/Atmosphere/atmosphere). All the functionalities are or will be ported to Atmosph4rX.

## ROADMAP
* Work in Progress. See [ROADMAP](./ROADMAP.md) for more details.

## As simple as

### Reactive Streams Subscriber
```java    
       @ReactTo("/mySubscriber")
       public final class MySubscriber implements AxSubscriber<String> {
   
           @Topic("/message")
           private MultiLinkProcessor<String> processor;
   
           @Override
           public void onSubscribe(AxSubscription s) {
               processor.subscribe(s.link());
           }
   
           @Override
           public void onNext(String next) {
                // Push data to all {@link Subscriber}s. Subscribers include Link, AxSubscriber or Subscriber 
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

### Annotation based
```java    
       @ReactTo("/foo")
       public final class MyPoJo {
   
           @Topic("/message")
           private MultiLinkProcessor<String> processor;
   
           @Open
           public void open() {
           }
        
           @Close
           public void close() {
           }
        
           @Message
           public void on(String message) {
              processor.publish(next);
           }
        
           @Error
           public void error() {
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
