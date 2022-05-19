package com.xiaomi.youpin.docean.plugin.json;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.plugin.IPlugin;

import java.util.Set;

/**
 * @author goodjava@qq.com
 */
@DOceanPlugin
public class JsonPlugin implements IPlugin {


    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        Json json = new Json();
        ioc.putBean(json);
    }

    @Override
    public String version() {
        return "0.0.1:2020-07-01";
    }
}
