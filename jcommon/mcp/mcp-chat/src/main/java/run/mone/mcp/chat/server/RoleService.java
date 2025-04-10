package run.mone.mcp.chat.server;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import run.mone.hive.common.RoleType;
import run.mone.hive.llm.LLM;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ChatTool;
import run.mone.hive.schema.Message;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2025/4/9 09:49
 */
@Service
public class RoleService {

    @Resource
    private LLM llm;

    private ReactorRole minzai = null;

    @PostConstruct
    public void init() {
        minzai = new ReactorRole("minzai", new CountDownLatch(1), llm);
        minzai.setScheduledTaskHandler(role -> {
            long now = System.currentTimeMillis();
            List<Message> messageList = role.getRc().getMessageList();
            if (!messageList.isEmpty()) {
                Message lastMsg = messageList.get(messageList.size() - 1);
                if (now - lastMsg.getCreateTime() > TimeUnit.MINUTES.toMillis(1)) {
                    role.putMessage(Message.builder().role(RoleType.assistant.name()).content("用户好久没说话了,和用户随便聊聊吧").build());
                }
            }
        });
        //支持使用聊天工具
        minzai.getTools().add(new ChatTool());

        //一直执行不会停下来
        minzai.run();
    }

    public Flux<String> receiveMsg(Message message) {
        return Flux.create(sink -> {
            message.setSink(sink);
            minzai.putMessage(message);
        });
    }


}
