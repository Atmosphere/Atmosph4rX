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
package org.atmosphere.atmosph4rx.core;

import org.atmosphere.atmosph4rx.AxSubscriber;
import org.atmosphere.atmosph4rx.AxSubscription;
import org.atmosphere.atmosph4rx.AxSocket;
import org.atmosphere.atmosph4rx.annotation.Close;
import org.atmosphere.atmosph4rx.annotation.Error;
import org.atmosphere.atmosph4rx.annotation.Message;
import org.atmosphere.atmosph4rx.annotation.Open;
import org.atmosphere.atmosph4rx.core.AnnotationsScanner.Context;
import org.reactivestreams.Processor;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AxAnnotationDrivenSubscriber implements AxSubscriber<String> {
    private final Logger logger = LoggerFactory.getLogger(AxAnnotationDrivenSubscriber.class);

    private final Map<String, Context<?>> contextMap;

    private final ReentrantLock lock = new ReentrantLock();

    private final static List<Class<?>> supportedTypes = new LinkedList<Class<?>>() {
        {
            add(AxSocket.class);
            add(String.class);
            add(byte[].class);
        }
    };
    
    //TODO
    private Subscription subscription;

    public AxAnnotationDrivenSubscriber(Map<String, Context<?>> contextMap) {

        this.contextMap = contextMap;
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;

        Context<?> context = contextMap.get(Open.class.getName());
        if (context != null) {
            invoke(Open.class.getSimpleName(), context, null, null);
        }
    }

    @Override
    public void onNext(String payload) {
        Context<?> context = contextMap.get(Message.class.getName());
        if (context != null) {
            invoke(Message.class.getSimpleName(), context, payload, null);
        }
    }

    @Override
    public void onError(Throwable t) {
        Context<?> context = contextMap.get(Error.class.getName());
        if (context != null) {
            invoke(Error.class.getSimpleName(), context, null, null);
        }
    }

    @Override
    public void onComplete() {
        Context<?> context = contextMap.get(Close.class.getName());
        if (context != null) {
            invoke(Close.class.getSimpleName(), context, null, null);
        }
    }

    public void onSubscribe(AxSubscription s) {
        this.subscription = s;

        Context<?> context = contextMap.get(Open.class.getName());
        if (context != null) {
            invoke(Open.class.getSimpleName(), context, null, s.socket());
        }
    }

    public void onNext(byte[] payload) {
        Context<?> context = contextMap.get(Message.class.getName());
        if (context != null) {
            invoke(Message.class.getSimpleName(), context, payload, null);
        }
    }

    public <U extends Processor<? super String, ? super String>> void onNext(AxSocket<U, String> single, String payload) {
        Context<?> context = contextMap.get(Message.class.getName());
        if (context != null) {
            invoke(Message.class.getSimpleName(), context, payload, single);
        }
    }

    public <U extends Processor<? super String, ? super String>, T> void onNext(AxSocket<U, T> single) {
    }

    public <U extends Processor<? super String, ? super String>> void onNext(AxSocket<U, byte[]> single, byte[] payload) {
        // TODO
    }

    private <T, U, V  extends Processor<? super String, ? super String>> T invoke(String simpleName, Context<T> m, U payload, AxSocket<V, String> single) {
        List<Object> objects = methodObjects(simpleName, m.method().getParameterTypes(), payload, single);

        if (objects.isEmpty()) {
            return null;
        }

        // TODO: TESTME
//        lock.lock();
        T t = null;
        try {
            t = m.apply(objects);
        } catch (Exception e) {
            logger.error("{}", simpleName, e);
        } finally {
//            lock.unlock();
        }
        return t;
    }

    private <T,V extends Processor<? super String, ? super String>> List<Object> methodObjects(String simpleName, Class<?>[] parameterTypes, T payload, AxSocket<V, String> single) {
        List<Object> l = Stream.of(parameterTypes)
                .filter(supportedTypes::contains)
                .map(m -> match(m, payload, single))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (l.size() != (parameterTypes.length + (single == null ? 0 : 1))) {
            logger.debug("No mapping found for {}", simpleName);
            return Collections.emptyList();
        }
        return l;
    }

    private <T,V extends Processor<? super String, ? super String>> Object match(Class<?> m, T payload, AxSocket<V, String> single) {

        if (single != null && AxSocket.class.equals(m)) {
            return single;
        } else if (payload != null && String.class.equals(m)) {
            return payload;
        } else if (payload != null && byte[].class.equals(m)) {
            return payload;
        }
        return null;
    }


}