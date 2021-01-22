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

package com.xiaomi.youpin.tesla.rcurve.proxy.control.timeout;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.xiaomi.youpin.tesla.rcurve.proxy.control.ControlCallable;
import com.xiaomi.youpin.tesla.rcurve.proxy.control.IControl;
import com.xiaomi.youpin.tesla.rcurve.proxy.control.Invoker;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.NamedThreadFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 1/16/21
 */
@Slf4j
public class TimeoutControl implements IControl {

    ThreadPoolExecutor timeLimiterPool = new ThreadPoolExecutor(100, 100, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1000), new NamedThreadFactory("MethodInvoker_Limiter"), (r, executor) -> log.warn("SimpleTimeLimiter invoke rejected"));
    SimpleTimeLimiter timeLimiter = new SimpleTimeLimiter(timeLimiterPool);

    @Override
    public Object call(ControlCallable call, Invoker invoker) {
        try {
            return timeLimiter.callWithTimeout(call, 1000, TimeUnit.MILLISECONDS, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
