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

package run.mone.mimeter.dashboard.service.impl;

import com.xiaomi.youpin.gateway.manager.bo.openApi.GatewayApiInfoList;
import com.xiaomi.youpin.gateway.manager.bo.openApi.GetGatewayApiInfoListReq;
import com.xiaomi.youpin.gateway.manager.service.IGatewayOpenApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import run.mone.mimeter.dashboard.bo.common.Result;

import static com.xiaomi.youpin.tesla.traffic.recording.api.bo.enums.GatewayEnvTypeEnum.*;
import static run.mone.mimeter.dashboard.exception.CommonError.WrongGatewayEnvError;

@Service
@Slf4j
public class GatewayService {

    @DubboReference(check = false, interfaceClass = IGatewayOpenApi.class, group = "${ref.gateway.service.group.staging}", timeout = 4000)
    private IGatewayOpenApi iGatewayOpenApiStaging;

    @DubboReference(check = false, interfaceClass = IGatewayOpenApi.class, group = "${ref.gateway.service.group.online}", timeout = 4000)
    private IGatewayOpenApi iGatewayOpenApiOnline;

    @DubboReference(check = false, interfaceClass = IGatewayOpenApi.class, group = "${ref.gateway.service.group.intranet}", timeout = 4000)
    private IGatewayOpenApi iGatewayOpenApiIntranet;

    public Result<GatewayApiInfoList> getGatewayApiInfoList(GetGatewayApiInfoListReq req, String user, int env) {
        switch (env) {
            case ONLINE_CODE: {
                return Result.success(iGatewayOpenApiOnline.getGatewayApiInfoList(req, user).getData());
            }
            case INTRANET_CODE: {
                return Result.success(iGatewayOpenApiIntranet.getGatewayApiInfoList(req, user).getData());
            }
            case STAGING_CODE: {
                return Result.success(iGatewayOpenApiStaging.getGatewayApiInfoList(req, user).getData());
            }
            default: {
                log.error("GatewayService.getGatewayApiInfoList error, env is wrong: {} ", env);
                return Result.fail(WrongGatewayEnvError.code, WrongGatewayEnvError.message);
            }

        }
    }

}
