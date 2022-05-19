package com.xiaomi.youpin.docean.plugin.test;

import com.xiaomi.youpin.docean.plugin.dubbo.anno.Service;

/**
 * @author goodjava@qq.com
 * @date 2020/6/25
 */
@Service(interfaceClass = IS.class)
public class S implements IS {

    @Override
    public String hi() {
        return "hi";
    }
}
