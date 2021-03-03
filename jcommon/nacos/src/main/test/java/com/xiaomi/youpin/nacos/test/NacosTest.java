package com.xiaomi.youpin.nacos.test;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.xiaomi.data.push.nacos.NacosConfig;
import com.xiaomi.data.push.nacos.NacosNaming;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class NacosTest {


    @Test
    public void testRegisterInstance() throws NacosException {
        NacosNaming nacosNaming = new NacosNaming();
        nacosNaming.setServerAddr("xxxx");
        nacosNaming.init();
        nacosNaming.registerInstance("abc", "127.0.0.1", 99);
    }


    @Test
    public void testDeregisterInstance() throws NacosException {
        NacosNaming nacosNaming = new NacosNaming();
        nacosNaming.setServerAddr("xxxx");
        nacosNaming.init();
        nacosNaming.deregisterInstance("abc", "127.0.0.1", 99);
    }


    @Test
    public void testGetAllInstances() throws NacosException {
        NacosNaming nacosNaming = new NacosNaming();
        nacosNaming.setServerAddr("xxx");
        nacosNaming.init();
        List<Instance> list = nacosNaming.getAllInstances("providers:com.xiaomi.youpin.gis.service.order.ProductInfoOrderService");
        list.forEach(it -> {
            System.out.println(it.getServiceName() + ":" + it.getIp() + ":" + it.getPort());
        });
    }


    @Test
    public void testGetConfig() {
        NacosConfig config = new NacosConfig();
        config.setDataId("gateway_detail");
        config.setGroup("DEFAULT_GROUP");
        config.setServerAddr("xxxx");
        config.init();
        Map<String, String> str = config.getConfig();
        System.out.println(str);
    }


    @Test
    public void testGetConfigStr() throws NacosException, IOException {
        NacosConfig config = new NacosConfig();
        config.setServerAddr("xxx");
        config.init();
        String str = config.getConfigStr("gateway_detail", "DEFAULT_GROUP", 1000);
        System.out.println(str);


        Properties p = new Properties();
        p.load(new StringReader(str));
        System.out.println(p.get("newUserWelcome"));
    }

    @Test
    public void testGet() {
        NacosConfig config = new NacosConfig();
        config.setServerAddr("xxxx");
        config.setDataId("gateway_detail");
        config.setGroup("DEFAULT_GROUP");
        config.init();
        String str = config.getConfig("newUserWelcome");
        System.out.println(str);
    }


    @Test
    public void testServiceList() {
        NacosNaming nacosNaming = new NacosNaming();
        nacosNaming.setServerAddr("127.0.0.1:80");
        nacosNaming.init();
        String res = nacosNaming.serviceList("public", 1, 100, "tesla_server_dev");
        System.out.println(res);
    }


    @Test
    public void testSubscribe() throws InterruptedException {
        NacosNaming nacosNaming = new NacosNaming();
        nacosNaming.setServerAddr("xxxx");
        nacosNaming.init();

        EventListener listener = new EventListener() {
            @Override
            public void onEvent(Event event) {
                System.out.println(event);
                if (event instanceof NamingEvent) {
                    NamingEvent ne = (NamingEvent) event;
                    System.out.println(ne.getInstances().size() + ":" + new Date());
                }
            }
        };
        nacosNaming.subscribe("zzyTest", listener);

        //解除订阅
//        nacosNaming.unsubscribe("zzyTest", listener);

        Thread.currentThread().join();
    }

}
