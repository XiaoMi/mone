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

package com.xiaomi.youpin.tesla.agent.po;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author goodjava@qq.com
 */
@Data
public class NginxReq {

    private String cmd;

    /**
     * 配置路径
     */
    private String configPath;

    /**
     * 配置内容
     */
    private String configStr;


    private String upstreamName;


    /**
     * 需要添加的服务器列表
     */
    private List<String> addServerList = new ArrayList<>();

    /**
     * 需要删除的服务器列表
     */
    private List<String> removeServerList = new ArrayList<>();

    /**
     * 最终的服务器列表
     */
    private List<String> serverList = new ArrayList<>();

    /**
     * 是否需要从新加载
     */
    private boolean needReload;

    /**
     * 修改配置
     */
    public static final String modifyConfig = "modifyConfig";

    /**
     * 从新加载配置
     */
    public static final String reload = "reload";

    /**
     * 停止nginx
     */
    public static final String stop = "stop";

    /**
     * 启动nginx
     */
    public static final String start = "start";

    /**
     * 获取服务信息  包括是否启动nginx  指定upstreamName 下的 ip:port 列表
     */
    public static final String info = "info";


}
