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

package com.xiaomi.youpin.docker;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author goodjava@qq.com
 * <p>
 * container 使用的信息
 */
@Data
@Builder
public class UseInfo {

    /**
     * 使用中的cpu数量
     */
    private int useCpuNum;


    /**
     * 使用中的内存数量
     */
    private long useMemNum;

    /**
     * 安装的应用
     */
    private Set<String> apps;


    /**
     * 安装的应用的详细信息
     */
    private List<AppInfo> appInfos;

}
