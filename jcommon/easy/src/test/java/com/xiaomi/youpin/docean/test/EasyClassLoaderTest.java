/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
