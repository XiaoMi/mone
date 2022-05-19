package com.xiaomi.youpin.docean.test;

import com.xiaomi.youpin.docean.anno.Bean;
import com.xiaomi.youpin.docean.anno.Configuration;

/**
 * @author goodjava@qq.com
 * @date 2020/6/24
 */
@Configuration
public class TestConfiguration {

    @Bean
    public String strBean() {
        return "abc";
    }

}
