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
