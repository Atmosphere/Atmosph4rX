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
package org.atmosphere.atmosph4rx;

import org.atmosphere.atmosph4rx.core.AxMetaData;
import org.atmosphere.atmosph4rx.defaults.DefaultAxRouter;
import org.reactivestreams.Subscription;
import reactor.core.publisher.FluxProcessor;

public class AxSubscription implements Subscription {

    private final Subscription subscription;
    private final AxMetaData metaData;
    private final DefaultAxRouter.AxProcessor<FluxProcessor<String, String>> outputProcessor;

    public AxSubscription(Subscription subscription, AxMetaData metaData, DefaultAxRouter.AxProcessor<FluxProcessor<String, String>> outputProcessor) {
        this.subscription = subscription;
        this.metaData = metaData;
        this.outputProcessor = outputProcessor;
    }

    public AxMetaData metaData() {
        return metaData;
    }

    public AxSocket<FluxProcessor<? super String, ? super String>, String> socket() {
        return new AxSocket<FluxProcessor<? super String, ? super String>, String>() {
            @Override
            public AxSockets<FluxProcessor<? super String, ? super String>, String> publish(String message) {
                outputProcessor.out().onNext(message);
                return this;
            }

            @Override
            public FluxProcessor<String, String> toProcessor() {
                return outputProcessor.out();
            }

            @Override
            public String id() {
                return outputProcessor.id();
            }
        };
    }

    @Override
    public void request(long n) {
        subscription.request(n);
    }

    @Override
    public void cancel() {
        subscription.cancel();
    }
}
