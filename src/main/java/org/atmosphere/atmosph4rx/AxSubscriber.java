/**
 * Copyright 2018 Async-IO.org
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
package org.atmosphere.atmosph4rx;

import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public interface AxSubscriber<T> extends Subscriber<T> {

    @Override
    default void onNext(T payload) {
    }

    @Override
    default void onSubscribe(Subscription s) {
        s.request(Integer.MAX_VALUE);
    }

    @Override
    default void onComplete() {
    }

    @Override
    default void onError(Throwable t) {
    }

    default void onSubscribe(AxSubscription s) {
    }

    default void onNext(byte[] payload) {
    }

    default <U extends Processor<? super String, ? super String>> void onNext(Link<U, String> single, String payload) {
    }

    default <U extends Processor<? super String, ? super String>, V> void onNext(Link<U, V> single) {
    }

    default <U extends Processor<? super String, ? super String>> void onNext(Link<U, byte[]> single, byte[] payload) {
    }

}
