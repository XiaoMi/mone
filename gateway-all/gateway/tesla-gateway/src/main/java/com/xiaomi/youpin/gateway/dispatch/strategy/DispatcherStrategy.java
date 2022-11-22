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

package com.xiaomi.youpin.gateway.dispatch.strategy;

import com.google.common.util.concurrent.ListeningExecutorService;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by zhangzhiyong1 on 2016/9/26.
 */
public abstract class DispatcherStrategy {

    protected ThreadPoolExecutor pool;

    public abstract void initPool(int poolSize);

    public abstract int poolId();

    public abstract ListeningExecutorService pool(int index);

}
