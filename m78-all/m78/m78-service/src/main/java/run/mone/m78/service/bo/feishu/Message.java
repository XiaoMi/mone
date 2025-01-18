package run.mone.m78.service.bo.feishu;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * @author caobaoyu
 * @description:
 * @date 2024-07-29 23:47
 */
@Data
public class Message implements Serializable {
    @SerializedName("message_id")
    public String messageId;
    @SerializedName("root_id")
    public String rootId;
    @SerializedName("parent_id")
    public String parentId;
    @SerializedName("create_time")
    public String createTime;
    @SerializedName("update_time")
    public String updateTime;
    @SerializedName("chat_id")
    public String chatId;
    @SerializedName("chat_type")
    public String chatType;
    @SerializedName("message_type")
    public String messageType;
    @SerializedName("content")
    public String content;
    @SerializedName("mentions")
    public List<Mention> mentions;
    @SerializedName("user_agent")
    public String userAgent;
}

