package com.xiaomi.miapi.config;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.xiaomi.data.push.nacos.NacosNaming;
import org.apache.dubbo.common.utils.NetUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class InitService {
    @Resource(name = "nacosNamingSt")
    private NacosNaming nacosNamingSt;

    @Resource(name = "nacosNamingOl")
    private NacosNaming nacosNamingOl;
 
    @Value("${server.port}")
    private String httpPort;
 
    @Value("${dubbo.group}")
    private String group;

    @PostConstruct
    public void init() {
        String host = System.getenv("host.ip") == null ? NetUtils.getLocalHost() : System.getenv("host.ip");
        final String port = httpPort;
        //appName请设置自己的项目名，设置之前去nacos看看是否有人已经用了你的appname了，不会看的找@丁佩，这个东西是不能重复的
        final String appName = "MiApiManager";
        NacosNaming nacosNaming;
        if (group.equals("online")){
            nacosNaming = nacosNamingOl;
        }else {
            nacosNaming = nacosNamingSt;
        }
        try {
            nacosNaming.registerInstance(appName, host, Integer.valueOf(port), group);

            Runtime.getRuntime().addShutdownHook(new Thread(()->{
                try {
                    System.out.println("stop");
                    nacosNaming.deregisterInstance(appName, host, Integer.valueOf(port), group);
                } catch (Exception e) {
                    //ignore
                }
            }));
        } catch (Exception ignored) {
        }
    }


}