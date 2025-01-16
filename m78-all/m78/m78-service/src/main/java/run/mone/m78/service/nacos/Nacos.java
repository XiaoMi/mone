package run.mone.m78.service.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import run.mone.m78.api.constant.AgentConstant;
import run.mone.m78.common.NetUtil;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Service

/**
 * Nacos类负责初始化节点并将其注册到Nacos服务。
 * 该类使用Spring的@Service注解进行标注，并在应用启动时通过@PostConstruct注解的方法进行初始化。
 * 主要功能包括从配置文件中读取Nacos服务地址和服务名称，并将本地实例注册到Nacos服务中。
 *
 * 使用了@Slf4j注解来启用日志记录功能。
 *
 * 依赖的配置项：
 * - nacos.config.addrs: Nacos服务地址
 * - nacos.service.name: Nacos服务名称
 *
 * 异常：
 * - NacosException: 在注册实例到Nacos时可能抛出的异常
 */

public class Nacos {

    @Value("${nacos.config.addrs}")
    private String nacosAddress;

    @Value("${nacos.service.name}")
    private String nacosServiceName;

    /**
     * 初始化节点并注册到Nacos服务
     *
     * @throws NacosException 如果注册实例到Nacos时发生错误
     */
    @PostConstruct
    public void initNode() throws NacosException {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, nacosAddress);
        NamingService naming = NacosFactory.createNamingService(properties);
        Instance instance = new Instance();
        instance.setIp(NetUtil.getLocalIp());
        instance.setPort(AgentConstant.AGENT_PORT);
        instance.setHealthy(true);
        naming.registerInstance(nacosServiceName, instance);
    }


}
