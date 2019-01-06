/**
 * Copyright 2018-2019 Async-IO.org
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
package org.atmosphere.atmosph4rx.boot;

import org.atmosphere.atmosph4rx.AxSubscriber;
import org.atmosphere.atmosph4rx.annotation.ReactTo;
import org.atmosphere.atmosph4rx.core.AxRouter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@ComponentScan(basePackages = {"org.atmosphere"})
public class Atmosph4rXConfig {

    @Inject
    private AxRouter axSubscriberSupplier;

    List<String> mapping;

    @Inject
    public void setAxSubscribers(List<AxSubscriber<String>> l) {
        mapping = l.stream()
                .filter(m -> m.getClass().isAnnotationPresent(ReactTo.class))
                .map(m -> m.getClass().getAnnotation(ReactTo.class).value())
                .collect(Collectors.toList());
    }

    @Inject
    private WebSocketHandler webSocketHandler;

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    public HandlerMapping webSocketMapping() {
        final Map<String, WebSocketHandler> map = new HashMap<>();

        axSubscriberSupplier.routes().forEach(f -> map.put(f, webSocketHandler));

        mapping.forEach(f -> map.put(f, webSocketHandler));

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(10);
        mapping.setUrlMap(map);
        return mapping;
    }
    
    //    @Bean
    //    public /*final*/ RouterFunction<ServerResponse> route(Bootstrap boot) {
    ////        return RouterFunctions
    ////                .route(POST(httpPath()).and(accept(APPLICATION_JSON).and(contentType(APPLICATION_JSON))), boot::longPollingSupport)
    ////                .andRoute(POST(ssePath()).and(accept(TEXT_EVENT_STREAM)), boot::serverSentEventSupport);
    //        return null;
    //    }


}
