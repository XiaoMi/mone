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

package com.xiaomi.mone.buddy.test;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import org.junit.Test;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.returns;


/**
 * @Author goodjava@qq.com
 * @Date 2021/7/16 17:09
 */
public class BuddyTest {


    @Test
    public void test1() {
        ByteBuddyAgent.install();
        new ByteBuddy()
                .redefine(Foo.class)
                .method(named("sayHelloFoo"))
                .intercept(FixedValue.value("Hello Foo Redefined"))
                .make()
                .load(
                        Foo.class.getClassLoader(),
                        ClassReloadingStrategy.fromInstalledAgent());

        Foo f = new Foo();
        System.out.println(f.sayHelloFoo());
    }


    @Test
    public void test2() {
        ByteBuddyAgent.install();
        Foo f = new Foo();
        new ByteBuddy()
                .redefine(Foo.class)
                .method(named("sayHelloFoo")
                        .and(isDeclaredBy(Foo.class)
                                .and(returns(String.class))))
                .intercept(MethodDelegation.to(Bar.class))
                .make()
                .load(
                        Foo.class.getClassLoader(),
                        ClassReloadingStrategy.fromInstalledAgent());

        System.out.println(f.sayHelloFoo());
    }


    @Test
    public void test3() {
        ByteBuddyAgent.install();
        Foo f = new Foo();
        new ByteBuddy()
                .redefine(Foo.class)
                .method(named("sum"))
                .intercept(MethodDelegation.to(Bar.class))
                .make()
                .load(
                        Foo.class.getClassLoader(),
                        ClassReloadingStrategy.fromInstalledAgent());

        System.out.println(f.sum(100, 200));
    }


    @Test
    public void test4() throws IllegalAccessException, InstantiationException {
        ByteBuddyAgent.install();
        Foo foo= new ByteBuddy()
                .subclass(Foo.class)
                .method(named("sum")).intercept(MethodDelegation.to(MyInterceptor.class))
                .make()
                .load(getClass().getClassLoader())
                .getLoaded()
                .newInstance();
        System.out.println(foo.sum(1,2));
    }

    @Test(expected = Throwable.class)
    public void test5() throws IllegalAccessException, InstantiationException {
        ByteBuddyAgent.install();
        new ByteBuddy()
                .redefine(Foo.class)
                .method(named("sum")).intercept(MethodDelegation.to(MyInterceptor.class))
                .make()
                .load(Foo.class.getClassLoader(),ClassReloadingStrategy.fromInstalledAgent());
        Foo foo = new Foo();
        System.out.println(foo.sum(1,2));
    }


}

