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

package com.xiaomi.youpin.tesla.rcurve.proxy.control.retry;

import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.google.common.base.Predicates;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.tesla.rcurve.proxy.control.ControlCallable;
import com.xiaomi.youpin.tesla.rcurve.proxy.control.IControl;
import com.xiaomi.youpin.tesla.rcurve.proxy.control.Invoker;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/14 17:30
 * 重试控制
 */
@Component(order = 300)
public class RetryControl implements IControl {

    private ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

    public void init() {
        map.put("D1", 2);
    }

    @Override
    public Object call(ControlCallable call, Invoker invoker) {
        if (!this.map.containsKey(call.getResource())) {
            return invoker.invoke(call);
        }
        Retryer retryer = RetryerBuilder.newBuilder()
                .retryIfResult(Predicates.isNull())
                .retryIfExceptionOfType(Exception.class)
                .retryIfRuntimeException()
                .withStopStrategy(StopStrategies.stopAfterAttempt(map.get(call.getResource())))
                .build();
        try {
            return retryer.call(() -> invoker.invoke(call));
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }

    }

}
