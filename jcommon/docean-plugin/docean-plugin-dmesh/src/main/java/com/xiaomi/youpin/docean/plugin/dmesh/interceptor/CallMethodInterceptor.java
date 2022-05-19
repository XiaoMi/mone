package com.xiaomi.youpin.docean.plugin.dmesh.interceptor;

import com.google.gson.Gson;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.plugin.config.Config;
import com.xiaomi.youpin.docean.plugin.dmesh.anno.MeshMsService;
import com.xiaomi.youpin.docean.plugin.dmesh.anno.MeshReference;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 1/9/21
 * <p>
 * 这里会处理dubbo的调用(看起来是调用本地方法,其实是调用远程的dubbo服务)
 */
@Slf4j
public class CallMethodInterceptor extends AbstractInterceptor {


    private MeshReference reference;

    public CallMethodInterceptor(Ioc ioc, Config config, MeshReference reference) {
        super(ioc, config, null);
        this.reference = reference;
    }

    @Override
    public void intercept0(UdsCommand command) {
        command.setServiceName(reference.name());
        command.setRemoteApp(reference.app());
        command.setTimeout(reference.timeout());

        command.setServiceName(reference.interfaceClass().getName());

        command.putAtt("mesh", String.valueOf(reference.mesh()));
        command.putAtt("group", reference.group());
        command.putAtt("version", reference.version());
        command.putAtt("timeout", String.valueOf(reference.timeout()));
        //是否是远程调用(调用远程dubbo)
        command.putAtt("remote", String.valueOf(reference.remote()));
        command.setCmd("dubboCall");


    }

}
