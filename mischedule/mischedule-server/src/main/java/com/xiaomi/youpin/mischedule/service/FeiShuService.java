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

package com.xiaomi.youpin.mischedule.service;

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

    @Value("${feishu.appId}")
    private String appId;

    @Value("${feishu.appSecret}")
    private String appSecret;

    @Value("${feishu.chat.id}")
    private String chatId;

    @Value("${feishu.platform.name}")
    private String platformName;

    @PostConstruct
    private void init(){
        feiShu = new FeiShu(appId, appSecret);
    }

    public boolean sendMsg(String username, String msg) {
        try {
            StringBuffer newMsg = new StringBuffer();
            if (StringUtils.isNotEmpty(username)) {
                String userId = feiShu.getUserIdByEmail(username + "@xxxxx.com");
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

}
