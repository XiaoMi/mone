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

package com.xiaomi.mone.buddy.agent;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

/**
 * @author goodjava@qq.com
 * @date 7/27/21
 */
public class CodeInterceptor {

//    @RuntimeType
//    public static Object intercept(@This Object object, @Origin Method method, @SuperCall Callable<?> callable, @AllArguments Object[] arguments) throws Exception {
//        System.out.println("method:"+method.getName());
//        Object res = callable.call();
//        if (res instanceof String) {
//            res = res + "|code";
//        }
//        return res;
//    }


//    @RuntimeType
//    public static Object intercept(@SuperCall Callable<?> callable) throws Exception {
//        System.out.println("code");
//        return callable.call();
//    }
//
//    @RuntimeType
//    public static Object intercept(@Argument(0) String str,@SuperCall Callable<?> callable) throws Exception {
//        str = "zty";
//        System.out.println("code");
//        return callable.call();
//    }

    /**
     * 可以修改参数
     * @param m
     * @param arguments
     * @return
     * @throws Exception
     */
//    @RuntimeType
//    public String intercept(@Morph IDemoService m,@AllArguments Object[] arguments)throws Exception{
//        System.out.println("code2");
//        return m.test("new");
//    }

    /**
     * 构造方法
     *
     * @param obj
     * @param allArguments
     */
//    @RuntimeType
//    public static void intercept(@This Object obj, @AllArguments Object[] allArguments) {
//        System.out.println("after constructor:"+ Arrays.toString(allArguments));
//    }


//    @RuntimeType
//    public static String intercept(String m){
//        System.out.println("code2");
//        return "googoo";
//    }


}
