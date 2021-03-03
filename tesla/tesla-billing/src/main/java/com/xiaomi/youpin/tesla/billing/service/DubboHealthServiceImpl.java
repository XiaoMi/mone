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

package com.xiaomi.youpin.tesla.billing.service;

import com.xiaomi.data.push.common.Result;
import com.xiaomi.data.push.common.Version;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Service;

/**
 * @description: TODO
 * @author zhenghao
 *
 */
@Service(interfaceClass = DubboHealthService.class)
public class DubboHealthServiceImpl implements DubboHealthService {
    @Override
    public Result<Version> health() {
        Result<Version> result = new Result<Version>(0,"success",new Version("1","2"));
        return result;
    }
}
