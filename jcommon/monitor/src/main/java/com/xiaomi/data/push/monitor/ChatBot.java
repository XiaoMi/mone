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