package run.mone.mimeter.dashboard.config;

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

        final String appName = "MiMeter";
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