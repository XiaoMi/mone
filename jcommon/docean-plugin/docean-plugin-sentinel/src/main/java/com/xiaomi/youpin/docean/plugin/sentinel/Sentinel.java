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

import com.alibaba.csp.sentinel.EntryType;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * @author lyc@qq.com
 * @date 2020/6/26
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sentinel {

    String value() default "";

    EntryType entryType() default EntryType.OUT;

    int resourceType() default 0;

    String blockHandler() default "";

    Class<?>[] blockHandlerClass() default {};

    String fallback() default "";

    String defaultFallback() default "";

    Class<?>[] fallbackClass() default {};

    Class<? extends Throwable>[] exceptionsToTrace() default {Throwable.class};

    Class<? extends Throwable>[] exceptionsToIgnore() default {};
}
