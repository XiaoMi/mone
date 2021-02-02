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

package com.xiaomi.data.push.monitor;

import com.xiaomi.data.push.monitor.model.ChatGroupMessageRequest;
import com.xiaomi.data.push.monitor.model.ChatGroupMessageResponse;
import com.xiaomi.data.push.monitor.model.CreateChatGroupRequest;
import com.xiaomi.data.push.monitor.model.CreateChatGroupResponse;

/**
 * @author maojinrui
 */
public interface ChatBot {

    /**
     * 创建群聊会话
     */
    CreateChatGroupResponse createChatGroup(CreateChatGroupRequest request);

    /**
     * 向群聊会话发送推送
     */
    ChatGroupMessageResponse sendMessageToChatGroup(ChatGroupMessageRequest request);
}