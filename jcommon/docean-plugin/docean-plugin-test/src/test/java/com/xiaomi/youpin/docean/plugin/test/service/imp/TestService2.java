package com.xiaomi.youpin.docean.plugin.test.service.imp;

import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.test.service.ITestService;

/**
 * @author goodjava@qq.com
 * @date 2022/7/12 23:26
 */
@Service
public class TestService2 implements ITestService {

    @Override
    public String test() {
        return "test";
    }
}
