package run.mone.m78.service.agent.state;

import lombok.Getter;
import run.mone.m78.service.agent.rebot.AiMessageManager;
import run.mone.m78.service.bo.chatgpt.AiChatMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/2/12 22:48
 */
public class ProjectAiMessageManager {

    @Getter
    private Map<String, AiMessageManager> map = new HashMap<>();

    public void addMessage(String project, AiChatMessage<Object> build) {

    }

    public List<AiChatMessage<?>> getMessageList(String project) {
        return null;
    }


    private static final class LazyHolder {
        private static final ProjectAiMessageManager ins = new ProjectAiMessageManager();
    }

    public static final ProjectAiMessageManager getInstance() {
        return LazyHolder.ins;
    }


}
