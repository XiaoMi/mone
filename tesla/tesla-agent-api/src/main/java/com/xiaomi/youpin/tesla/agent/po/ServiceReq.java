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

import java.util.Map;

/**
 * @author goodjava@qq.com
 */
@Data
public class ServiceReq {

    /**
     * 部署的任务id(biz_id)
     */
    private long id;

    private String cmd;

    /**
     * 如果是golang 就是 binName
     */
    private String jarName;

    private String downloadKey;

    /**
     * 下载地址(使用可打断的http client 进行下载)
     */
    private String downloadUrl;

    private String servicePath;

    private Map<String, String> attachments;


    /**
     * health check
     */
    private String ip;

    private String healthPath;

    private String regAddress;

    private String type;

    /**
     * 堆内存大小
     */
    private String heapSize;


    /**
     * 语言 java golang
     */
    private String language;

    private String userRight;

    /**
     * 虚拟机参数
     */
    private String jvmParams;
}
