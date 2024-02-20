package run.mone.m78.ip.bo.chatgpt;

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
}
