/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.docean.plugin;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.bo.Bean;
import com.xiaomi.youpin.docean.common.Cons;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author goodjava@qq.com
 * @date 2020/6/26
 */
public interface IPlugin {

    default void init() {
    }

    default String getInitMethodName(Object obj, Class clazz) {
        return Cons.INIT;
    }

    default String getDestoryMethodName(Object obj, Class clazz) {
        return Cons.DESTORY;
    }

    default void init(Set<? extends Class<?>> classSet, Ioc ioc) {

    }

    /**
     * Destroy operation
     *
     * @param ioc
     */
    default void destory(Ioc ioc) {

    }

    /**
     * rate limited or exceeded quota
     *
     * @return
     */
    default List<Class<? extends Annotation>> filterAnnotations() {
        return new ArrayList<>();
    }

    default List<Class<? extends Annotation>> filterResourceAnnotations() {
        return new ArrayList<>();
    }

    /**
     * Initialization requires the takeover of the bean.
     *
     * @param bean
     * @return
     */
    default Bean initBean(Ioc ioc, Bean bean) {
        return bean;
    }


    default Optional<String> ioc(Ioc ioc, Class type, Annotation[] annotations) {
        return Optional.empty();
    }


    default Optional<Annotation> getAnno(Annotation[] annotations, Class<? extends Annotation> clazz) {
        Optional<Annotation> optional = Arrays.stream(annotations).filter(it -> it.annotationType().equals(clazz)).findAny();
        return optional;

    }


    default String version() {
        return "0.0.1";
    }

    default boolean disable(Ioc ioc) {
        return false;
    }


    default boolean after(Ioc ioc) {
        return true;
    }

    /**
     * plugin Start operation (after all dependencies are injected, any desired operations can be placed here)
     *
     * @param ioc
     * @return
     */
    default boolean start(Ioc ioc) {
        return true;
    }

    default void putBean(String name, Bean bean) {

    }
}
