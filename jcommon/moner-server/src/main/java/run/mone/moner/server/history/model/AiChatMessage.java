package run.mone.moner.server.history.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Data;

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

    // 返回的聊天消息是否需要被打断
    private boolean needApprove;

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
            if (null == data) {
                return "";
            }
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
