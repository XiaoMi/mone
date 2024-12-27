package run.mone.ultraman.state;

import com.xiaomi.youpin.tesla.ip.bo.PromptInfo;
import com.xiaomi.youpin.tesla.ip.common.PromptType;
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
