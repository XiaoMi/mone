package com.xiaomi.youpin.gwdash.service;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.youpin.feishu.FeiShu;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class FeiShuService {

    private FeiShu feiShu;

    @NacosValue("${feishu.appId}")
    private String appId;

    @NacosValue("${feishu.appSecret}")
    private String appSecret;

    @Value("${feishu.chat.id}")
    private String chatId;

    @Value("${feishu.chat.audit.id}")
    private String auditChatId;

    @Value("${feishu.platform.name}")
    private String platformName;

    @PostConstruct
    private void init() {
        feiShu = new FeiShu(appId, appSecret);
    }

    public boolean sendMsg(String username, String msg) {
        return sendMsg(username, msg, chatId);
    }

    public boolean sendMsg(String username, String msg, String chatIdParam) {
        try {
            StringBuffer newMsg = new StringBuffer();
            if (StringUtils.isNotEmpty(username)) {
                String userId = feiShu.getUserIdByEmail(username + "@xx.com");
                log.info("FeiShuService#sendMsg userId: {}", userId);
                if (StringUtils.isNotEmpty(userId)) {
                    newMsg.append("<at user_id=\"" + userId + "\"></at>\n");
                }
            }
            newMsg.append(msg);
            newMsg.append("\n米效环境: " + platformName);
            log.info("FeiShuService#sendMsg msg: {}", newMsg.toString());
            return feiShu.sendMsgByChatId(chatIdParam, newMsg.toString());
        } catch (Throwable e) {
            log.error("FeiShuService#sendMsg Throwable" + e.getMessage(), e);
            return false;
        }
    }

    public boolean sendCard(String content) {
        return sendCard(content, chatId);
    }

    public boolean sendAuditCard(String content) {
        return sendCard(content, auditChatId);
    }

    public boolean sendCard(String content, String chatIdParam) {
        try {
            log.info("FeiShuService#sendMsg msg: {}", content);
            return feiShu.sendCardByChatId(chatIdParam, content);
        } catch (Throwable e) {
            log.error("FeiShuService#sendMsg Throwable" + e.getMessage(), e);
            return false;
        }
    }

    public void sendMsg2Person(String username, String msg) {
        if (StringUtils.isEmpty(username)) {
            log.error("username is null");
            return;
        }
        try {
            log.info("FeiShuService#sendMsg personal msg: {}", msg);
            feiShu.sendMsgByEmail(username + "@xx.com", msg);
        } catch (Exception e) {
            log.error("FeiShuService#send personal msg Throwable" + e.getMessage(), e);
        }
    }

    public void sendCard2Person(String username, String card) {
        if (StringUtils.isEmpty(username)) {
            log.error("username is null");
            return;
        }
        try {
            log.info("FeiShuService#sendCard2Person personal msg: {}", card);
            feiShu.sendCardByEmail(username + "@xx.com", card);
        } catch (Exception e) {
            log.error("FeiShuService#send personal msg Throwable" + e.getMessage(), e);
        }
    }

}
