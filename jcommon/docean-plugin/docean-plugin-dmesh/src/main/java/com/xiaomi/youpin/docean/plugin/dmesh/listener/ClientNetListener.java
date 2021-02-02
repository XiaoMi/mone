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

package com.xiaomi.youpin.docean.plugin.dmesh.listener;

import com.xiaomi.data.push.uds.context.NetEvent;
import com.xiaomi.data.push.uds.context.NetListener;
import com.xiaomi.data.push.uds.context.NetType;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.dmesh.state.client.ClientFsm;
import com.xiaomi.youpin.docean.plugin.dmesh.state.client.ConnectState;

import javax.annotation.Resource;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/13 17:52
 */
@Service(name = "clientNetListener")
public class ClientNetListener implements NetListener {


    @Resource
    private ClientFsm fsm;

    @Override
    public void handle(NetEvent event) {
        //如果发生异常直接切换为连接状态
        if (event.getType().equals(NetType.exception) || event.getType().equals(NetType.inactive)) {
            fsm.change(Ioc.ins().getBean(ConnectState.class));
        }
    }
}
