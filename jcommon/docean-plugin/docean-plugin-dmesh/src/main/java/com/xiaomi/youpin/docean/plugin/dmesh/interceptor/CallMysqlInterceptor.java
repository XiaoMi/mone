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

package com.xiaomi.youpin.docean.plugin.dmesh.interceptor;

import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.bo.Bean;
import com.xiaomi.youpin.docean.plugin.config.Config;
import com.xiaomi.youpin.docean.plugin.dmesh.anno.MeshMsService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 1/9/21
 */
@Slf4j
public class CallMysqlInterceptor extends AbstractInterceptor {

    private Ioc ioc;


    public CallMysqlInterceptor(Ioc ioc, Config config, MeshMsService reference) {
        super(ioc, config, reference);
        this.ioc = ioc;
    }

    @Override
    public void intercept0(UdsCommand req) {

    }


    @Override
    public void intercept1(UdsCommand req, Object o) {
        Bean bean = ioc.getBeanInfo(o.toString());
        if (null != bean) {
            req.putAtt("lookup", bean.getLookup());
        }
    }
}
