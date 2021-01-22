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

package com.xiaomi.youpin.tesla.rcurve.proxy.listener;

import com.xiaomi.data.push.common.Send;
import com.xiaomi.data.push.uds.context.NetEvent;
import com.xiaomi.data.push.uds.context.NetListener;
import com.xiaomi.data.push.uds.context.NetType;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.tesla.rcurve.proxy.common.CurveVersion;
import com.xiaomi.youpin.tesla.rcurve.proxy.manager.DsManager;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/13 11:27
 * 网络的一些事件都会收集到这里
 */
@Service(name = "com.xiaomi.data.push.uds.context.NetListener")
@Slf4j
public class ProxyNetListener implements NetListener {

    @Resource
    private DsManager dsManager;


    @Override
    public void handle(NetEvent netEvent) {
        log.info("{}", netEvent);

        //注销数据源
        if (netEvent.getType().equals(NetType.inactive)) {
            dsManager.remove(netEvent.getApp());
        }

        //发送一些系统信息给客户端
        if (netEvent.getType().equals(NetType.active)) {
            Send.sendMessage(netEvent.getChannel(), new CurveVersion().toString());
        }

    }
}
