package run.mone.ultraman.state;

import run.mone.m78.ip.bo.PromptInfo;
import run.mone.m78.ip.common.PromptType;
import lombok.Builder;
import lombok.Data;

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

    private String answer;

    private AnswerType answerType;

    private String project;

    @Builder.Default
    private Map<String,String> meta = new HashMap<>();


    @Builder.Default
    private CountDownLatch askLatch = new CountDownLatch(1);


}
