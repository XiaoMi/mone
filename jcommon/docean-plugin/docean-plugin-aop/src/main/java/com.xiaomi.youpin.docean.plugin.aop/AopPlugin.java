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

package com.xiaomi.youpin.docean.plugin.aop;

import com.google.common.collect.Lists;
import com.xiaomi.youpin.docean.Aop;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.aop.EnhanceInterceptor;
import com.xiaomi.youpin.docean.common.ReflectUtils;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.aop.anno.AopConfig;

import java.util.Set;

/**
 * @author goodjava@qq.com
 */
@DOceanPlugin
public class AopPlugin implements IPlugin {

    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
//        classSet.stream().filter(it -> Ioc.filterClass(ioc, it, Lists.newArrayList(AopConfig.class))).forEach(it -> {
//            AopConfig ac = it.getAnnotation(AopConfig.class);
//            Aop.ins().getInterceptorMap().put(ac.clazz(), (EnhanceInterceptor) ReflectUtils.getInstance(it));
//        });
    }

}
