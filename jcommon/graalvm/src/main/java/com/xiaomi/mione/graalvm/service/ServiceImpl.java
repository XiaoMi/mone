package com.xiaomi.mione.graalvm.service;

import com.xiaomi.mione.graalvm.anno.LogAnno;
import com.xiaomi.youpin.docean.anno.Service;

/**
 * @author goodjava@qq.com
 * @date 6/4/21
 */
@Service
public class ServiceImpl implements IService,MService,ZService,DestoryService {

    @LogAnno
    @Override
    public String hi() {
        return "hi";
    }

    @Override
    public String version() {
        return "0.0.1";
    }

    @Override
    public String destory() {
        return "destory";
    }

    @Override
    public String z() {
        return "z";
    }

    @Override
    public void $destory() {
        System.out.println("destory");
    }
}
