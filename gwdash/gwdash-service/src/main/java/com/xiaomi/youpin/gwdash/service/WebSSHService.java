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

package com.xiaomi.youpin.gwdash.service;

import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * @Description: WebSSH的业务逻辑
 * @Author: zhangjunyi
 * @Date: 2020/4/7
 */
public interface WebSSHService {
    /**
     * @Description: 初始化ssh连接
     * @Param:
     * @return:
     * @Author: zhangjunyi
     * @Date: 2020/4/7
     */
    void initConnection(WebSocketSession session);

    /**
     * @Description: 处理客户段发的数据
     * @Param:
     * @return:
     * @Author: zhangjunyi
     * @Date: 2020/4/7
     */
    void recvHandle(String buffer, WebSocketSession session);

    /**
     * @param buffer
     * @param session
     * @@Description: agenLog 处理用户的输入，
     * 原则上只有第一次的Connect 需要输入，后续的输入会被扔掉，系统日志不需要用户的输入
     */
    void recvAgentLogHanlde(String buffer, WebSocketSession session);

    /**
     * @Description: 数据写回前端 for websocket
     * @Param:
     * @return:
     * @Author: zhangjunyi
     * @Date: 2020/4/7
     */
    void sendMessage(WebSocketSession session, byte[] buffer) throws IOException;

    /**
     * @Description: 关闭连接
     * @Param:
     * @return:
     * @Author: zhangjunyi
     * @Date: 2020/4/7
     */
    void close(WebSocketSession session);

    /**
     * 种ssh key
     *
     * @param address
     */
    void plantSshKey(String address);

    /**
     * 删除种植的key
     *
     * @param address
     */
    void removeSshKey(String address);
}