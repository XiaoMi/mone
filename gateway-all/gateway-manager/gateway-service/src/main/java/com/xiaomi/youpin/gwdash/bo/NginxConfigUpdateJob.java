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

package com.xiaomi.youpin.gwdash.bo;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author Zheng Xu zheng.xucn@outlook.com
 */
public class NginxConfigUpdateJob implements Delayed {
    private long nginxId;
    private long time;
    private List<String> removeIpList;

    public NginxConfigUpdateJob(long nginxId, long delaySeconds, List<String> removeIpList) {
        this.nginxId = nginxId;
        long currentTime = System.currentTimeMillis();
        if (delaySeconds < 0) {
            delaySeconds = 0;
        }
        this.time = currentTime + delaySeconds * 1000;
        this.removeIpList = removeIpList;
    }

    public long getNginxId() {
        return this.nginxId;
    }

    public List<String> getRemoveIpList() {
        return this.removeIpList;
    }

    @Override
    public long getDelay(@NotNull TimeUnit timeUnit) {
        return timeUnit.convert(time - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(@NotNull Delayed delayed) {
        if (this.time > ((NginxConfigUpdateJob) delayed).time) {
            return 1;
        }
        if (this.time < ((NginxConfigUpdateJob) delayed).time) {
            return -1;
        }
        return 0;
    }
}
