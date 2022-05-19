package com.xiaomi.youpin.docean.plugin.dmesh.interceptor;

import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.plugin.config.Config;
import com.xiaomi.youpin.docean.plugin.dmesh.anno.MeshMsService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dingpei@xiaomi.com
 * @author goodjava@qq.com
 * @date 1/11/21
 */
@Slf4j
public class CallNacosInterceptor extends AbstractInterceptor {


    public CallNacosInterceptor(Ioc ioc, Config config, MeshMsService reference) {
        super(ioc, config, reference);
    }

    @Override
    public void intercept0(UdsCommand req) {
    }

}
