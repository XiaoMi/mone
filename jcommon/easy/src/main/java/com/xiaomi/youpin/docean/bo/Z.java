package com.xiaomi.youpin.docean.bo;

import lombok.Setter;

/**
 * @author goodjava@qq.com
 * @date 3/28/21
 */
public class Z {

    @Setter
    private A a;

    public String hi() {
        return "hi:3.0:" + a.hi();
    }

}
