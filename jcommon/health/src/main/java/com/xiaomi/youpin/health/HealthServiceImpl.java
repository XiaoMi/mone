package com.xiaomi.youpin.health;


import com.xiaomi.bo.HealthData;
import com.xiaomi.bo.JResult;
import com.xiaomi.youpin.qps.QpsAop;
import com.xiaomi.youpin.service.HealthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author goodjava@qq.com
 */
@Slf4j
@Service(interfaceClass = HealthService.class, group = "${health_dubbo_group:online}")
public class HealthServiceImpl implements HealthService {

    @Autowired
    private QpsAop qpsAop;

    @Override
    public JResult<HealthData> health() {
        log.info("check health");
        HealthData data = new HealthData();
        data.setQps(qpsAop.getQps());
        return new JResult<>(data);
    }


}
