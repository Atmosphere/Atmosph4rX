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

import org.atmosphere.atmosph4rx.Link;
import org.atmosphere.atmosph4rx.MultiLink;
import org.reactivestreams.Processor;

public interface MultiLinkProcessor<IN extends String> extends MultiLink<Processor<? super String, ? super String>, String> {

    @Override
    Processor<String,String> toProcessor();

    default void subscribe(Link<Processor<? super String, ? super String>, IN> single){
        toProcessor().subscribe(single.toProcessor());
    }
}
