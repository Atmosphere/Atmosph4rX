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
package org.atmosphere.atmosph4rx.util;

import org.atmosphere.atmosph4rx.util.uri.UriTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class DefaultEndpointMapper<U> implements EndpointMapper<U> {

    private static final Logger logger = LoggerFactory.getLogger(DefaultEndpointMapper.class);

    public DefaultEndpointMapper() {
    }

    protected U match(String path, Map<String, U> handlers) {
        U handler = handlers.get(path);

        if (handler == null) {
            final Map<String, String> m = new HashMap<>();
            for (Map.Entry<String, U> e : handlers.entrySet()) {
                UriTemplate t = null;
                try {
                    t = new UriTemplate(e.getKey());
                    logger.trace("Trying to map {} to {}", t, path);
                    if (t.match(path, m)) {
                        handler = e.getValue();
                        logger.trace("Mapped {} to {}", t, e.getValue());
                        break;
                    }
                } finally {
                    if (t != null) t.destroy();
                }
            }
        }
        return handler;
    }

    @Override
    public Optional<U> map(String path, Map<String, U> targets) {

        if (path == null || path.isEmpty()) {
            path = "/";
        }

        U target = match(path, targets);
        if (target == null) {
            // (2) First, try exact match
            target = match(path + (path.endsWith("/") ? "all" : "/all"), targets);

            if (target == null) {
                // (3) Wildcard
                target = match(path + "*", targets);

                // (4) try without a path
                if (target == null) {
                    String p = path.lastIndexOf("/") <= 0 ? "/" : path.substring(0, path.lastIndexOf("/"));
                    while (p.length() > 0 && p.contains("/")) {
                        target = match(p, targets);

                        // (3.1) Try path wildcard
                        if (target != null) {
                            break;
                        }
                        p = p.substring(0, p.lastIndexOf("/"));
                    }
                }
            }
        }
        return target == null ? Optional.empty() : Optional.of(target);
    }
}
