package run.mone.m78.service.bo.chatgpt;

import com.google.gson.JsonObject;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/11/24 13:48
 */
@Data
@Builder
public class Message {

    private String id;

    private String content;

    private String role;

    private Map<String, String> params;

    //先放这里吧...很粗糙...
    private String promptName;

    private Integer multimodal;

    private String mediaType;

    /**
     * 人设，prompt的参数
     */
    private Map<String, String> promptParams;

    /**
     * 多模态的附言
     */
    private String postscript;

    //最原始的输入,从最外边一直带进来的
    private JsonObject input;

    //todo 支持pin消息
    private Boolean pinned;
}
