package com.xiaomi.youpin.docean.plugin.test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.xiaomi.youpin.docean.bo.Bean;
import com.xiaomi.youpin.docean.plugin.dubbo.DubboPlugin;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2020/6/25
 */
public class DubboPluginTest {

    @Test
    public void testPlugin() throws InterruptedException {
        String ip = "" ;
        Map<String,String> properties = ImmutableMap.<String,String>builder()
                .put("appName","testApp")
                .put("regAddress","nacos://"+ip+":80")
                .build();
        List<Bean> beans = Lists.newArrayList();
        DubboPlugin plugin = new DubboPlugin();
        plugin.init(new HashSet<>(), null);
        Thread.currentThread().join();
    }
}
