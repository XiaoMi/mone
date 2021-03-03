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

package com.youpin.xiaomi.tesla.service;

import com.xiaomi.youpin.infra.rpc.Result;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.youpin.xiaomi.tesla.bo.GatewayInfo;
import com.youpin.xiaomi.tesla.bo.ModifyType;
import com.youpin.xiaomi.tesla.bo.PlugInfo;
import com.xiaomi.data.push.common.Health;

/**
 * @author goodjava@qq.com
 */
public interface TeslaGatewayService {

    Result<Health> health();

    Result<String> ping();

    /**
     * 广播调用,调用所有proxy
     *
     * @param apiInfo
     * @return
     */
    Result<Boolean> updateApiInfo(ApiInfo apiInfo);

    /**
     * 广播调用,更新录制配置
     *
     * @param str
     * @return
     */
    Result<Boolean> updateRecordingTraffic(ModifyType opt, String str);

    /**
     * 启动插件
     *
     * @param plugInfo
     * @return
     */
    Result<Boolean> startPlugin(PlugInfo plugInfo);


    /**
     * 停止插件
     *
     * @param plugInfo
     * @return
     */
    Result<Boolean> stopPlugin(PlugInfo plugInfo);


    /**
     * 重新加载插件
     *
     * @return
     */
    Result<Boolean> reloadFilter();


    /**
     * 更新插件
     *
     * @param name
     * @param type ("add" "remove")
     * @return
     */
    Result<Boolean> updateFilter(String name, String groups, String type);


    Result<GatewayInfo> getGatewayInfo();


}
