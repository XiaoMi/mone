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

    private static final String GET_TOKEN_URL = "https://open.f.mioffice.cn/open-apis/auth/v3/tenant_access_token/internal/";
    private static final String GET_GROUPS_URL = "https://open.f.mioffice.cn/open-apis/chat/v4/list";
    private static final String SEND_MSG_URL = "https://open.f.mioffice.cn/open-apis/message/v4/send/";
    private static final String SEND_BATCH_MSG_URL = "https://open.f.mioffice.cn/open-apis/message/v4/batch_send/";
    private static final String GET_USER_ID = "https://open.f.mioffice.cn/open-apis/user/v1/batch_get_id?emails=";


    @Before
    public void init() {
        feiShu = new FeiShu("", "", GET_TOKEN_URL, GET_GROUPS_URL, SEND_MSG_URL, SEND_BATCH_MSG_URL, GET_USER_ID);
    }

    @Test
    public void getGroups() {
        Result<GroupPageData> groupList = feiShu.getGroupList();
        System.out.println(groupList);
    }

    @Test
    public void sendMsg(){
//        feiShu.sendMsgByEmail("liuyuchong@xxxx.com","@liuyuchong");
//        feiShu.sendMsgByChatId("oc_3646758afc45327e7b906e8b4cc1891e","msg1  <at user_id=\"1cd5cg8c\">  test at msg </at>   msg2");
        feiShu.sendMsgByChatId("oc_3646758afc45327e7b906e8b4cc1891e","<at user_id=\"d3ceea95\"></at>   吃饭了吗");
    }

    @Test
    public void batchSendMsg() {
        MsgBatchSendRequest request = new MsgBatchSendRequest();
        ContentBo content = new ContentBo();
        content.setText("test");
        request.setContent(content);
        List<String> users = new ArrayList<>();
        users.add(feiShu.getUserIdByEmail("liuyuchong@xxxx.com"));
        users.add(feiShu.getUserIdByEmail("zhangxiuhua@xxxx.com"));
        request.setUser_ids(users);
        feiShu.batchSendMsg(request);
    }

    @Test
    public void sendMsgByOpenId(){
        feiShu.sendMsgByOpenId("ou_2b58f952b9b226d8c69bd437a2cfbf87", "test open id msg 1");
    }

    @Test
    public void sendMsgByUserId(){
        feiShu.sendMsgByUserId("1cd5cg8c", "test user id msg 1");
    }

    @Test
    public void getGroupMembersByPage(){
        System.out.println(feiShu.getUserIdByEmail("z111hangxiuhua@xxxx.com"));
    }
}
