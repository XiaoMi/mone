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

package com.xiaomi.youpin.tesla.plug.test;

import com.xiaomi.youpin.tesla.plug.nutz.DubboAgent;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.meta.IocField;
import org.nutz.ioc.meta.IocObject;
import org.nutz.ioc.meta.IocValue;

import java.util.HashMap;
import java.util.Map;

public class MyIocLoader implements IocLoader {


    private Map<String,IocObject> m = new HashMap<>();


    public MyIocLoader() {
        IocObject io = new IocObject();
        io.setSingleton(true);
        io.setType(Bean.class);
        io.addField(DubboAgent._field("ioc", DubboAgent._ref("$ioc")));

        IocField field = new IocField();
        field.setName("id");
        field.setValue(new IocValue(IocValue.TYPE_NORMAL,9999));

        io.addField(field);
        io.addArg(new IocValue(IocValue.TYPE_NORMAL,"lwj"));
        m.put("bean",io);


        IocObject io2 = new IocObject();
        io2.setSingleton(true);
        io2.setFactory("com.xiaomi.youpin.tesla.plug.test.BeanFactory#create");
        m.put("bean2",io2);


        IocObject io3 = new IocObject();
        io3.setSingleton(true);
        io3.setType(BaBa.class);
        m.put("baba",io3);


        IocObject io4 = new IocObject();
        io4.setSingleton(true);
        io4.setFactory("$baba#get");
        m.put("bean4",io4);


        IocObject io5 = new IocObject();
        io5.setSingleton(false);
        io5.setType(Bean2.class);
        m.put("bean5",io5);

        IocObject io6 = new IocObject();
        io6.setSingleton(false);
        io6.setType(Bean.class);
        IocField field5 = new IocField();
        field5.setName("bean2");
        field5.setValue(new IocValue(IocValue.TYPE_REFER,"bean5"));
        io6.addField(field5);
        m.put("bean6",io6);
    }

    @Override
    public String[] getName() {
        return new String[0];
    }

    @Override
    public IocObject load(IocLoading loading, String name) throws ObjectLoadException {
        return m.get(name);
    }

    @Override
    public boolean has(String name) {
        return true;
    }
}
