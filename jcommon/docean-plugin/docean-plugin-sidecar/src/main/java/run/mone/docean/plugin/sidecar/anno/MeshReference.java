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

package run.mone.docean.plugin.sidecar.anno;

import java.lang.annotation.*;


/**
 * @author goodjava@qq.com
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface MeshReference {

    String name() default "";

    Class<?> interfaceClass();

    String group() default "";

    String version() default "";

    boolean check() default true;

    int timeout() default 1000;

    String app() default "";

    /**
     * 对面也是mesh服务(经过网格的)
     * @return
     */
    boolean mesh() default true;

    /**
     * 是否是远程调用
     * @return
     */
    boolean remote() default false;

}
