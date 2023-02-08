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

package com.xiaomi.mone.buddy.agent.common;

import java.util.concurrent.Callable;

/**
 * @Author goodjava@qq.com
 * @Date 2021/8/3 10:35
 */
public class CallableWrapper<V> implements Callable<V> {

    private final Callable<V> callable;

    public CallableWrapper(Callable<V> callable) {
        this.callable = callable;
    }

    @Override
    public V call() throws Exception {
        System.out.println("callable begin");
        V res = callable.call();
        System.out.println("callable end");
        return res;
    }
}
