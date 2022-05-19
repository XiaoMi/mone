package com.xiaomi.youpin.docean.test.aop;

import com.xiaomi.youpin.docean.anno.Service;

/**
 * @author goodjava@qq.com
 * @date 5/14/22
 */
@Service
public class MyService {

    public void log() {
        System.out.println("log");
    }

}
