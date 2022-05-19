package com.xiaomi.youpin.docean.plugin.dmesh.interceptor;

import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.bo.Bean;
import com.xiaomi.youpin.docean.plugin.config.Config;
import com.xiaomi.youpin.docean.plugin.dmesh.anno.MeshMsService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 1/9/21
 */
@Slf4j
public class CallMysqlInterceptor extends AbstractInterceptor {

    private Ioc ioc;


    public CallMysqlInterceptor(Ioc ioc, Config config, MeshMsService reference) {
        super(ioc, config, reference);
        this.ioc = ioc;
    }

    @Override
    public void intercept0(UdsCommand req) {

    }


    @Override
    public void intercept1(UdsCommand req, Object o) {
        Bean bean = ioc.getBeanInfo(o.toString());
        if (null != bean) {
            req.putAtt("lookup", bean.getLookup());
        }
    }
}
