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

import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.Iocs;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.meta.IocObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
public class DubboIocLoader implements IocLoader {

    protected Map<String, IocObject> iobjs = new HashMap<>();

    public DubboIocLoader(String[] annotationPackages) {
        this.load(annotationPackages);
    }

    public void load(String[] annotationPackages) {
        IocObject dubbo_iobjs = Iocs.wrap(iobjs);
        dubbo_iobjs.setType(Object.class);
        iobjs.put("iobjs", dubbo_iobjs);

        IocObject pathsIobjs = Iocs.wrap(annotationPackages);
        pathsIobjs.setType(Object.class);
        iobjs.put("annotationPackages", pathsIobjs);
    }


    @Override
    public String[] getName() {
        int count = iobjs.size();
        return iobjs.keySet().toArray(new String[count]);
    }

    @Override
    public IocObject load(IocLoading loading, String name) throws ObjectLoadException {
        IocObject obj = iobjs.get(name);
        if (obj == null) {
            throw new ObjectLoadException("Object '" + name + "' without define!");
        }
        return obj;
    }

    @Override
    public boolean has(String name) {
        return iobjs.containsKey(name);
    }


}
