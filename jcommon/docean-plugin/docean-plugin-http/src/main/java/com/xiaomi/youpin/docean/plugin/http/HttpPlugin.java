package com.xiaomi.youpin.docean.plugin.http;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.plugin.IPlugin;

import java.util.Set;

/**
 * @author goodjava@qq.com
 */
@DOceanPlugin
public class HttpPlugin implements IPlugin {

    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        Http http = new Http();
        ioc.putBean(http);
    }
}
