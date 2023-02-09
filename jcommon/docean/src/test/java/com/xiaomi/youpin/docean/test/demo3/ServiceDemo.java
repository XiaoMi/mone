package com.xiaomi.youpin.docean.test.demo3;

import com.xiaomi.youpin.docean.anno.Service;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @date 2023/1/15 12:09
 */
@Service
public class ServiceDemo {


    @Resource
    private DaoDemo daoDemo;


    public String call() {
        return "service";
    }

}
