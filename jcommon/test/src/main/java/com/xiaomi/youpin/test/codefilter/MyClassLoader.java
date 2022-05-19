package com.xiaomi.youpin.test.codefilter;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author goodjava@qq.com
 * @date 3/14/21
 */
public class MyClassLoader extends URLClassLoader {

    public MyClassLoader(URL[] urls) {
        super(urls);
    }
}
