package com.xiaomi.youpin.docean.plugin.test.config;

import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.Getter;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @date 2023/12/8 13:47
 */

@Service
public class ConfigService {

    @Resource(name = "^ddd")
    private Demo demo;

    @Getter
    @Value("$ddd")
    private String val;

    public String hi() {
        return demo.hi();
    }



}
