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

package com.xiaomi.youpin.gateway.sidecar;

import com.xiaomi.data.push.uds.po.RpcCommand;
import com.xiaomi.mone.grpc.GrpcClientGroup;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.plugin.config.Config;
import com.xiaomi.youpin.gateway.common.EnvUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.mone.api.IClient;
import run.mone.api.IServer;
import run.mone.mesh.bo.SideCarRequest;
import run.mone.mesh.bo.SideCarResponse;

import javax.annotation.PostConstruct;

/**
 * @author goodjava@qq.com
 * @date 2022/6/18
 */
@Service
@Slf4j
public class SidecarService {

    /**
     * 通过埋入env开启
     */
    public static final String SIDE_CAR_ENABLE = "side.car.enable";

    public static final String SIDE_CAR_REMOTE = "side.car.remote";

    private IServer server;

    private IClient client;

    private boolean sideCar;

    private String sideCarRemote;

    @PostConstruct
    public void init() {
        sideCar = EnvUtils.getEnvOrProperty(SIDE_CAR_ENABLE, "false").equals("true");
        sideCarRemote = EnvUtils.getEnvOrProperty(SIDE_CAR_REMOTE, "false");
        if (sideCar) {
            log.info("sidecar service init");
            Config config = new Config();
            config.put("sidecarServer", "true");
            config.put("openClientGroup", "false");
            config.put("sidecarGroupConfig", "127.0.0.1:8123:pingSideCar");
            config.put("sidecarGrpc", "false");
            config.put("sidecarRemote", sideCarRemote);

            Ioc ioc = Ioc.ins().putBean(config).init("run.mone.docean.plugin", "com.xiaomi.youpin.gateway.sidecar");
            server = ioc.getBean("sideCarServer");
            client = ioc.getBean(GrpcClientGroup.class);
            log.info("sidecar server:{}", server);
        }
    }

    public FilterResponse call(RpcCommand req) {
        RpcCommand res = server.call(req);
        FilterResponse filterResponse = res.getData(FilterResponse.class);
        return filterResponse;
    }

    /**
     * sidecar 是 server 模式启动的
     *
     * @param request
     * @return
     */
    public SideCarResponse call(SideCarRequest request) {
        return (SideCarResponse) client.callServer(request);
    }


}
