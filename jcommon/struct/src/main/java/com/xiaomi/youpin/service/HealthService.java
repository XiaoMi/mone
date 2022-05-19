package com.xiaomi.youpin.service;

import com.xiaomi.bo.HealthData;
import com.xiaomi.bo.JResult;

/**
 * @author goodjava@qq.com
 */
public interface HealthService {

    JResult<HealthData> health();

}
