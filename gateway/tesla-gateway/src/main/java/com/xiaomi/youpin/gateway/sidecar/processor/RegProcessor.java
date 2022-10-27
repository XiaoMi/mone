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

import com.xiaomi.data.push.common.Send;
import com.xiaomi.data.push.uds.context.UdsServerContext;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import com.xiaomi.youpin.docean.anno.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 2022/6/19
 * sidecar 注册过来会调用此接口
 */
@Component
@Slf4j
public class RegProcessor implements UdsProcessor<UdsCommand,UdsCommand> {

    @Override
    public UdsCommand processRequest(UdsCommand request) {
        log.info("side client:{} reg", request.getApp());
        UdsServerContext.ins().put(request.getApp(), request.getChannel());
        UdsCommand res = UdsCommand.createResponse(request);
        res.setData("reg success");
        Send.sendResponse(request.getChannel(), res);
        return null;
    }

    @Override
    public String cmd() {
        return "reg";
    }
}
