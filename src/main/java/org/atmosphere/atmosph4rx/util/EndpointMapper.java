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
package org.atmosphere.atmosph4rx.util;

import java.util.Map;
import java.util.Optional;


public interface EndpointMapper<U> {

    /**
     * Mape the request to its associated endpoint.
     *
     * @param path     a URI
     * @param handlers a map used for mapping the request to.
     * @return U the result, or null if not mapped
     */
    Optional<U> map(String path, Map<String, U> handlers);
}
