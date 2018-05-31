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
package org.atmosphere.atmosph4rx.injector;

import org.atmosphere.atmosph4rx.annotation.Topic;
import org.atmosphere.atmosph4rx.core.AxReactorProcessorFactory;
import org.atmosphere.atmosph4rx.core.SocketsGroupProcessor;
import org.reactivestreams.Processor;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class AxProcessorInjector {

    @Inject
    private AxReactorProcessorFactory processorsFactory;

    private final Map<String, SocketsGroupProcessor<?>> aXProcessors = new LinkedHashMap<>();

    @Bean
    public SocketsGroupProcessor<String> construct(DependencyDescriptor ip) {

//        if (!ip.isPresent()) return null;

        Topic topic = AnnotationUtils.getAnnotation(ip.getAnnotatedElement(), Topic.class);

        List<Class<?>> resolvableTypes = Stream.of(ip.getResolvableType().getGenerics())
                .map(ResolvableType::getRawClass)
                .collect(Collectors.toList());

        if (resolvableTypes.isEmpty()) {
            throw new IllegalStateException();
        }
        return toBroadcaster(topic.value(), resolvableTypes.get(0));
    }

    @SuppressWarnings("unchecked")
    private <IN> SocketsGroupProcessor<String> toBroadcaster(String topic, Class<IN> in) {

        SocketsGroupProcessor<String> cIn = (SocketsGroupProcessor<String>) aXProcessors.get(topic);
        if (cIn == null) {
            Processor<String, String> tp = processorsFactory.createMultiLinkProcessor();
            cIn = new SocketsGroupProcessor<String>() {

                @Override
                public Processor<String, String> toProcessor() {
                    return tp;
                }

                @Override
                public String topic() {
                    return topic;
                }

                @Override
                public SocketsGroupProcessor<String> publish(String message) {
                    tp.onNext(message);
                    return this;
                }
            };
            aXProcessors.put(topic, cIn);
        }
        return cIn;
    }
}
