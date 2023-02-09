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

import com.xiaomi.data.push.nacos.NacosConfig;
import com.xiaomi.youpin.feishu.bo.ContentBo;
import com.xiaomi.youpin.feishu.bo.GroupPageData;
import com.xiaomi.youpin.feishu.bo.MsgBatchSendRequest;
import com.xiaomi.youpin.feishu.bo.Result;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FeiShuTest {
    private FeiShu feiShu;
    String appId = "";
    String appSecret = "";
    private static final String GET_TOKEN_URL = "";
    private static final String GET_GROUPS_URL = "";
    private static final String SEND_MSG_URL = "";
    private static final String SEND_BATCH_MSG_URL = "";
    private static final String GET_USER_ID = "";


    @Before
    public void init() {
        NacosConfig config = new NacosConfig();
        config.setDataId("zzy_new");
        config.init();
        appId = config.getConfig("feishu_appid");
        appSecret = config.getConfig("58eASBLGw9IqBFyds5m93m4GmTDYBMMt");
        feiShu = new FeiShu(appId, appSecret);
    }

    @Test
    public void getGroups() {
        Result<GroupPageData> groupList = feiShu.getGroupList();
        System.out.println(groupList);
    }

    @Test
    public void sendMsg(){
        String chatId = "";
        feiShu.sendMsgByChatId(chatId,"<at user_id=\"d3ceea95\"></at>   吃饭了吗");
    }

    @Test
    public void batchSendMsg() {
        MsgBatchSendRequest request = new MsgBatchSendRequest();
        ContentBo content = new ContentBo();
        content.setText("test");
        request.setContent(content);
        List<String> users = new ArrayList<>();
        users.add(feiShu.getUserIdByEmail(""));
        users.add(feiShu.getUserIdByEmail(""));
        request.setUser_ids(users);
        feiShu.batchSendMsg(request);
    }
    @Test
    public void getPhone(){
        List<String> users = new ArrayList<>();
        users.add(feiShu.getOpenIdIdByEmail(""));
        users.add(feiShu.getOpenIdIdByEmail(""));
        System.out.println(feiShu.getBatchPhone(users));
    }



    @Test
    public void sendMsgByOpenId(){
        String openId = "";
        feiShu.sendMsgByOpenId(openId, "test open id msg 1");
    }

    @Test
    public void sendMsgByUserId(){
        String userId = "";
        feiShu.sendMsgByUserId(userId, "test user id msg 1");
    }

    @Test
    public void getGroupMembersByPage(){
        System.out.println(feiShu.getUserIdByEmail("xxxxxxxx@xiaomi.com"));
    }
}
