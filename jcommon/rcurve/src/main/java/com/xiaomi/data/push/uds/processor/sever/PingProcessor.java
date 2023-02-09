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

package com.xiaomi.data.push.uds.processor.sever;

import com.xiaomi.data.push.uds.context.UdsServerContext;
import com.xiaomi.data.push.uds.handler.UdsServerHandler;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import io.netty.util.Attribute;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class PingProcessor implements UdsProcessor<UdsCommand, UdsCommand> {

    @Override
    public UdsCommand processRequest(UdsCommand request) {
        Attribute<String> attr = request.getChannel().attr(UdsServerHandler.app);
        attr.setIfAbsent(request.getApp());
        log.info("ping:{}", request.getApp());
        UdsServerContext.ins().put(request.getApp(), request.getChannel());
        UdsCommand res = UdsCommand.createResponse(request);
        res.setData("pong");
        return res;
    }

    @Override
    public String cmd() {
        return "ping";
    }
}
