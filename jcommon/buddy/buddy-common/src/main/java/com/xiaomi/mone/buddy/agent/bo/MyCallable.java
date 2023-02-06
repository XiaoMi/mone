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

package com.xiaomi.mone.buddy.agent.bo;

import java.util.concurrent.Callable;

/**
 * @Author goodjava@qq.com
 * @Date 2021/8/7 23:37
 */
public class MyCallable<V> implements Callable<V> {

    private final Callable<V> c;

    private Span span;

    public MyCallable(Callable<V> c) {
        this.c = c;
        this.span = Context.getContext().getSpan();
    }

    @Override
    public V call() throws Exception {
        Context.getContext().setSpan(this.span);
        return c.call();
    }
}
