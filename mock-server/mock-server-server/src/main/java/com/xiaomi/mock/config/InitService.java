package com.xiaomi.mock.config;
 
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.NetUtils;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import com.xiaomi.data.push.nacos.NacosNaming;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class InitService {
    @Resource
    private NacosNaming nacosNaming;
 
    @Value("$server.port")
    private String httpPort;
 
    @Value("$dubbo.group")
    private String group;
 
    @PostConstruct
    public void init() {
        String host = System.getenv("host.ip") == null ? NetUtils.getLocalHost() : System.getenv("host.ip");
        final String port = httpPort;
        //appName请设置自己的项目名，设置之前去nacos看看是否有人已经用了你的appname了，不会看的找@丁佩，这个东西是不能重复的
        final String appName = "mock-server";
        try {
            nacosNaming.registerInstance(appName, host, Integer.parseInt(httpPort), group);
 
            Runtime.getRuntime().addShutdownHook(new Thread(()->{
                try {
                    System.out.println("stop");
                    nacosNaming.deregisterInstance(appName, host, Integer.valueOf(port), group);
                } catch (Exception e) {
                    //ignore
                }
            }));
        } catch (Exception e) {
            System.out.print(e);
        }
    }
}