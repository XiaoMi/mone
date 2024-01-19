package run.mone.m78.ip.common;

import com.google.gson.Gson;
import run.mone.m78.ip.bo.AiMessage;
import run.mone.m78.ip.bo.MessageConsumer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 2023/7/5 09:49
 */
@Slf4j
public class AthenaMessageConsumer extends MessageConsumer {

    private Gson gson = new Gson();

    private String projectName;

    public AthenaMessageConsumer(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public void begin(AiMessage message) {
        String str = gson.toJson(message);
        log.info(str);
        ChromeUtils.call(projectName, "setResultCode", str);
    }

    @Override
    public void onEvent(AiMessage message) {
        String str = gson.toJson(message);
        log.info(str);
        ChromeUtils.call(projectName, "setResultCode", str);
    }

    @Override
    public void end(AiMessage message) {
        String str = gson.toJson(message);
        log.info(str);
        ChromeUtils.call(projectName, "setResultCode", str);
    }

}
