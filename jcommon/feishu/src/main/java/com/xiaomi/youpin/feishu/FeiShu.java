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

package com.xiaomi.youpin.feishu;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.data.push.client.HttpClientV2;
import com.xiaomi.youpin.feishu.bo.*;
import com.xiaomi.youpin.feishu.enums.MsgTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author liuyuchong
 */
@Slf4j
public class FeiShu {

    private String appId;
    private String appSecret;

    private AtomicReference<TokenResult> tr = new AtomicReference<>();

    private String getTokenUrl = "";
    private String getGroupsUrl = "";
    private String sendMsgUrl = "";
    private String sendBatchMsgUrl = "";
    private String getUserIdUrl = "";
    private String cardUpdate = "";
    private String getBatchUserInfoUrl = "";

    public FeiShu(String appId, String appSecret) {
        this.appId = appId;
        this.appSecret = appSecret;
        rereshToken();
    }


    public FeiShu(String appId, String appSecret, String getTokenUrl, String getGroupsUrl, String sendMsgUrl, String sendBatchMsgUrl, String getUserIdUrl) {
        this.appId = appId;
        this.appSecret = appSecret;
        this.getTokenUrl = getTokenUrl;
        this.getGroupsUrl = getGroupsUrl;
        this.sendMsgUrl = sendMsgUrl;
        this.sendBatchMsgUrl = sendBatchMsgUrl;
        this.getUserIdUrl = getUserIdUrl;
        rereshToken();
    }

    private void rereshToken() {
        try {
            TokenResult tokenResult = getTokenResult();
            if (tokenResult == null) {
                log.error("get token failed");
            }
            tokenResult.setUtime(System.currentTimeMillis());
            tr.set(tokenResult);
        } catch (Throwable ex) {
            log.error(ex.getMessage());
        }
    }


    /**
     * 获取token
     * 企业自建应用通过此接口获取 tenant_access_token，调用接口获取企业资源时，需要使用 tenant_access_token 作为授权凭证。
     * Token 有效期为 2 小时，在此期间调用该接口 token 不会改变。当 token 有效期小于 10 分的时候，再次请求获取 token 的时候，会生成一个新的 token，与此同时老的 token 依然有效。
     */
    private String getToken() {
        TokenResult token = this.tr.get();
        long now = System.currentTimeMillis();
        if (token == null || (now - token.getUtime() >= TimeUnit.SECONDS.toMillis(token.getExpire() - 5))) {
            rereshToken();
        }
        return this.tr.get().getTenant_access_token();
    }

    private TokenResult getTokenResult() {
        TokenRequest request = new TokenRequest();
        request.setApp_id(appId);
        request.setApp_secret(appSecret);
        Map headers = new HashMap();
        headers.put("content-type", "application/json");
        String tokenResult = HttpClientV2.post(getTokenUrl, new Gson().toJson(request), headers);
        TokenResult token = null;
        try {
            token = new Gson().fromJson(tokenResult, TokenResult.class);
        } catch (Exception e) {
            log.error("json parse error, msg:{}", e.getMessage());
        }
        return token;
    }

    public List<String> getBatchPhone(List<String> openIdSet) {
        StringBuilder param = new StringBuilder("open_ids=");
        openIdSet.stream().forEach(it -> {
                param.append(it + "&open_ids=");
            }
        );
        String url = getBatchUserInfoUrl + param;
        Map headers = new HashMap();
        headers.put("Authorization", "Bearer " + getToken());
        String strResult = HttpClientV2.get(url, headers, 2000);
        Result<UserInfoWrapperResult> result = null;
        try {
            result = new Gson().fromJson(strResult, new TypeToken<Result<UserInfoWrapperResult>>() {
            }.getType());
        } catch (Exception e) {
            log.error("json parse error, result:{}, msg:{}", strResult, e.getMessage());
        }
        return result.getData().getUser_infos().stream().map(it -> it.getMobile()).collect(Collectors.toList());
    }

