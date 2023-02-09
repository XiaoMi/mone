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

import com.youpin.xiaomi.tesla.service.DubboService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author goodjava@qq.com
 * 测试dubbo接口暴露
 */
@Slf4j
@IocBean
@Service(interfaceClass = DubboService.class)
public class DubboServiceImpl implements DubboService {

    @Override
    public String test() {
        log.info("test");
        return "test";
    }
}
