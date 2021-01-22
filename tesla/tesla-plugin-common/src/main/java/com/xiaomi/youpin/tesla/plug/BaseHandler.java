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

package com.xiaomi.youpin.tesla.plug;

import com.xiaomi.youpin.tesla.plug.ioc.IocInit;
import com.youpin.xiaomi.tesla.plugin.handler.IHandler;
import org.apache.commons.lang3.StringUtils;
import org.nutz.ioc.loader.annotation.Inject;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @author goodjava@qq.com
 */
public abstract class BaseHandler<T> implements IHandler<T> {


    public BaseHandler() {
        Field[] fields = this.getClass().getDeclaredFields();
        Arrays.stream(fields).forEach(it -> {
            Inject inject = it.getAnnotation(Inject.class);
            if (null != inject) {
                String name = inject.value();
                Object t = null;
                if (StringUtils.isNotEmpty(name)) {
                    t = IocInit.ins().get(it.getType(), name);
                } else {
                    t = IocInit.ins().get(it.getType());
                }
                try {
                    it.setAccessible(true);
                    it.set(this, t);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
