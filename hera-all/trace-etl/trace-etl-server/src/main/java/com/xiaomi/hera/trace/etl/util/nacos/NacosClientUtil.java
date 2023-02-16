package com.xiaomi.hera.trace.etl.util.nacos;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.NacosNamingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

@Component
public class NacosClientUtil {

    private static final Logger log = LoggerFactory.getLogger(NacosClientUtil.class);

    @Value("${nacos.address}")
    private String nacosAddr;
    @Value("${prometheus.http.server.port}")
    private int prometheusPort;

    private ConfigService nacosConfigService;

    private String serverIp = System.getenv("CONTAINER_S_IP");
    private String hostName = System.getenv("CONTAINER_S_HOSTNAME");

    @PostConstruct
    public void registNacos() {
        try {
            Properties properties = new Properties();
            properties.put("serverAddr", nacosAddr);
            nacosConfigService = NacosFactory.createConfigService(properties);

            if (StringUtils.isEmpty(serverIp)) {
                serverIp = InetAddress.getLocalHost().getHostAddress();
            }
            List<NacosNamingService> nacos = getNacos();
            for (NacosNamingService nacosNamingService : nacos) {
                log.info("nacos regist prometheus port is : " + prometheusPort);
                Instance instance = new Instance();
                instance.setIp(serverIp);
                instance.setPort(55255);
                Map<String, String> map = new HashMap<>();
                map.put("jaegerQuery_port", String.valueOf(prometheusPort));
                map.put("jaegerQuery_host_name", String.valueOf(hostName));
                instance.setMetadata(map);
                nacosNamingService.registerInstance("jaegerQuery_ip_port", instance);
                // deregister
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    log.info("nacos shutdown hook deregister instance");
                    try {
                        nacosNamingService.deregisterInstance("jaegerQuery_ip_port", instance.getIp(), instance.getPort());
                    } catch (Exception e) {
                        log.error("nacos shutdown hook error : " + e.getMessage());
                    }
                }));
            }
        } catch (Exception e) {
            log.error("注册IP到nacos失败：", e);
        }
    }

    public List<NacosNamingService> getNacos() {
        List<NacosNamingService> list = new ArrayList<>();
        try {
            NacosNamingService chinaNacosNamingService = new NacosNamingService(nacosAddr);
            list.add(chinaNacosNamingService);
        } catch (Exception e) {
            log.error("初始化NacosNamingService失败：", e);
        }
        return list;
    }
}
