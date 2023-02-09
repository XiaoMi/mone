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

package com.xiaomi.youpin.gateway.sidecar.processor;

import com.xiaomi.data.push.uds.context.UdsServerContext;
import com.xiaomi.data.push.uds.po.RpcCommand;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import com.xiaomi.youpin.docean.anno.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 2022/6/18
 */
@Component
@Slf4j
public class PingProcessor implements UdsProcessor<RpcCommand, RpcCommand> {

    @Override
    public RpcCommand processRequest(RpcCommand request) {
        log.info("side client:{} ping", request.getApp());
        if (request instanceof UdsCommand) {
            UdsCommand uds = (UdsCommand) request;
            UdsServerContext.ins().put(request.getApp(), uds.getChannel());
        }
        UdsCommand res = UdsCommand.createResponse(request);
        res.setData("pong");
        return res;
    }

    @Override
    public String cmd() {
        return "ping";
    }
}
