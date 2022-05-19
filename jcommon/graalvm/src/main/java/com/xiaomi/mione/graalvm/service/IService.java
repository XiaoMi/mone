package com.xiaomi.mione.graalvm.service;

import com.xiaomi.mione.graalvm.anno.LogAnno;

/**
 * @author goodjava@qq.com
 * @date 6/4/21
 */
public interface IService {

    @LogAnno
    String hi();


    String version();
}
