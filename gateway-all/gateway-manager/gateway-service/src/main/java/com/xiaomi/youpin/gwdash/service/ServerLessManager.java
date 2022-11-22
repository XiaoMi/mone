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

package com.xiaomi.youpin.gwdash.service;

import com.google.gson.Gson;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.gwdash.common.Keys;
import com.xiaomi.youpin.infra.rpc.Result;
import com.youpin.xiaomi.tesla.bo.ScriptInfo;
import com.youpin.xiaomi.tesla.service.TeslaGatewayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author goodjava@qq.com
 * @Date 2021/3/30 11:45
 */
@Service
@Slf4j
public class ServerLessManager {


    @Reference(check = false, group = "${tesla.gateway.dubbo.group}", interfaceClass = TeslaGatewayService.class, cluster = "broadcast")
    private TeslaGatewayService teslaGatewayService;


    @Autowired
    private Redis redis;


    /**
     * 发布server less 的jar包
     */
    public void deploy(long apiId, ScriptInfo apiInfo) {
        log.info("deploy begin apiId:{} scriptInfo :{}", apiId, apiInfo);
        redis.set(Keys.scriptKey(apiId), new Gson().toJson(apiInfo));
        Result<Boolean> res = teslaGatewayService.deployServerLessJar(String.valueOf(apiId), apiInfo.getJarUrl());
        log.info("deploy {} res:{}", apiId, res);
    }
}
