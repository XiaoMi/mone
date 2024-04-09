package run.mone.local.docean.fsm;


import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;


@Data
public class BotContext {

    boolean exit;

    //直接退出循环
    volatile boolean quit;

    private int index;

    private List<BotState> botList;

    private BotState currentBot;

    private boolean cycle;

    private List<MemoryData> memary = Lists.newArrayList();

    private LinkedBlockingQueue<String> questionQueue = new LinkedBlockingQueue<>();

    private LinkedBlockingQueue<String> answerQueue = new LinkedBlockingQueue<>();


    /**
     * 获取下一个机器人实例。
     * 如果索引超出机器人列表大小，并且循环标志为真，则返回列表中的第一个机器人。
     * 如果索引超出机器人列表大小，并且循环标志为假，则返回null。
     * 否则返回当前索引对应的机器人实例。
     */
    public BotState nextBot() {
        index++;
        if (index >= botList.size()) {
            if (cycle) {
                return botList.get(0);
            }
            return null;
        }
        return botList.get(index);
    }

}
