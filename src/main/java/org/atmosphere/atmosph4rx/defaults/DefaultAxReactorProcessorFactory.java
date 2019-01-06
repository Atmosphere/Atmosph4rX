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
package org.atmosphere.atmosph4rx.defaults;

import org.atmosphere.atmosph4rx.core.AxReactorProcessorFactory;
import org.reactivestreams.Processor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.TopicProcessor;
import reactor.core.publisher.UnicastProcessor;

@Component
public class DefaultAxReactorProcessorFactory implements AxReactorProcessorFactory {

    @Value("${atmosph4rX.output.flux.buffer:50}")
    private int buffer;

    @Override
    public FluxProcessor<String, String> socketProcessor() {
        return UnicastProcessor.create();
    }

    @Override
    public TopicProcessor<String> socketsProcessor() {
        return TopicProcessor.create();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Flux<String> toFlux(Processor<String, String> processor) {
        if (processor instanceof UnicastProcessor) {
            return UnicastProcessor.class.<String>cast(UnicastProcessor.class.cast(processor))
                    .replay(buffer)
                    .autoConnect();
        } else {
            Flux<String> f = Flux.create(messageEmitter -> {/* TODO */ });
            f.replay().connect();
            return f;
        }

    }
}
