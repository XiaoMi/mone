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

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/14 21:46
 */
@Component
@Data
@Slf4j
public class ControlChain {

    private List<IControl> list = new ArrayList<>();

    private volatile Invoker lastInvoker;

    public void init() {
        Set<IControl> l = Ioc.ins().getBeans(IControl.class);
        this.list.addAll(l.stream().map(it -> it).sorted((a, b) ->
                a.getClass().getAnnotation(Component.class).order() - b.getClass().getAnnotation(Component.class).order()
        ).collect(Collectors.toList()));

        Invoker last = callable -> {
            log.info("call resource:{}", callable.getResource());
            return callable.call();
        };

        int size = list.size();
        for (int i = 0; i < size; i++) {
            IControl control = list.get(i);
            Invoker next = last;
            last = (callable) -> control.call(callable, next);
        }
        this.lastInvoker = last;
    }


    public Object invoke(ControlCallable callable) {
        return this.lastInvoker.invoke(callable);
    }


}
