/*
 * Copyright 2018 Async-IO.org
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
package org.atmosphere.atmosph4rx.core;

import org.atmosphere.atmosph4rx.AxSubscriber;
import org.atmosphere.atmosph4rx.annotation.Close;
import org.atmosphere.atmosph4rx.annotation.Error;
import org.atmosphere.atmosph4rx.annotation.Interceptor;
import org.atmosphere.atmosph4rx.annotation.Message;
import org.atmosphere.atmosph4rx.annotation.Open;
import org.atmosphere.atmosph4rx.annotation.ReactTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public final class AnnotationsScanner {

    private final Logger logger = LoggerFactory.getLogger(AnnotationsScanner.class);

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private IoCInvoker invoker;

    private Map<String, Map<String, Context<?>>> routeTo;

    @PostConstruct
    private void findAnnotations() {
        // (1) Classes annotated with @ReactTo
        List<Object> reactoToAnnotatedClasses = annotatedClasses();

        /*
           @Open => onSubscribe
           @Close => onComplete
           @On => onNext
           @Error => onError
         */

        routeTo = reactoToAnnotatedClasses
                .stream()
                .collect(Collectors.toMap(e -> e.getClass().getAnnotation(ReactTo.class).value(),
                        e -> Stream.of(e.getClass().getDeclaredMethods())
                                .collect(Collectors.toMap(m -> Arrays.stream(m.getAnnotations())
                                                .filter(AnnotationsScanner::isMapped)
                                                .map(this::nameOf)
                                                .reduce((r1, r2) -> r1)
                                                .orElse(UUID.randomUUID().toString())
                                        , m -> new Context<Object>() {
                                            @Override
                                            public Object apply(List<Object> objects) {
                                                try {
                                                    return invoker.invoke(m, e, objects);
                                                } catch (Throwable ex) {
                                                    logger.error("", ex);
                                                    throw new IllegalStateException(ex);
                                                }
                                             }

                                            @Override
                                            public Object instance() {
                                                return e;
                                            }

                                            @Override
                                            public Method method() {
                                                return m;
                                            }
                                        }))));

//        List<Object> clazzes = annotatedMethods();
//
//        List<Processor<String, String>> p =  new LinkedList<>();
//
//        for()
//                methods.stream()
//                .flatMap(m -> Stream.of(m.getClass().getDeclaredMethods()))
//                .filter(f -> f.isAnnotationPresent(Interceptor.class))
//                .map(m -> {
//                    m.invoke()
//
//
//
//                })
//                .collect(Collectors.toList());
    }

    public interface Context<A> extends Function<List<Object>, A> {

        Object instance();

        Method method();
    }

    // ???
    private String nameOf(Annotation annotation) {
        if (annotation instanceof Open) {
            return Open.class.getName();
        } else if (annotation instanceof Close) {
            return Close.class.getName();
        } else if (annotation instanceof Message) {
            return Message.class.getName();
        } else if (annotation instanceof Error) {
            return Error.class.getName();
        } else {
            throw new IllegalStateException();
        }
    }

    static boolean isMapped(Annotation annotation) {
        return (annotation instanceof Open
                || annotation instanceof Close
                || annotation instanceof Message
                || annotation instanceof Error);

    }

    protected List<Object> annotatedClasses() {
        // TODO:
        // getBeansWithAnnotation
        return Stream.of(applicationContext.getBeanDefinitionNames())
                .map(beanName -> {
                    try {
                        Class<?> clazz = applicationContext.getType(beanName);

                        // null CAN happens
                        if (clazz != null) {
                            try {
                                logger.trace("Scanning {}", clazz.getName());
                            } catch (Exception ex) {
                                //
                                logger.trace("", clazz.getName(), ex);
                            }

                            if (!AxSubscriber.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(ReactTo.class)) {
                                logger.debug("Found {}", beanName);
                                return applicationContext.getBean(beanName);
                            }
                        }
                        return null;
                    } catch (BeanCreationException b) {
                        logger.trace("", b);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<Object> annotatedMethods() {
        return Stream.of(applicationContext.getBeanDefinitionNames())
                .filter(f -> !f.startsWith("com.yulplay.desjardins.id.messaging"))
                .map(beanName -> {
                    try {
                        Class<?> clazz = applicationContext.getType(beanName);

                        if (clazz != null) {
                            try {
                                logger.trace("Scanning {}", clazz.getName());
                            } catch (Exception ex) {
                                //
                            }

                            for (Method m : clazz.getDeclaredMethods()) {
                                if (m.isAnnotationPresent(Interceptor.class)) {
                                    logger.debug("Found {}", beanName);
                                    return applicationContext.getBean(beanName);
                                }
                            }
                        }
                        return null;
                    } catch (BeanCreationException b) {
                        logger.trace("", b);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    public Set<String> routes() {
        return routeTo.keySet();
    }

    public Map<String, Map<String, Context<?>>> routesMap(){
        return routeTo;
    }
}