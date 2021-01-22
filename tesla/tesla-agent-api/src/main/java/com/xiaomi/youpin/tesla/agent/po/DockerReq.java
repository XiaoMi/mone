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
public class DockerReq {

    private long id;

    /**
     * 项目id
     */
    private Long projectId;

    /**
     * 环境id
     */
    private Long envId;

    private String cmd;

    private String containerId;

    private String dockerFile;

    private String imageName;

    private String containerName;

    private String network;

    private Map<String, String> attachments;

    private String fileUrl;

    /**
     * java
     * golang
     */
    private String language;


    /**
     * 下载文件的key
     */
    private String downloadKey;

    /**
     * jar包名称
     */
    private String jarName;

    /**
     * 服务的路径
     */
    private String servicePath;

    /**
     * 日志的路径
     */
    private String logPath;


    /**
     * 堆内存大小
     */
    private String heapSize;

    /**
     * 暴露的端口号
     */
    private String exposePort;


    private String ip;

    private String healthPath;

    private String regAddress;

    private String type;


    private boolean flow;


    private String cpu;
    private Long mem;
    private Integer blkioWeight;

    /**
     * docker file 文件内容
     */
    private String dockerFileContent;

    /**
     * example dubbo_port=34444,http_port=8080,python=true
     */
    private String labels;

    /**
     * 是否采用直接拉取的方式
     */
    private boolean pull;

    private int memLimit;

    private int maxDirectMemorySize;

    private int mms;

    /**
     * 虚拟机参数
     */
    private String jvmParams;

}
