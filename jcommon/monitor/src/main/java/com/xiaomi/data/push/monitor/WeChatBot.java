package com.xiaomi.data.push.monitor;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.data.push.client.HttpClientV2;
import com.xiaomi.data.push.monitor.model.ChatGroupMessageRequest;
import com.xiaomi.data.push.monitor.model.ChatGroupMessageResponse;
import com.xiaomi.data.push.monitor.model.CreateChatGroupRequest;
import com.xiaomi.data.push.monitor.model.CreateChatGroupResponse;
import com.xiaomi.data.push.monitor.model.GetAccessTokenResponse;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

/**
 * @author maojinrui
 */
@Slf4j
public class WeChatBot implements ChatBot {
    private final static Long AUTHORIZE_INTERVAL = 5400L;
    private String corpId;
    private String corpSecret;
    private String accessToken;
    private Gson gson = new Gson();

    private Long lastAuthorizeTime = 0L;

    public WeChatBot(String corpId, String corpSecret) {
        this.corpId = corpId;
        this.corpSecret = corpSecret;
        authorize();
    }

    private void authorize() {
        // 如果距离上次认证超过90分钟, 重新认证
        // 否则无需重新认证
        Long now = Instant.now().getEpochSecond();
        if (now - lastAuthorizeTime > AUTHORIZE_INTERVAL) {
            String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s", this.corpId, this.corpSecret);
            String result = HttpClientV2.get(url, Maps.newHashMap());
            GetAccessTokenResponse response = gson.fromJson(result, GetAccessTokenResponse.class);
            if (response.getErrcode() == 0) {
                this.accessToken = response.getAccess_token();
                this.lastAuthorizeTime = now;
            }
        }
    }

    @Override
    public CreateChatGroupResponse createChatGroup(CreateChatGroupRequest request) {
        authorize();
        String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/appchat/create?access_token=%s", this.accessToken);
        String body = gson.toJson(request);
        String result = HttpClientV2.post(url, body, Maps.newHashMap());
        CreateChatGroupResponse response = gson.fromJson(result, CreateChatGroupResponse.class);
        return response;
    }

    @Override
    public ChatGroupMessageResponse sendMessageToChatGroup(ChatGroupMessageRequest request) {
        authorize();
        String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/appchat/send?access_token=%s", this.accessToken);
        String body = gson.toJson(request);
        String result = HttpClientV2.post(url, body, Maps.newHashMap());
        ChatGroupMessageResponse response = gson.fromJson(result, ChatGroupMessageResponse.class);
        return response;
    }
}
