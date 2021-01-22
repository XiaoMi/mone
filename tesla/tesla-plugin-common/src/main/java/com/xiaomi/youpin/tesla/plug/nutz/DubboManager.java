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

import lombok.extern.slf4j.Slf4j;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.ioc.meta.IocObject;

import java.util.Map;

/**
 * @author goodjava@qq.com
 */
@Slf4j
@IocBean(create = "init",depose = "depose")
public class DubboManager {

    @Inject
    protected Ioc ioc;

    @Inject
    protected Map<String, IocObject> iobjs;


    public void init() {
        log.debug("dubbo obj count=" + iobjs.size());
        AnnotationBean ab = ioc.get(AnnotationBean.class);
        ab.load();
    }


    public void depose() {
        log.info("depose dubbo manager");
    }


}
