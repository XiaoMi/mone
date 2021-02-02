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

package com.xiaomi.youpin.tesla.plug.nutz;

import org.nutz.dao.impl.NutDao;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.meta.IocObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * 用来定义resouce 的bean 比如 redis mysql mongodb
 */
public class ResourceLoader implements IocLoader {

    private Map<String, IocObject> m = new HashMap<>();

    public ResourceLoader() {
        IocObject io = new IocObject();
        io.setSingleton(true);
        io.setType(NutDao.class);
        m.put("dao", io);
    }

    @Override
    public String[] getName() {
        return m.keySet().stream().toArray(String[]::new);
    }

    @Override
    public IocObject load(IocLoading loading, String name) {
        return m.get(name);
    }

    @Override
    public boolean has(String name) {
        return m.containsKey(name);
    }
}
