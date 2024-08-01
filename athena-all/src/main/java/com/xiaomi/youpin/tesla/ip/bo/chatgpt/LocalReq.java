package com.xiaomi.youpin.tesla.ip.bo.chatgpt;

import lombok.Data;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/11/28 11:16
 */
@Data
public class LocalReq {

    private List<Message> msgList;

    //需要带的上下文数量
    private int num;

}
