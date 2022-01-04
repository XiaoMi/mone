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

package com.xiaomi.youpin.tesla.agent.common;

import com.xiaomi.youpin.docker.YpDockerClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author goodjava@qq.com
 * @Date 2021/5/10 21:03
 */
@Slf4j
public class SubnetCal {

    private AtomicInteger i = new AtomicInteger(20);

    public synchronized String calSubset() {
        String res = "";
        int j = 0;
        Set<String> set = YpDockerClient.ins().listSubnet();
        log.info("subnet set:{}", set);
        do {
            res = "172." + i.getAndIncrement() + ".0.0/16";
            if (i.get() > 170) {
                i.set(19);
            }
        } while (set.contains(res) && j++ < 340);
        log.info("subnet cal res:{}", res);
        return res;
    }


}
