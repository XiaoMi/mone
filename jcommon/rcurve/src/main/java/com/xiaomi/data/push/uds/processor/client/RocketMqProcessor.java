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

package com.xiaomi.data.push.uds.processor.client;

import com.google.common.collect.Lists;
import com.xiaomi.data.push.common.Send;
import com.xiaomi.data.push.uds.listener.UdsEvent;
import com.xiaomi.data.push.uds.listener.UdsListener;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


/**
 * @author goodjava@qq.com
 * mesh server 会把rocketmq注册的信息发回来
 */
@Slf4j
public class RocketMqProcessor implements UdsProcessor {

    private List<UdsListener> listenerList = Lists.newLinkedList();


    public RocketMqProcessor() {
    }

    public void regListener(UdsListener listener) {
        listenerList.add(listener);
    }


    @Override
    public void processRequest(UdsCommand req) {
        UdsCommand response = UdsCommand.createResponse(req);
        try {
            log.info("{} {}", req.getApp(), req.getData());
            this.listenerList.stream().forEach(l -> l.handle(new UdsEvent(req.getApp(), req.getData())));
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
            response.setCode(500);
            response.setMessage("call method error:" + req.getMethodName() + ex.getMessage());
        }
        Send.send(req.getChannel(), response);
    }

    @Override
    public String cmd() {
        return "rocketmq_consumer";
    }
}
