package run.mone.m78.ip.bo.robot;

import com.google.gson.Gson;
import com.intellij.openapi.project.Project;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/12/1 14:38
 */
public class ProjectAiMessageManager {


    @Getter
    private Map<String, AiMessageManager> map = new HashMap<>();


    public void putMessageManager(String project, AiMessageManager manager) {
        this.map.put(project, manager);
    }

    public void removeMessageManager(String project) {
        this.map.remove(project);
    }


    public AiMessageManager getMessageManager(String project) {
        return map.get(project);
    }


    public List<AiChatMessage<?>> getMessageList(String project) {
        return map.get(project).getMessages();
    }

    public List<AiChatMessage<?>> addMessage(Project project, AiChatMessage<?> message) {
        getMessageList(project).add(message);
        return getMessageList(project);
    }

    public List<AiChatMessage<?>> addMessage(String project, AiChatMessage<?> message) {
        getMessageList(project).add(message);
        return getMessageList(project);
    }

    public List<AiChatMessage<?>> getMessageList(Project project) {
        return getMessageList(project.getName());
    }

    public void clearMsg(Project project) {
        this.getMessageManager(project.getName()).clearMsg();
    }

    //删除指定消息
    public void delMsg(Project project, String msgId) {
        this.getMessageManager(project.getName()).removeMessage(msgId);
    }

    //事件
    public EventRes event(Project project, MessageReq req) {
        return this.getMessageManager(project.getName()).event(req);
    }

    //同步信息过来
    public MessageRes appendMsg(Project project, AiChatMessage message) {
        return MessageRes.builder().id(this.getMessageManager(project.getName()).appendMsg(message)).build();
    }

    //获取消息列表
    public List<MessageRes> listMsg(Project project) {
        return this.getMessageManager(project.getName()).list().stream().map(it -> {
            String message = it.getData().toString();
            if (!(it.getData() instanceof String)) {
                message = (new Gson()).toJson(it.getData());
            }
            return MessageRes.builder().id(it.getId()).type(it.getType().name()).message(message).role(it.getRole().name()).build();
        }).collect(Collectors.toList());
    }


    private static final class LazyHolder {
        private static final ProjectAiMessageManager ins = new ProjectAiMessageManager();
    }

    public static final ProjectAiMessageManager getInstance() {
        return LazyHolder.ins;
    }


}
