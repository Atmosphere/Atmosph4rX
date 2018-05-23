## Welcome to the Atmosph4rX Framework!

SpringBoot 2 + Project Reactor + Atmosphere = Atmosph4rX

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
