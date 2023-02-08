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

package com.xiaomi.mione.graalvm.bootstrap;

import com.xiaomi.mione.graalvm.service.AB;
import com.xiaomi.mione.graalvm.service.IA;
import com.xiaomi.mione.graalvm.service.IB;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Author goodjava@qq.com
 * @Date 2021/6/10 10:31
 */
public class ProxyRun {


    public static void main(String[] args) throws ClassNotFoundException {
        System.out.println("run");
        AB ab = new AB();
        //com.xiaomi.mione.graalvm.service.IA
        Class<?> c = Class.forName("com.xiaomi.mione.graalvm.service.IA");
        System.out.println(c);
        String as = args[0];
        //com.xiaomi.mione.graalvm.service.IB
        Class<?> c2 = Class.forName("com.xiaomi.mione.graalvm.service.IB");
        System.out.println(c2);
        String bs = args[1];
        Class[] array = new Class[]{Class.forName(as),Class.forName(bs)};
        IA a = (IA) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), array, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return method.invoke(ab, args);
            }
        });

        System.out.println(a.a());
    }

}
