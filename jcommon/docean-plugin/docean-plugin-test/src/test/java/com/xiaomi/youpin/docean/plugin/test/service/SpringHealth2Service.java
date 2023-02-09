package com.xiaomi.youpin.docean.plugin.test.service;


import com.xiaomi.youpin.docean.plugin.test.component.HealthComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author goodjava@qq.com
 * @date 2022/9/16 10:46
 */
@Service
public class SpringHealth2Service {


    @Autowired
    private HealthComponent component;

    public String hi() {
        return "SpringHealth2Service:" + component.hi();
    }

    @PostConstruct
    public void init1() {
        System.out.println("SpringHealth2Service init");
    }

    @PreDestroy
    public void destory1() {
        System.out.println("SpringHealth2Service destory");
    }


}
