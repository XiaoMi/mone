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

import net.bytebuddy.implementation.bind.annotation.Super;

/**
 * @Author goodjava@qq.com
 * @Date 2021/7/20 09:56
 */
public class MyInterceptor {

    public static int log(int a, int b, @Super Foo foo) {
        System.out.println("Calling sum");
        try {
            return foo.sum(a, b);
        } finally {
            System.out.println("Returned from sum");
        }
    }


}
