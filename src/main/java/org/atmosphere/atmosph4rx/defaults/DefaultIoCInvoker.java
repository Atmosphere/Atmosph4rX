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

import org.atmosphere.atmosph4rx.core.IoCInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DefaultIoCInvoker implements IoCInvoker {

    private final Logger logger = LoggerFactory.getLogger(DefaultIoCInvoker.class);

    @Override
    public Object invoke(Method m, Object instance, List<Object> objects) throws Throwable {
        List<Class<?>> classes = Stream.of(m.getParameterTypes()).collect(Collectors.toList());

        MethodType methodType = MethodType.methodType(void.class, classes);
        MethodHandle methodHandler = MethodHandles.lookup().findVirtual(instance.getClass(), m.getName(), methodType);
        return methodHandler.bindTo(instance).invokeWithArguments(objects.toArray());
    }

}
