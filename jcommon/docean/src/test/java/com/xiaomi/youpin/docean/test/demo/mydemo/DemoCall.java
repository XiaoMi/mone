package com.xiaomi.youpin.docean.test.demo.mydemo;

import com.xiaomi.youpin.docean.anno.Service;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @date 2023/11/18 14:52
 */


@Service
public class DemoCall {

    @Resource(name = "$demoName")
    private MyDemo demo;


    @Getter
    @Value("$val")
    private String val;

    @Resource
    private DemoInterface demoInterface;


    //This is just an interface, but if it only has one implementation class, then Ioc will automatically find this unique implementation class.
    @Resource
    private ICall call;



    public String hi() {
        System.out.println(demoInterface.hi());
        return demo.hi();
    }


  
    public String call() {
        return call.call();
    }


}
