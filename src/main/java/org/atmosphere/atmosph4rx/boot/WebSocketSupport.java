/*
 * Copyright 2018-2020 Async-IO.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.atmosphere.atmosph4rx.boot;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import javax.inject.Inject;

@Component
public class WebSocketSupport implements WebSocketHandler {

    private final Bootstrap boot;

    @Inject
    public WebSocketSupport(Bootstrap boot) {
        this.boot = boot;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return boot.websocketSupport(session);
    }
}
