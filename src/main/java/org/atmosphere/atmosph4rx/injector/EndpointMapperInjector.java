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
package org.atmosphere.atmosph4rx.injector;

import org.atmosphere.atmosph4rx.util.DefaultEndpointMapper;
import org.atmosphere.atmosph4rx.util.EndpointMapper;
import org.springframework.context.annotation.Bean;

import java.util.Map;
import java.util.function.Supplier;

public class EndpointMapperInjector {

    @Bean
    public EndpointMapper<Map<String, Supplier<?>>> endpointMapper(){
        return new DefaultEndpointMapper<>();
    }
}
