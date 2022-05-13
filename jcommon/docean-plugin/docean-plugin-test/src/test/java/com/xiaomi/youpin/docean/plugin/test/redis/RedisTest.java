package com.xiaomi.youpin.docean.plugin.test.redis;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.common.Safe;
import com.xiaomi.youpin.docean.plugin.redis.Redis;
import com.xiaomi.youpin.docean.plugin.redis.RedisDsConfig;
import com.xiaomi.youpin.docean.plugin.redis.RedisPlugin;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 */
public class RedisTest {


    @Test
    public void testPlugin() {
        Ioc.ins().init("com.xiaomi");
        RedisPlugin plugin = Ioc.ins().getBean(RedisPlugin.class);
        RedisDsConfig config = new RedisDsConfig();
        String hosts = "";
        config.setHosts(hosts);
        config.setType("");
        config.setName("dynamic");
        plugin.add(config);
        Redis redis = Ioc.ins().getBean("redis:dynamic");
        redis.set("a","1");
        System.out.println(redis.get("a"));

        plugin.remove(config);

        Safe.runAndLog(()->{
            System.out.println(redis.get("1"));
        });

        plugin.add(config);
        Redis r = Ioc.ins().getBean("redis:dynamic");
        System.out.println(r.get("a"));

    }
}
