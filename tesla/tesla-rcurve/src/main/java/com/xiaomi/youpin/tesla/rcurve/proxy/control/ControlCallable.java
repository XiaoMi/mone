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

package com.xiaomi.youpin.tesla.rcurve.proxy.control;

import lombok.Getter;

import java.util.concurrent.Callable;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/14 21:53
 */
public abstract class ControlCallable<T> implements Callable<T> {

    /**
     * 资源名称(涉及到流控)
     */
    @Getter
    private String resource;

    public ControlCallable(String resource) {
        this.resource = resource;
    }

    @Override
    public abstract T call();
}
