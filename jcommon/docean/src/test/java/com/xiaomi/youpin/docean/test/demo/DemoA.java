package com.xiaomi.youpin.docean.test.demo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author goodjava@qq.com
 * @Date 2021/3/3 16:19
 */
@Data
public class DemoA implements Serializable {

    private int id;

    public DemoA() {
    }

    public String f() {
        return "abc";
    }
}
