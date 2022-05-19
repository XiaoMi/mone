package com.xiaomi.youpin.docean.plugin.test.mybatis;

import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2020/7/2
 */
@Data
public class Test {

    private int id;



    public Test(int id) {
        this.id = id;
    }


    public Test() {
    }
}
