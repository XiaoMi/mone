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

package com.xiaomi.youpin.jmonitor;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author gaoyibo
 */
@Data
public class MonitorInfo {

    @Getter
    @Setter
    private String version;

    @Getter
    @Setter
    private long time;


    /**
     * 可使用内存
     **/
    private long totalMemory;

    /**
     * 剩余内存
     **/
    private long freeMemory;

    /**
     * 最大可使用内存
     **/
    private long maxMemory;

    /**
     * 操作系统
     **/
    private String osName;

    /**
     * 总的物理内存
     **/
    private long totalMemorySize;

    /**
     * 剩余的物理内存
     **/
    private long freePhysicalMemorySize;

    /**
     * 已使用的物理内存
     **/
    private long usedMemory;

    /**
     * 线程总数
     **/
    private int totalThread;

    /**
     * cpu使用率
     **/
    private double cpuRatio;

    private NetworkInfo networkInfo;

    public long getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public long getFreePhysicalMemorySize() {
        return freePhysicalMemorySize;
    }

    public void setFreePhysicalMemorySize(long freePhysicalMemorySize) {
        this.freePhysicalMemorySize = freePhysicalMemorySize;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(long maxMemory) {
        this.maxMemory = maxMemory;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(long totalMemory) {
        this.totalMemory = totalMemory;
    }

    public long getTotalMemorySize() {
        return totalMemorySize;
    }

    public void setTotalMemorySize(long totalMemorySize) {
        this.totalMemorySize = totalMemorySize;
    }

    public int getTotalThread() {
        return totalThread;
    }

    public void setTotalThread(int totalThread) {
        this.totalThread = totalThread;
    }

    public long getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(long usedMemory) {
        this.usedMemory = usedMemory;
    }

    public double getCpuRatio() {
        return cpuRatio;
    }

    public void setCpuRatio(double cpuRatio) {
        this.cpuRatio = cpuRatio;
    }
}

