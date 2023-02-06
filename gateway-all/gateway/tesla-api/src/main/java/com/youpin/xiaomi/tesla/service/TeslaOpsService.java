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
import com.youpin.xiaomi.tesla.bo.*;

import java.util.HashMap;
import java.util.List;

/**
 * @author goodjava@qq.com
 */
public interface TeslaOpsService {

    /**
     * 发送ping 信息
     *
     * @return
     */
    Result<String> ping();


    /**
     * 更新gateway server  信息
     *
     * @param info
     * @return
     */
    Result<ServerInfo> updateGatewayInfo(GatewayInfo info);

    /**
     * 获取机器分组
     *
     * @param ip
     * @return
     */
    Result<String> getMachineGroupByIp(String ip);


    /**
     * 不按分页获取路由信息
     * 返回所有api info
     *
     * @return
     */
    Result<List<ApiInfo>> apiInfoList();



    /**
     * 按分页获取路由信息
     *
     * @return
     */
    Result<ApiInfoList> apiInfoList(int pageNum, int pageSize);

    /**
     * 按分页获取路由信息
     * @return
     */
    Result<ApiInfoList> apiInfoList(ApiInfoReq param);


    /**
     * 获取插件信息列表
     *
     * @return
     */
    Result<List<PlugInfo>> pluginInfoList();

    /**
     * 根据url获取创建者信息列表
     */
    Result<HashMap<String, AccountVo>> getAccountsByUrls(UrlInfoParam urlInfoParam);

}
