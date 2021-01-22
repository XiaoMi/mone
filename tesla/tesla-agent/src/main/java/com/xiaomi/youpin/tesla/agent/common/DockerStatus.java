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

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;

/**
 * @author goodjava@qq.com
 * @date 2020/9/1
 */
public class DockerStatus {

    public List<String> dockerStats(String name) {
        List<String> list = ProcessUtils.process("/tmp/", "docker stats --no-stream").getValue();
        Optional<String[]> opt = list.stream().filter(it -> it.contains(name)).map(it -> it.split("\\s+")).findAny();
        if (opt.isPresent()) {
            return Lists.newArrayList(opt.get()[2], opt.get()[6]);
        }
        return Lists.newArrayList("0", "0");
    }

}
