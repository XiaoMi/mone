package run.mone.ultraman.state;


import com.google.common.collect.Maps;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/12/3 10:18
 */
@Data
public class StateContext {

    //目前第几步
    private int step;

    //第几步完成
    private int finishStep;

    private int promptStep;

    private int finishPromptStep;

    //隶属于那个项目
    private String project;

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


    boolean exit;

    //直接退出循环
    volatile boolean quit;

    //存储一些快照信息,方便回滚(目前只记录InitQuestionState中的上下文)
    private Map<Integer, Snapshot> snapshots = Maps.newHashMap();

    //附加问题列表(索引就是第几个问题)
    private List<String> addonList;


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


}
