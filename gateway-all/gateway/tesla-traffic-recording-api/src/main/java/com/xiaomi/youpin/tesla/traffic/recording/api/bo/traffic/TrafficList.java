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

package com.xiaomi.youpin.tesla.traffic.recording.api.bo.traffic;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TrafficList implements Serializable {


    /**
     * 返回的列表
     */
    private List<Traffic> list;

    /**
     * 当前的页码
     */
    private int page;

    private int pagesize;

    /**
     * 总记录数
     */
    private int total;

}
