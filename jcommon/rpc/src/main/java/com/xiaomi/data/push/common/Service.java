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

package com.xiaomi.data.push.common;

import com.xiaomi.data.push.rpc.protocol.RemotingCommand;

/**
 *
 * @author zhangzhiyong
 * @date 15/06/2018
 */
public interface Service {

    /**
     * 服务初始化
     */
    default void init() {

    }

    /**
     * 服务启动
     */
    default void start() {

    }

    /**
     * 服务停止
     */
    default void stop() {

    }

    /**
     * 服务关闭(kill)
     */
    default void shutdown() {

    }

    /**
     * 定期执行
     */
    default void schedule() {

    }

    default RemotingCommand call(RemotingCommand req) {
        return null;
    }
}
