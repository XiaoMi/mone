package com.xiaomi.youpin.docean.common;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.function.Function;

/**
 * @author goodjava@qq.com
 * @date 3/28/21
 */
public class EasyClassLoader extends URLClassLoader {

    private Function<String, byte[]> function;

    public EasyClassLoader(URL[] urls) {
        super(urls);
    }

}
