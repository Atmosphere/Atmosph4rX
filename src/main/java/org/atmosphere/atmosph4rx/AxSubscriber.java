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
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * An extended {@link Subscriber} with support extended functionality.
 *
 * @param <T>
 */
public interface AxSubscriber<T> extends Subscriber<T> {

    /**
      * {@inheritDoc}
      */
    @Override
    default void onNext(T payload) {
    }

    /**
      * {@inheritDoc}
      */
    @Override
    default void onSubscribe(Subscription s) {
        s.request(Integer.MAX_VALUE);
    }

    /**
      * {@inheritDoc}
      */
    @Override
    default void onComplete() {
    }

    /**
      * {@inheritDoc}
      */
    @Override
    default void onError(Throwable t) {
    }

    /**
     * Same behavior as {@link #onSubscribe(Subscription)}, but with an extended version of {@link Subscription} called {@link AxSubscription}.
     *
     * @param subscription the element signaled
     */
    default void onSubscribe(AxSubscription subscription) {
    }

    /**
     * Data notification sent by a {@link Publisher} in response to requests to {@link Subscription#request(long)}.
     * The {@code payload} can comes from a {@link MultiLink}, a {@link Link} or a websocket/http connection.
     *
     * @param payload the element signaled
     */
    default void onNext(byte[] payload) {
    }

    /**
     * Data notification sent by a {@link Publisher} in response to requests to {@link Subscription#request(long)}.
     * The {@code payload} can comes from a {@link MultiLink}, a {@link Link} or a websocket/http connection.
     *
     * @param link The {@link Link} representing the underlying websocket or http connection
     * @param payload The {@link String} payload received
     * @param <U>
     */
    default <U extends Processor<? super String, ? super String>> void onNext(Link<U, String> link, String payload) {
    }

    /**
     * Data notification sent by the {@link Publisher} in response to requests to {@link Subscription#request(long)}.
     * The {@code payload} can comes from a {@link MultiLink}, a {@link Link} or a websocket/http connection.
     *
     * @param link
     * @param <U>
     * @param <V>
     */
    default <U extends Processor<? super String, ? super String>, V> void onNext(Link<U, V> link) {
    }

    /**
     * Data notification sent by a {@link Publisher} in response to requests to {@link Subscription#request(long)}.
     * The {@code payload} can comes from a {@link MultiLink}, a {@link Link} or a websocket/http connection.
     *
     * @param link The {@link Link} representing the underlying websocket or http connection
     * @param payload The {@link byte} payload received
     * @param <U>
     */
    default <U extends Processor<? super String, ? super String>> void onNext(Link<U, byte[]> link, byte[] payload) {
    }

}
