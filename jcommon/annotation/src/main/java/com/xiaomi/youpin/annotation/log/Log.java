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

package com.xiaomi.youpin.annotation.log;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
public @interface Log {


    String name() default "";

    /**
     * 记录错误信息
     *
     * @return
     */
    boolean recordError() default false;

    /**
     * 记录错误到日志
     *
     * @return
     */
    boolean recordErrorLog() default false;

    /**
     * 打印结果
     *
     * @return
     */
    boolean printResult() default true;

    boolean printParam() default true;

    /**
     * 是否启用限流
     *
     * @return
     */
    boolean useSemphore() default false;

    /**
     * 是否使用falcon
     *
     * @return
     */
    boolean usePercount() default false;

    /**
     * 是否使用cat
     *
     * @return
     */
    boolean useCat() default true;

}
