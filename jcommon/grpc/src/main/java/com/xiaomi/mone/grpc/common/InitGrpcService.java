package com.xiaomi.mone.grpc.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
public class InitGrpcService {


//    private NacosNaming nacosNaming = NacosConfiguration.nacosNaming();
    /**
     * grpc server的端口
     */
    @Value("${server.port}")
    private String port = "8080";

    private static final String prefix = "grpc:";

    /**
     * 这里填写项目中任意服务的全路径名，即 <packageName.serviceName> 例如:
     * com.xiaomi.mone.grpc.demo.HelloService
     */
    private static final String grpcSymbol = "com.xiaomi.mone.grpc.demo.HelloService";

    /**
     * 这里填写自定义项目名
     */
    private static final String appName = "grpc-demo";

    @PostConstruct
    public void init() throws UnknownHostException {
        String host = System.getenv("host.ip") == null ? InetAddress.getLocalHost().getHostAddress() : System.getenv("host.ip");
        //appName请设置自己的项目名，设置之前去nacos看看是否有人已经用了你的appname了,这个东西是不能重复的
        try {
//            Instance instance = new Instance();
//            instance.setIp(host);
//            instance.setPort(Integer.parseInt(port));
//            instance.setWeight(1.0D);
//            instance.setClusterName("DEFAULT");
//            Map<String, String> metaData = new HashMap<>();
//            metaData.put("grpc_symbol", grpcSymbol);
//            instance.setMetadata(metaData);
//            nacosNaming.registerInstance(prefix+appName,instance);
//            Runtime.getRuntime().addShutdownHook(new Thread(()->{
//                try {
//                    nacosNaming.deregisterInstance(prefix+appName, host, Integer.parseInt(port));
//                } catch (Exception ignored) {
//                }
//            }));
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }


}