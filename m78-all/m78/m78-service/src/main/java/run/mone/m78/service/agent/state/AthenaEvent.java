package run.mone.m78.service.agent.state;

import com.google.gson.JsonObject;
import lombok.Builder;
import lombok.Data;
import run.mone.m78.service.bo.chatgpt.PromptInfo;
import run.mone.m78.service.bo.chatgpt.PromptType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author goodjava@qq.com
 * @date 2023/12/3 10:30
 */
@Builder
@Data
public class AthenaEvent {

    private PromptInfo promptInfo;

    private PromptType promptType;

    private String content;

    private AnswerType answerType;

    private String project;

    private String from;

    private String to;

    private String role;

    private JsonObject input;

    private Integer multimodal;

    private String mediaType;

    @Builder.Default
    private Map<String,String> meta = new HashMap<>();


    @Builder.Default
    private CountDownLatch askLatch = new CountDownLatch(1);


}
