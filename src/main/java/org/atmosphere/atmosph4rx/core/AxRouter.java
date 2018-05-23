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
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.security.Principal;
import java.util.Optional;
import java.util.Set;

public interface AxRouter {

    Optional<AxConnection> supply(Mono<Principal> principal, URI uri, HttpHeaders headers, String id);

    Set<String> routes();

    class AxConnection {

        private final AxSubscriber<String> axSubscriber;
        private final Flux<String> flux;
        private final AxMetaData axMetaData;


        public AxConnection(AxMetaData axMetaData, AxSubscriber<String> axSubscriber, Flux<String> flux) {
            this.axSubscriber = axSubscriber;
            this.flux = flux;
            this.axMetaData = axMetaData;
        }

        public AxSubscriber<String> axSubscriber() {
            return axSubscriber;
        }

        public Flux<String> outputFlux() {
            return flux;
        }
    }


}
