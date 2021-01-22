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

package com.xiaomi.youpin.gwdash.dao.model;

import lombok.Data;

/**
 * @author gaoyibo
 * @create 2019-04-30 00:43
 */
@Data
public class Plugin {
    private Integer id;
    private String name;
    private long ctime;
    private long utime;
    private String creator;
    private Integer status;
    /**
     * 路由
     */
    private String url;

    /**
     * jar 数据的id
     */
    private int dataId;
    /**
     * jar 数据的version
     */
    private String dataVersion;
}
