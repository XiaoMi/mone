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

import java.io.Serializable;
import java.util.List;

/**
 * @author goodjava@qq.com
 * 部署信息
 */
@Data
public class DeployInfo implements Serializable {

    private int portNum;

    private String id;

    private String downloadKey;

    private String name;

    private long utime;

    private long ctime;

    private int state;

    /**
     * 附加信息
     */
    private String attachment;

    /**
     * 部署路径
     */
    private String deployPath;


    private String pid;

    private int step;

    public DeployInfo() {
    }

    public DeployInfo(List<String> containerIds) {
        this.containerIds = containerIds;
    }

    public enum DeployState {
        init,
        create,
        running,
        failure,
        stop,
        nuke
    }


    public enum DockerStep {
        build,
        create,
        stop,
        start,
        finish,
    }

    /**
     * 0 物理机
     * 1 虚拟化
     */
    private int type;

    /**
     * 对外开放端口号列表
     */
    private List<Port> ports;


    private ContainerInfo containerInfo;

    /**
     * 停机保存的需要再次拉起的容器id
     */
    private List<String> containerIds;


}
