package com.xiaomi.mone.monitor.service.prometheus;

import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.api.AlarmServiceExtension;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/4/21 2:35 PM
 */
@Service
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class AlarmServiceExtensionImpl implements AlarmServiceExtension {

    @Override
    public Result getGroup(Integer iamId, String user) {
        return Result.success("example");
    }
}
