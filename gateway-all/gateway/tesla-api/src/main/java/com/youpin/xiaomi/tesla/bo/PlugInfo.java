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

package com.youpin.xiaomi.tesla.bo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 */
@Data
@ToString(exclude = {"data"})
public class PlugInfo implements Serializable {

    /**
     * 插件id
     */
    private String pluginId;
    /**
     * 插件名称
     */
    private String name;

    /**
     * 数据id(插件数据)
     */
    private int dataId;

    /**
     * 插件的数据
     */
    private byte[] data;


    /**
     * 插件版本
     */
    private String version;
    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 路由
     */
    private String url;
    /**
     * datasource 的id
     */
    private String dsIds;

}
