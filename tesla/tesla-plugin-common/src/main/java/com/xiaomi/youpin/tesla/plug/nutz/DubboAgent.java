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

import org.apache.dubbo.config.spring.ReferenceBean;
import org.apache.dubbo.config.spring.ServiceBean;
import org.nutz.ioc.meta.IocEventSet;
import org.nutz.ioc.meta.IocField;
import org.nutz.ioc.meta.IocObject;
import org.nutz.ioc.meta.IocValue;


/**
 * @author goodjava@qq.com
 */
public class DubboAgent {

    public static void checkIocObject(String beanName, IocObject iobj) {
        IocEventSet events = new IocEventSet();
        if (iobj.getType() == ServiceBean.class) {
            events.setDepose("unexport");
        }
        if (iobj.getType() == ReferenceBean.class
                || iobj.getType() == DubboManager.class) {
        }
        iobj.setEvents(events);
    }

    public static IocValue _ref(String beanName) {
        return new IocValue(IocValue.TYPE_REFER, beanName);
    }

    public static IocField _field(String fieldName, IocValue val) {
        IocField field = new IocField();
        field.setName(fieldName);
        field.setValue(val);
        return field;
    }

}
