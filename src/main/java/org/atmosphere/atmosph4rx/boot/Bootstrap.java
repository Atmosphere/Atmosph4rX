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
package org.atmosphere.atmosph4rx.boot;

import org.atmosphere.atmosph4rx.core.AxRouter;
import org.atmosphere.atmosph4rx.core.AxRouter.AxConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Optional;

@Component
public class Bootstrap {
    private final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    @Inject
    private AxRouter axRouter;

    @PostConstruct
    public void init() {
        logger.info("Starting Atmosph4rX Framework");
    }

    Mono<Void> websocketSupport(WebSocketSession session) {

        Optional<AxConnection> foundMapping = axRouter.supply(session.getHandshakeInfo().getPrincipal(),
                session.getHandshakeInfo().getUri(),
                session.getHandshakeInfo().getHeaders(),
                session.getId());

        if (foundMapping.isPresent()) {
            AxConnection axConnection = foundMapping.get();
            session.receive()
                    .map(WebSocketMessage::getPayloadAsText)
                    .log()
                    .subscribe(axConnection.axSubscriber());

            return session.send(axConnection.outputFlux().map(session::textMessage));
        } else {
            logger.warn("No mapping for {}", session.getHandshakeInfo().getUri());
            return null;
        }
    }

    Mono<ServerResponse> longPollingSupport(ServerRequest serverRequest) {

        //return ok().contentType(APPLICATION_JSON).body(BodyInserters.fromPublisher(publisher, String.class));
        return null;
    }

    public Mono<ServerResponse> serverSentEventSupport(ServerRequest serverRequest) {

//        return ok().body(fromServerSentEvents(publisher));
        return null;
    }
}
