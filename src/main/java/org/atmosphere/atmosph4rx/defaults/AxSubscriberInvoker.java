/**
 * Copyright 2018-2020 Async-IO.org
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.atmosphere.atmosph4rx.defaults;

import org.atmosphere.atmosph4rx.AxSocket;
import org.atmosphere.atmosph4rx.AxSubscriber;
import org.atmosphere.atmosph4rx.AxSubscription;
import org.atmosphere.atmosph4rx.core.AxMetaData;
import org.atmosphere.atmosph4rx.defaults.DefaultAxRouter.AxProcessor;
import org.reactivestreams.Subscription;
import reactor.core.publisher.FluxProcessor;

class AxSubscriberInvoker<T> implements AxSubscriber<T> {

    private final AxSubscriber<T> axSubscriber;
    private final AxMetaData metaData;
    private final AxProcessor<FluxProcessor<String, String>> outputProcessor;

    public AxSubscriberInvoker(AxSubscriber<T> axSubscriber, AxMetaData metaData, AxProcessor<FluxProcessor<String, String>> outputProcessor) {
        this.axSubscriber = axSubscriber;
        this.metaData = metaData;
        this.outputProcessor = outputProcessor;
    }

    @Override
    public void onNext(T payload) {
        axSubscriber.onNext(payload);

        AxSocket<FluxProcessor<String, String>, String> single = new AxSocket<FluxProcessor<String, String>, String>() {

            @Override
            public AxSocket<FluxProcessor<String, String>, String> publish(String message) {
                toProcessor().onNext(message);
                return this;
            }

            @Override
            public FluxProcessor<String, String> toProcessor() {
                return outputProcessor.out();
            }

            @Override
            public String id() {
                return outputProcessor.id();
            }

            @Override
            public String toString() {
                return "$classname{}";
            }
        };

        axSubscriber.onNext(single);
        axSubscriber.onNext(single, (String) payload);
    }

    @Override
    public void onSubscribe(Subscription s) {
        s.request(Integer.MAX_VALUE);
        axSubscriber.onSubscribe(s);

        onSubscribe(new AxSubscription(s, metaData, outputProcessor));
    }

    @Override
    public void onComplete() {
        axSubscriber.onComplete();
    }

    @Override
    public void onError(Throwable t) {
        axSubscriber.onError(t);
    }

    public void onSubscribe(AxSubscription s) {
        axSubscriber.onSubscribe(s);
    }

    public void onNext(byte[] payload) {
        axSubscriber.onNext(payload);
    }

    public <U extends FluxProcessor<? super String, ? super String>> void onNext(AxSocket<U, String> single, String payload) {
        axSubscriber.onNext(single, payload);
    }

    @Override
    public <U extends FluxProcessor<? super String, ? super String>, V> void onNext(AxSocket<U, V> single) {
        axSubscriber.onNext(single);         
    }

    public <U extends FluxProcessor<? super String, ? super String>> void onNext(AxSocket<U, byte[]> single, byte[] payload) {
        axSubscriber.onNext(single, payload);
    }
}
