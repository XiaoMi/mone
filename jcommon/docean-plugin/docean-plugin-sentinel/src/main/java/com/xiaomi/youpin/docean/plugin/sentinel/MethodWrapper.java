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

package com.xiaomi.youpin.docean.plugin.sentinel;

import java.lang.reflect.Method;

public class MethodWrapper {
    private final Method method;
    private final boolean present;

    private MethodWrapper(Method method, boolean present) {
        this.method = method;
        this.present = present;
    }

    static MethodWrapper wrap(Method method) {
        return method == null ? none() : new MethodWrapper(method, true);
    }

    static MethodWrapper none() {
        return new MethodWrapper((Method)null, false);
    }

    Method getMethod() {
        return this.method;
    }

    boolean isPresent() {
        return this.present;
    }
}
