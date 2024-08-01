package com.xiaomi.youpin.tesla.ip.bo.robot;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/12/1 13:55
 */
@Data
@Builder
public class AiChatMessage<T extends Object> implements Serializable {

    //这条消息的id
    private String id;

    private int code;

    private int state;

    private long utime;

    private long ctime;

    //角色
    private Role role;

    private String message;

    //数据
    private T data;

    @Builder.Default
    private MessageType type = MessageType.string;

    //返回的音频
    private String sound;

    @Builder.Default
    private Map<String, Object> meta = new HashMap<>();

    //这里就是向chatgpt提问的字符串
    public String toString() {
        //有问题的把问题也append上
        String question = meta.getOrDefault("question", "").toString() + "\n";
        return question + getAnswer();
    }

    private String getAnswer() {
        //单纯的String
        if (type.equals(MessageType.string)) {
            return data.toString();
        }
        //一个列表
        if (type.equals(MessageType.list)) {
            List<ItemData> list = (List<ItemData>) data;
            return list.stream().map(data -> data.getIndex() + ":" + data.getTitle()).collect(Collectors.joining("\n"));
        }
        return data.toString();
    }

}
