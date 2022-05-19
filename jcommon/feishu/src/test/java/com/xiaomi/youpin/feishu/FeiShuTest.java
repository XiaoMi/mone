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

    private static final String GET_TOKEN_URL = "";
    private static final String GET_GROUPS_URL = "";
    private static final String SEND_MSG_URL = "";
    private static final String SEND_BATCH_MSG_URL = "";
    private static final String GET_USER_ID = "";


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