    public UserIdInfo getUserIdInfoByEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            log.error("email error");
            return null;
        }

        String url = getUserIdUrl + email;
        Map headers = new HashMap();
        headers.put("Authorization", "Bearer " + getToken());
        String strResult = HttpClientV2.get(url, headers, 2000);
        Result<EmailQueryResult> result = null;
        try {
            result = new Gson().fromJson(strResult, new TypeToken<Result<EmailQueryResult>>() {
            }.getType());
        } catch (Exception e) {
            log.error("json parse error, msg:{}", e.getMessage());
        }
        if (result == null || result.getCode() != 0 || result.getData() == null) {
            return null;
        }
        EmailQueryResult queryResult = result.getData();
        if (CollectionUtils.isEmpty(queryResult.getEmail_users())) {
            return null;
        }
        List<UserIdInfo> userIdInfos = queryResult.getEmail_users().get(email);
        if (CollectionUtils.isEmpty(userIdInfos)) {
            return null;
        }
        return userIdInfos.get(0);
    }

    /**
     * 获取邮箱获取Open_id
     */
    public String getOpenIdIdByEmail(String email) {
        UserIdInfo userIdInfo = getUserIdInfoByEmail(email);
        return userIdInfo == null ? "" : userIdInfo.getOpen_id();
    }

    /**
     * 获取邮箱获取用户id
     */
    public String getUserIdByEmail(String email) {
        UserIdInfo userIdInfo = getUserIdInfoByEmail(email);
        return userIdInfo == null ? "" : userIdInfo.getUser_id();
    }


    /**
     * 获取群列表--带分页
     */
    public Result<GroupPageData> getGroupListByPage(String pageSize, String pageToken) {
        String url = getGroupsUrl;
        boolean isPageSizeExist = false;
        if (!StringUtils.isEmpty(pageSize)) {
            isPageSizeExist = true;
            url = url + "?page_size=" + pageSize;
        }
        if (!StringUtils.isEmpty(pageToken)) {
            url = url + (isPageSizeExist ? "&" : "?") + "page_token=" + pageToken;
        }
        Map headers = new HashMap();
        headers.put("Authorization", "Bearer " + getToken());
        Result<GroupPageData> result = null;
        try {
            String response = HttpClientV2.get(url, headers);
            if (StringUtils.isEmpty(response)) {
                log.error("response is empty");
                return null;
            }
            result = new Gson().fromJson(response, new TypeToken<Result<GroupPageData>>() {
            }.getType());
        } catch (Exception e) {
            log.error("feishu get group list error, msg:{}", e.getMessage());
        }

        return result;
    }

    /**
     * 获取群列表
     */
    public Result<GroupPageData> getGroupList() {
        return getGroupListByPage(null, null);
    }


    /**
     * 给 用户/群 发消息
     *
     * @param userId 向用户发送消息 只需要填 open_id、email、user_id 中的一个即可，
     *               向群里发消息使 用群的 chat_id（可通过获取群列表接口获取）
     * @return
     */
    private boolean sendMsg(String openId, String chatId, String rootId, String userId, String email, MsgTypeEnum type, String content) {
        if (type == null || StringUtils.isEmpty(content)) {
            log.error("type and content can not be null, params: type:{}, content:{}", type, content);
            return false;
        }
        MsgSendRequest request = new MsgSendRequest();
        request.setMsg_type(type.getName());
        if (!StringUtils.isEmpty(openId)) {
            request.setOpen_id(openId);
        }
        if (!StringUtils.isEmpty(chatId)) {
            request.setChat_id(chatId);
        }
        if (!StringUtils.isEmpty(rootId)) {
            request.setRoot_id(rootId);
        }
        if (!StringUtils.isEmpty(userId)) {
            request.setUser_id(userId);
        }
        if (!StringUtils.isEmpty(email)) {
            request.setEmail(email);
        }
        MsgDetail detail = new MsgDetail();
        switch (type) {
            case TEXT:
                detail.setText(content);
                request.setContent(detail);
                break;
            case IMAGE:
                detail.setImage_key(content);
                request.setContent(detail);
                break;
            case CARD:
                request.setCard(GsonFactory.getGson().fromJson(content, Map.class));
                break;
        }
        Map headers = new HashMap();
        headers.put("Authorization", "Bearer " + getToken());
        headers.put("content-type", "application/json");
        Result result = null;
        try {
            String response = HttpClientV2.post(sendMsgUrl, new String(new Gson().toJson(request).getBytes(), StandardCharsets.UTF_8), headers, 2000);
            if (StringUtils.isEmpty(response)) {
                log.error("http post error when send msg, response:{}", response);
                return false;
            }
            result = new Gson().fromJson(response, Result.class);
        } catch (Exception e) {
            log.error("feishu send msg error:{}", e.getMessage());
            return false;
        }
        if (result == null || result.getCode() != 0) {
            return false;
        }
        return true;
    }

    public boolean updateCard(String body) {
        Map headers = new HashMap();
        headers.put("Authorization", "Bearer " + getToken());
        headers.put("content-type", "application/json");
        String response = HttpClientV2.post(cardUpdate, body, headers, 2000);
        return true;
    }


    /**
     * 批量发送消息
     */
    public boolean batchSendMsg(MsgBatchSendRequest batchSendRequest) {
        Map headers = new HashMap();
        headers.put("Authorization", "Bearer " + getToken());
        headers.put("content-type", "application/json");
        Result result = null;
        try {
            String response = HttpClientV2.post(sendBatchMsgUrl, new Gson().toJson(batchSendRequest), headers, 2000);
            if (StringUtils.isEmpty(response)) {
                log.error("http post error when send msg, response:{}", response);
                return false;
            }
            result = new Gson().fromJson(response, Result.class);
        } catch (Exception e) {
            log.error("feishu send msg error:{}", e.getMessage());
            return false;
        }
        if (result == null || result.getCode() != 0) {
            return false;
        }
        return true;
    }

    /**
     * 通过邮件发送信息
     *
     * @param email
     * @param content
     * @return
     */
    public boolean sendMsgByEmail(String email, String content) {
        return sendMsg(null, null, null, null, email, MsgTypeEnum.TEXT, content);
    }

    /**
     * 通过邮件发送信息
     *
     * @param email
     * @param content
     * @return
     */
    public boolean sendCardByEmail(String email, String content) {
        return sendMsg(null, null, null, null, email, MsgTypeEnum.CARD, content);
    }

    /**
     * 通过飞书chatId或者飞书群chatId发消息
     *
     * @param chatId
     * @param content
     * @return
     */
    public boolean sendMsgByChatId(String chatId, String content) {
        return sendMsg(null, chatId, null, null, null, MsgTypeEnum.TEXT, content);
    }

    /**
     * 通过飞书chatId或者飞书群chatId发消息卡片
     *
     * @param chatId
     * @param card
     * @return
     */
    public boolean sendCardByChatId(String chatId, String card) {
        return sendMsg(null, chatId, null, null, null, MsgTypeEnum.CARD, card);
    }

    /**
     * 通过飞书userId发消息
     *
     * @param userId
     * @param content
     * @return
     */
    public boolean sendMsgByUserId(String userId, String content) {
        return sendMsg(null, null, null, userId, null, MsgTypeEnum.TEXT, content);
    }

    /**
     * 通过飞书userId发消息
     *
     * @param userId
     * @param card
     * @return
     */
    public boolean sendCardByUserId(String userId, String card) {
        return sendMsg(null, null, null, userId, null, MsgTypeEnum.CARD, card);
    }

    /**
     * 通过openId发消息
     *
     * @param openId
     * @param content
     * @return
     */
    public boolean sendMsgByOpenId(String openId, String content) {
        return sendMsg(openId, null, null, null, null, MsgTypeEnum.TEXT, content);
    }

    /**
     * 通过openId发消息
     *
     * @param openId
     * @param card
     * @return
     */
    public boolean sendCardByOpenId(String openId, String card) {
        return sendMsg(openId, null, null, null, null, MsgTypeEnum.CARD, card);
    }
}
