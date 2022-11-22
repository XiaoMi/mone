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

package com.xiaomi.youpin.gateway.dispatch.strategy.impl;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.xiaomi.youpin.gateway.dispatch.strategy.DispatcherStrategy;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by zhangzhiyong1 on 2016/9/27.
 */
public class IncrStrategy extends DispatcherStrategy {

    private int poolNum = 10;

    private AtomicInteger i = new AtomicInteger(1);


    @Override
    public void initPool(int poolSize) {
    }

    @Override
    public int poolId() {
        return i.incrementAndGet() % poolNum;
    }

    @Override
    public ListeningExecutorService pool(int index) {
        return null;
    }
}
