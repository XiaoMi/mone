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

package com.xiaomi.youpin.tesla.agent.bootstrap;

import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.rpc.common.Pair;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.tesla.agent.interceptor.Log;
import com.xiaomi.youpin.tesla.agent.service.IService;

import javax.annotation.Resource;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2020/6/20
 */
@Component(desc = "processor service")
public class ProcessorInit implements IService {

    @Resource
    private RpcClient client;

    public ProcessorInit(RpcClient client) {
        this.client = client;
    }

    public ProcessorInit() {
    }

    @Log
    @Override
    public void init() {
        Set<NettyRequestProcessor> set = Ioc.ins().getBeans(NettyRequestProcessor.class);
        client.setProcessorList(set.stream().map(it-> new Pair<>(it.cmdId(),it)).collect(Collectors.toList()));
    }
}
