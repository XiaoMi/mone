package run.mone.moner.server.history;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import lombok.Getter;
import run.mone.moner.server.history.model.AiChatMessage;
import run.mone.moner.server.mcp.FromType;

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

    public List<AiChatMessage<?>> addMessage(FromType fromType, AiChatMessage<?> message) {
        getMessageList(fromType.getValue()).add(message);
        return getMessageList(fromType.getValue());
    }

    public List<AiChatMessage<?>> addMessage(String project, AiChatMessage<?> message) {
        getMessageList(project).add(message);
        return getMessageList(project);
    }

    public List<AiChatMessage<?>> getMessageList(FromType fromType) {
        return getMessageList(fromType.getValue());
    }

    public void clearMsg(FromType fromType) {
        this.getMessageManager(fromType.getValue()).clearMsg();
    }

    //删除指定消息
    public void delMsg(FromType fromType, String msgId) {
        this.getMessageManager(fromType.getValue()).removeMessage(msgId);
    }

    // //事件
    // public EventRes event(FromType fromType, MessageReq req) {
    //     return this.getMessageManager(fromType.getValue()).event(req);
    // }

    // //同步信息过来
    // public MessageRes appendMsg(FromType fromType, AiChatMessage message) {
    //     return MessageRes.builder().id(this.getMessageManager(fromType.getValue()).appendMsg(message)).build();
    // }

    // //获取消息列表
    // public List<MessageRes> listMsg(FromType fromType) {
    //     return this.getMessageManager(fromType.getValue()).list().stream().map(it -> {
    //         String message = it.getData().toString();
    //         if (!(it.getData() instanceof String)) {
    //             message = (new Gson()).toJson(it.getData());
    //         }
    //         return MessageRes.builder().id(it.getId()).type(it.getType().name()).message(message).role(it.getRole().name()).build();
    //     }).collect(Collectors.toList());
    // }


    private static final class LazyHolder {
        private static final ProjectAiMessageManager ins = new ProjectAiMessageManager();
    }

    public static final ProjectAiMessageManager getInstance() {
        return LazyHolder.ins;
    }
}
