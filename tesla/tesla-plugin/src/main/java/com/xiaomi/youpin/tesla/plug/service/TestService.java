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

package com.xiaomi.youpin.tesla.plug.service;

import com.xiaomi.youpin.tesla.plug.bo.ApiInfo;
import com.youpin.xiaomi.tesla.service.TeslaOpsService;
import org.apache.dubbo.config.annotation.Reference;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author goodjava@qq.com
 */
@IocBean(name = "testService")
public class TestService {

    /**
     * 注入dao层
     */
    @Inject("dao")
    private Dao testDao;

    /**
     * 注入dubbo接口
     */
    @Inject
    @Reference(interfaceClass = TeslaOpsService.class)
    private TeslaOpsService teslaOpsService;


    /**
     * 测试普通方法
     *
     * @param a
     * @param b
     * @return
     */
    public int sum(int a, int b) {
        return a + b;
    }

    /**
     * 测试调用dao层
     *
     * @return
     */
    public int getId() {
        return testDao.fetch(ApiInfo.class, 1L).getId();
    }


    /**
     * 测试调用dubbo接口
     *
     * @return
     */
    public String ping() {
        return teslaOpsService.ping().getData();
    }

}
