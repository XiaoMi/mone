package com.xiaomi.youpin.docean.test.demo;

import lombok.Data;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @date 2020/6/21
 */
@Data
public class DemoVo {

    private String id;
    private String name;
    private int age;

    private long t = System.currentTimeMillis();


    public DemoVo() {
        System.out.println("------>create demo vo");
    }

    @Resource
    private DemoDao demoDao;


    @Override
    public String toString() {
        return "DemoVo" + this.getClass() + ":" + t;
    }
}
