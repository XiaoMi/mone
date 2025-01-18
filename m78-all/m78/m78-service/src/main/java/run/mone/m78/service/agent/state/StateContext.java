package run.mone.m78.service.agent.state;


import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.mone.m78.service.agent.bo.ChatSetup;
import run.mone.m78.service.bo.chatgpt.Message;
import run.mone.m78.service.vo.BotVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/12/3 10:18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StateContext {

    //目前第几步
    private int step;

    //第几步完成
    private int finishStep;

    private int promptStep;

    private int finishPromptStep;

    private String key;

    //当前的问题
    private String question;

    //当前问题的元数据
    private Map<String, String> questionMeta = new HashMap<>();

    //用户提出问题后的处理(可以是prompt,也可以是function)
    private List<String> beforePrompt = new ArrayList<>();

    //用户回答问题后的处理
    private List<String> afterPrompt = new ArrayList<>();

    //记忆
    private Map<String, Object> memary = new HashMap<>();

    //prompt的labels
    private Map<String, String> promptLables = new HashMap<>();

    //聊天的一些配置
    private ChatSetup chatSetup;

    //预设定的一些message
    private List<Message> messageDefinedList = new ArrayList<>();

    //聊天内容
    private List<Message> messageList = new ArrayList<>();

    boolean exit;

    //直接退出循环
    volatile boolean quit;

    //存储一些快照信息,方便回滚
    private Map<Integer, Snapshot> snapshots = Maps.newHashMap();

    //附加问题列表(索引就是第几个问题)
    private List<String> addonList;

    //机器人的一些属性
    private BotVo botVo;

    private String user;

    private Long botId;

    private String topicId;

    private String sessionId;


    public String key() {
        return Joiner.on("_").join(user, botId, sessionId);
    }


    public void reset() {
        this.step = 0;
        this.finishStep = 0;
        this.promptStep = 0;
        this.finishPromptStep = 0;
        this.beforePrompt.clear();
        this.afterPrompt.clear();
        this.memary.clear();
        this.exit = false;
        this.question = "";
        this.questionMeta.clear();
        this.messageList.clear();
        this.messageDefinedList.clear();
    }

    public void reset(StateContext context) {
        this.step = context.getStep();
        this.finishStep = context.getFinishStep();
        this.promptStep = context.getPromptStep();
        this.finishPromptStep = context.getFinishPromptStep();
        this.beforePrompt.clear();
        this.beforePrompt.addAll(context.getBeforePrompt());
        this.afterPrompt.clear();
        this.afterPrompt.addAll(context.getAfterPrompt());
        this.memary.clear();
        this.memary.putAll(context.getMemary());
        this.exit = context.isExit();
        this.question = context.getQuestion();
        this.questionMeta.clear();
        this.questionMeta.putAll(context.getQuestionMeta());
    }

    public String getMessages() {
        return getMessageList().stream().map(it -> it.getRole() + ":" + it.getContent()).collect(Collectors.joining("\n"));
    }

    //清除除了MessageList中除最后一条的所有记录,考虑容错(class)
    public void clearMessagesExceptLast(int count) {
        if (messageList != null && !messageList.isEmpty()) {
            // 如果count为0，则保留最后一条记录
            if (count == 0) {
                count = 1;
            }

            // 确保count不超过messageList的大小
            count = Math.min(count, messageList.size());

            // 获取最后count条记录
            List<Message> lastMessages = new ArrayList<>(messageList.subList(messageList.size() - count, messageList.size()));

            // 清空messageList并添加最后count条记录
            messageList.clear();
            messageList.addAll(lastMessages);
        }
    }


    public String getBotIdByName(String name) {
        Map<String, String> meta = this.getBotVo().getMeta();
        String botId = meta.getOrDefault(name, "");
        return botId;
    }


}
