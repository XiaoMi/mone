package run.mone.m78.service.bo.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import run.mone.m78.service.bo.BaseMessage;

import java.io.Serializable;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/5/9 10:17
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage extends BaseMessage implements Serializable {

    private String from;

    private String to;

    //如果是音频,或者视频,或者图片这里都是base64
    private String message;

    //0 message 1 image 2 audio
    private int type;

    private Map<String, String> meta;

}
