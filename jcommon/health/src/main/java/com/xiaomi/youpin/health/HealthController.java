package com.xiaomi.youpin.health;

import com.xiaomi.bo.HealthData;
import com.xiaomi.bo.JResult;
import com.xiaomi.youpin.qps.QpsAop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author goodjava@qq.com
 */
@RestController
@Slf4j
public class HealthController {

    @Autowired
    private QpsAop qps;

    @RequestMapping(value = "/health", method = RequestMethod.GET)
    public JResult<HealthData> health() {
        log.info("check http health");
        HealthData data = new HealthData();
        data.setQps(qps.getQps());
        return new JResult<>(data);
    }

}
