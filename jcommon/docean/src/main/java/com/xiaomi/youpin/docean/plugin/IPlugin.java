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

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author goodjava@qq.com
 * @date 2020/6/26
 */
public interface IPlugin {

    default void init() {

    }

    default void init(Set<? extends Class<?>> classSet, Ioc ioc) {

    }

    /**
     * 销毁操作
     * @param ioc
     */
    default void destory(Ioc ioc) {

    }

    /**
     * 过滤的注解
     *
     * @return
     */
    default List<Class<? extends Annotation>> filterAnnotations() {
        return new ArrayList<>();
    }

    /**
     * 初始化需要被接管的bean
     *
     * @param bean
     * @return
     */
    default Bean initBean(Ioc ioc, Bean bean) {
        return bean;
    }


    default Optional<String> ioc(Ioc ioc, Annotation[] annotations) {
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
     * plugin 启动操作(可以理解为依赖注入都完成后,想要完成的操作都可以放到这里)
     * @param ioc
     * @return
     */
    default boolean start(Ioc ioc) {
        return true;
    }
}
