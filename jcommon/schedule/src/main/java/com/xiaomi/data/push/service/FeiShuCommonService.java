package com.xiaomi.data.push.service;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.youpin.feishu.FeiShu;
import com.xiaomi.youpin.feishu.bo.ContentBo;
import com.xiaomi.youpin.feishu.bo.MsgBatchSendRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class FeiShuCommonService {

    private FeiShu feiShu;

    @NacosValue("${feishu.appId}")
    private String appId;

    @NacosValue("${feishu.appSecret}")
    private String appSecret;

    @Value("${feishu.chat.id}")
    private String chatId;

    @Value("${feishu.platform.name}")
    private String platformName;

    @PostConstruct
    private void init() {
        feiShu = new FeiShu(appId, appSecret);
    }

    public boolean sendMsg(String username, String msg) {
        try {
            StringBuffer newMsg = new StringBuffer();
            if (StringUtils.isNotEmpty(username)) {
                String userId = feiShu.getUserIdByEmail(username + "@xiaomi.com");
                log.info("FeiShuService#sendMsg userId: {}", userId);
                if (StringUtils.isNotEmpty(userId)) {
                    newMsg.append("<at user_id=\"" + userId + "\"></at>\n");
                }
            }
            newMsg.append(msg);
            newMsg.append("\n米效环境: " + platformName);
            log.info("FeiShuService#sendMsg msg: {}", newMsg.toString());
            return feiShu.sendMsgByChatId(chatId, newMsg.toString());
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
            feiShu.sendMsgByEmail(username + "@xiaomi.com", msg);
        } catch (Exception e) {
            log.error("FeiShuService#send personal msg Throwable" + e.getMessage(), e);
        }
    }

    public boolean batchSendMsg(String alarmUsername, String msg) {
        boolean flag = false;
        MsgBatchSendRequest request = new MsgBatchSendRequest();
        ContentBo content = new ContentBo();
        content.setText(msg);
        request.setContent(content);
        List<String> users = new ArrayList<>();

        Arrays.asList(alarmUsername.split(",")).stream().distinct().forEach(userName -> {
            String openIdId = feiShu.getOpenIdIdByEmail(userName + "@xiaomi.com");
            if (StringUtils.isNotBlank(openIdId)){
                users.add(openIdId);
            } else {
                log.info("FeiShuCommonService.batchSendMsg 根据userName:{}未查询到openIdId", userName);
            }
        });
        if (users.size()>0){
            request.setOpen_ids(users);
            flag = feiShu.batchSendMsg(request);
        }
        log.info("FeiShuCommonService.batchSendMsg alarmUsername:{}, msg:{} users.size:{} rst:{}", alarmUsername, msg, users.size(), flag);
        return flag;
    }

}
