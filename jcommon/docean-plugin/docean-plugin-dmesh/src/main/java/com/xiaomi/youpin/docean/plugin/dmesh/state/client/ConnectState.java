package com.xiaomi.youpin.docean.plugin.dmesh.state.client;

import com.xiaomi.data.push.uds.UdsClient;
import com.xiaomi.data.push.uds.context.UdsClientContext;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/13 12:02
 * 连接服务器状态
 */
@Component
@Slf4j
public class ConnectState extends BaseState {

    @Resource
    private UdsClient client;

    @Value("$uds_app")
    private String app;

    @Resource
    private ClientFsm fsm;


    @Override
    public void execute() {
        log.info("client connect state");
        try {
            Channel channel = client.getChannel();
            if (null != channel) {
                UdsCommand request = UdsCommand.createRequest();
                request.setApp(app);
                request.setCmd("ping");
                request.setData("ping");
                UdsClientContext.ins().channel.set(channel);
                UdsCommand res = client.call(request);
                if (null != res) {
                    fsm.change(Ioc.ins().getBean(InitState.class));
                }
            }
        } catch (Throwable ex) {
            log.error(ex.getMessage());
        }
    }

    @Override
    public long delay() {
        return 500L;
    }
}
