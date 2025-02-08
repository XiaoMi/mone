package run.mone.m78.service.agent.rebot;

import org.apache.commons.lang3.StringUtils;
import run.mone.m78.service.agent.bo.BoolData;
import run.mone.m78.service.agent.bo.EventRes;
import run.mone.m78.service.agent.bo.MapData;
import run.mone.m78.service.agent.bo.MessageReq;
import run.mone.m78.service.agent.state.ProjectAiMessageManager;
import run.mone.m78.service.bo.chatgpt.AiChatMessage;
import run.mone.m78.service.bo.chatgpt.MessageType;
import run.mone.m78.service.bo.chatgpt.Role;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/12/1 14:20
 */
public class AiMessageManager {


    private List<AiChatMessage<? extends Object>> messages = new LinkedList<>();


    private Map<String, ItemData> memory = new HashMap<>();


    public void addMessage(AiChatMessage<?> message) {
        this.messages.add(message);
    }

    public void removeMessage(String id) {
        messages = messages.stream().filter(it -> {
            if (null == it.getId()) {
                return false;
            }
            return !it.getId().equals(id);
        }).collect(Collectors.toList());
    }

    public List<AiChatMessage<?>> getMessages() {
        return messages;
    }

    public Map<String, ItemData> getMemory() {
        return memory;
    }


    public void clearMsg() {
        this.messages.clear();
        this.memory.clear();
    }

    /**
     * 如果是submit 类型的event,返回的就是给前端的选择结果
     * 这里处理的其实是返回结果
     *
     * @param req
     * @return
     */
    public EventRes event(MessageReq req) {
        String id = req.getId();
        String data = "";
        //是否显示出来
        boolean show = false;
        String messageId = "";
        Optional<AiChatMessage<?>> optional = this.messages.stream().filter(it -> null != it.getId() && it.getId().equals(id)).findAny();
        if (optional.isPresent()) {
            if ("submit".equals(req.getEventType())) {
                AiChatMessage message = optional.get();
                //逻辑就是选中一个
                if (message.getType().equals(MessageType.list)) {
                    int index = Integer.valueOf(req.getMeta().getOrDefault("index", "0"));
                    List<ItemData> list = (List<ItemData>) message.getData();
                    data = list.get(index - 1).getTitle();
                    messageId = UUID.randomUUID().toString();
                    ProjectAiMessageManager.getInstance().addMessage(req.getProject(),
                            AiChatMessage.builder()
                                    .message(data)
                                    .type(MessageType.string)
                                    .data(data)
                                    .id(messageId)
                                    .role(Role.user)
                                    .build());
                    //看看有没有需要放入记忆的
                    String memary = message.getMeta().getOrDefault("memary", "").toString();
                    Map<String, String> memaryMap = new HashMap<>();
                    if (StringUtils.isNotEmpty(memary)) {
                        memaryMap.put(memary, data);
                    }
                    show = true;
                }
                //map(逻辑就是数据填充)
                else if (message.getType().equals(MessageType.map)) {
                    Map<String, String> map = req.getMapData();
                    MapData mapData = MapData.builder().map(map).build();
                    message.setData(mapData);

                    data = "数据确认完毕";
                    ProjectAiMessageManager.getInstance().addMessage(req.getProject(),
                            AiChatMessage.builder()
                                    .message(data)
                                    .type(MessageType.string)
                                    .data(data)
                                    .id(messageId)
                                    .role(Role.user)
                                    .build());
                    show = true;
                }

                //bool(返回true或者false)
                else if (message.getType().equals(MessageType.bool)) {
                    boolean anwser = Boolean.valueOf(req.getMeta().getOrDefault("anwser", "false"));
                    String question = req.getMeta().getOrDefault("question", "");
                    BoolData boolData = BoolData.builder().anwser(anwser).question(question).build();
                    message.setData(boolData);
                    data = anwser ? "是" : "否";
                    messageId = UUID.randomUUID().toString();
                    ProjectAiMessageManager.getInstance().addMessage(req.getProject(),
                            AiChatMessage.builder()
                                    .message(data)
                                    .type(MessageType.string)
                                    .data(data)
                                    .id(messageId)
                                    .role(Role.user)
                                    .build());
                    show = true;
                }
            }
        }
        return EventRes.builder().data(data).messageId(messageId).show(show).build();
    }


    public String appendMsg(AiChatMessage<?> message) {
        String id = "";
        if (StringUtils.isEmpty(message.getId())) {
            id = UUID.randomUUID().toString();
            message.setId(id);
        }
        this.messages.add(message);
        return message.getId();
    }

    public List<AiChatMessage<?>> list() {
        return this.messages;
    }
}
