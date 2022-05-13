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

package com.xiaomi.youpin.docean.plugin.test;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.xiaomi.youpin.docean.plugin.test.bo.TEvent;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author goodjava@qq.com
 * @date 2020/7/4
 */
public class CommonTest {

    @Subscribe
    public void onMessageEvent(TEvent event){
        System.out.println(event);
    }

    @Test
    public void testEventBus() {
        EventBus eb = new EventBus();
        eb.register(this);
        eb.post(new TEvent());
    }


    @Test
    public void testTime() {
        LocalDateTime rightNow = LocalDateTime.now();
        String date = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(rightNow);
        System.out.println(date);
    }

    class C {
        public void m() {
            System.out.println("m");
        }
    }

    @Test
    public void testCglib() {

    }

    public class TestClass {
        private int num;

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }
    }

    @Test
    public void testReflect() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        long now;
        long sum = 0;
        TestClass t = new TestClass();
        long count = 100000000;
        now = System.nanoTime();

        for (int i = 0; i < count; ++i) {
            t.setNum(i);
            sum += t.getNum();
        }

        System.out.println("get-set耗时" + ((System.nanoTime() - now) / 1000000) + "ms，和是" + sum);

        sum = 0;
        now = System.nanoTime();

        for (int i = 0; i < count; ++i) {
            Class<?> c = TestClass.class;
            Class<?>[] argsType = new Class[1];
            argsType[0] = int.class;
            Method m = c.getMethod("setNum", argsType);
            m.invoke(t, i);
            sum += t.getNum();
        }
        System.out.println("标准反射耗时" + ((System.nanoTime() - now) / 1000000) + "ms，和是" + sum);

        sum = 0;

        Class<?> c = TestClass.class;
        Class<?>[] argsType = new Class[1];
        argsType[0] = int.class;
        Method m = c.getMethod("setNum", argsType);

        now = System.nanoTime();

        for (int i = 0; i < count; ++i) {
            m.invoke(t, i);
            sum += t.getNum();
        }
        System.out.println("缓存反射耗时" + ((System.nanoTime() - now) / 1000000) + "ms，和是" + sum);

        sum = 0;
        FastClass serviceFastClass = FastClass.create(TestClass.class);
        FastMethod serviceFastMethod = serviceFastClass.getMethod("setNum", argsType);
        now = System.nanoTime();

        for (int i = 0; i < count; ++i) {
            serviceFastMethod.invoke(t, new Object[]{i});
            sum += t.getNum();
        }
        System.out.println("cglib反射耗时" + ((System.nanoTime() - now) / 1000000) + "ms，和是" + sum);
    }
}
