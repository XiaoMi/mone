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

package com.xiaomi.youpin.docean.common;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author shanwb
 * @date 2023-04-04
 */
public class InterfaceLoader<T> {

    public static <T> List<Class<? extends T>> loadAll(Class<T> interfaceClass, ClassLoader classLoader) {
        List<Class<? extends T>> implementationClasses = new ArrayList<>();
        ServiceLoader<T> serviceLoader = ServiceLoader.load(interfaceClass, classLoader);
        for (T implementation : serviceLoader) {
            implementationClasses.add((Class<? extends T>) implementation.getClass());
        }
        return implementationClasses;
    }
}

