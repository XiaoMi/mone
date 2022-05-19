package com.xiaomi.youpin.docean.test.demo2;

import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.test.anno.CAfter;
import com.xiaomi.youpin.docean.test.anno.CBefore;

/**
 * @author goodjava@qq.com
 * @date 5/14/22
 */
@Service
public class DemoService2 {


    @CBefore
    @CAfter
    public String hi(String name,String name2) {
        return "hi:" + name+" " +name2;
    }

}
