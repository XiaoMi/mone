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

package com.xiaomi.youpin.docean.ioc;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Bean;
import com.xiaomi.youpin.docean.common.Pair;
import com.xiaomi.youpin.docean.common.ReflectUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author goodjava@qq.com
 * @date 2020/6/24
 */
public class BeanAnnoProcessor {

    public static void process(Class<?> configuration, Ioc ioc) {
            Arrays.stream(configuration.getMethods()).filter(m -> Optional.ofNullable(m.getAnnotation(Bean.class)).isPresent())
                    .map(m -> Pair.of(m.getReturnType().getName(),ReflectUtils.invokeMethod(ReflectUtils.getInstance(configuration), m, new Object[]{}))).forEach(pair->{
                        ioc.putBean(pair.getKey(),pair.getValue());
            });
    }


}
