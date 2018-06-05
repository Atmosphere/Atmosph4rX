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
package org.atmosphere.atmosph4rx.defaults;

import org.atmosphere.atmosph4rx.AxSubscriber;
import org.atmosphere.atmosph4rx.annotation.ReactTo;
import org.atmosphere.atmosph4rx.core.*;
import org.atmosphere.atmosph4rx.util.EndpointMapper;
import org.reactivestreams.Processor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class DefaultAxRouter implements AxRouter {

    @Inject
    private AxThrowablesHandler throwablesHandler;

    private List<Processor<String, String>> interceptorProcessor;

    @Inject
    private AnnotationsScanner annotationsScanner;

    @Inject
    private AxReactorProcessorFactory processorsFactory;

    private Map<String, AxSubscriber<String>> subscribers;

    @Inject
    private EndpointMapper<AxSubscriber<String>> endpointMapper;

    @Inject
    public void setAxSubscribers(List<AxSubscriber<String>> l) {
        subscribers = l.stream()
                .filter(m -> m.getClass().isAnnotationPresent(ReactTo.class))
                .collect(Collectors.toMap(e -> e.getClass().getAnnotation(ReactTo.class).value(), e -> e));

        subscribers.putAll(annotationsScanner.routesMap()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new AxAnnotationDrivenSubscriber(e.getValue()))));
    }

//    @Inject
//    public void setInterceptors(List<Processor<String, String>> interceptorProcessor) {
//        this.interceptorProcessor = interceptorProcessor;
//    }

    @Override
    public Optional<AxConnection> supply(Mono<Principal> principal, URI uri, HttpHeaders headers, String id) {

        Optional<AxSubscriber<String>> maybe = endpointMapper.map(uri.getPath(), subscribers);
        if (maybe.isPresent()) {
            AxMetaData metaData = new AxMetaData() {

                private final Map<String, Object> metaData = new ConcurrentHashMap<>();

                @Override
                public Map<String, Object> metaData() {
                    return metaData;
                }

                @Override
                public STAGE stage() {
                    return null;
                }

                @Override
                public HttpHeaders headers() {
                    return headers;
                }

                @Override
                public URI uri() {
                    return uri;
                }

                @Override
                public Mono<Principal> principal() {
                    return principal;
                }
            };

            return map(maybe.get(), metaData, id);
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public Optional<AxConnection> map(AxSubscriber<String> axSubscriber, AxMetaData axMetaData, String id) {
        FluxProcessor<String, String> processor = processorsFactory.socketProcessor();
        AxProcessor<FluxProcessor<String, String>> axProcessor = new AxProcessor<>(processor, id);

        if (processor instanceof Flux) {
            // TODO
//            unicastProcessor.doOnSubscribe(s -> throwablesHandler.handle(interceptorsInvoker.invoke(axMetaData, webSocketOutput, STAGE.OPEN)))
//                    .doOnNext(s -> throwablesHandler.handle(interceptorsInvoker.invoke(axMetaData, webSocketOutput, STAGE.MESSAGE)))
//                    .doOnCancel(() -> throwablesHandler.handle(interceptorsInvoker.invoke(axMetaData, webSocketOutput, STAGE.ERROR)))
//                    .doOnComplete(() -> throwablesHandler.handle(interceptorsInvoker.invoke(axMetaData, webSocketOutput, STAGE.CLOSE)));
//            axProcessor.out(new AxProcessorInterceptor(axMetaData, axProcessor));
        }

        Flux<String> single = processorsFactory.toFlux(axProcessor.out());

        axSubscriber = new AxSubscriberInvoker<>(axSubscriber, axMetaData, axProcessor);

        return Optional.of(new AxConnection(axMetaData, axSubscriber, single));
    }


    @Override
    public Set<String> routes() {
        return annotationsScanner.routes();
    }

    public final static class AxProcessor<T extends FluxProcessor> {

        private T processor;
        private final String id;


        public AxProcessor(T processor, String id) {
            this.processor = processor;
            this.id = id;
        }

        public T out() {
            return processor;
        }

        public AxProcessor<T> out(T processor) {
            this.processor = processor;
            return this;
        }

        public String id() {
            return id;
        }
    }
}
