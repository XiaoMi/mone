package com.xiaomi.youpin.docean.test;

import com.xiaomi.youpin.docean.bo.A;
import com.xiaomi.youpin.docean.common.EasyClassLoader;
import com.xiaomi.youpin.docean.common.ReflectUtils;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author goodjava@qq.com
 * @date 3/28/21
 */
public class EasyClassLoaderTest {


    @Test
    public void testClassLoader() throws ClassNotFoundException, IllegalAccessException, InstantiationException, MalformedURLException, NoSuchFieldException {
        File file = new File("/tmp/easy-1.4-SNAPSHOT.jar");
        URL url = file.toURI().toURL();
        EasyClassLoader loader = new EasyClassLoader(new URL[]{url});
        A a = new A();
        Object obj =  loader.loadClass("com.xiaomi.youpin.docean.bo.E").newInstance();
        Field field = obj.getClass().getDeclaredField("a");
        ReflectUtils.setField(obj,field,a);
        System.out.println(ReflectUtils.invokeMethod(obj,"hi",new Object[]{}));
    }
}
