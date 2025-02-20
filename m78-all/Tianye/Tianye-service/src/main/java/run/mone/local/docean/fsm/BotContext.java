package run.mone.local.docean.fsm;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;


@Data
public class BotContext {

    volatile boolean exit;

    /**
     * 服务shutdown
     */
    private volatile boolean quit;

    private Consumer<Pair<Integer, String>> msgConsumer;

    /**
     * 停止flow流
     */
    private AtomicBoolean cancel = new AtomicBoolean(false);

    private int index;

    private List<BotState> botList;

    private BotState currentBot;

    private boolean cycle;

    private List<MemoryData> memary = new CopyOnWriteArrayList<>();

    private LinkedBlockingQueue<String> questionQueue = new LinkedBlockingQueue<>();

    private LinkedBlockingQueue<String> answerQueue = new LinkedBlockingQueue<>();

    private LinkedBlockingQueue<FlowMessage> messageList = new LinkedBlockingQueue<>();

    /**
     * 完成任务数
     */
    private AtomicInteger finishTaskNum = new AtomicInteger(0);

    /**
     * 是否发生了错误
     */
    private volatile boolean error;

    //版本为1的时候会传递给每个BotFlow自己的消息队列
    //private int msgVersion = 1;

    private AtomicReference<BotRes> botRes = new AtomicReference<>(null);

    public void setBotRes(BotRes botRes) {
        this.botRes.compareAndSet(null, botRes);
    }

    public void addQuestionMessage(String message, int msgVersion) {
        //转给特定flow(他们自己的消息队列)
        if (msgVersion == 1) {
            JsonObject jo = JsonParser.parseString(message).getAsJsonObject();
            if (jo.has("meta") && jo.get("meta").getAsJsonObject().has("targetNodeId")) {
                String targetNodeId = jo.get("meta").getAsJsonObject().get("targetNodeId").getAsString();
                if (null != msgConsumer) {
                    msgConsumer.accept(Pair.of(Integer.valueOf(targetNodeId), message));
                }
            }
        } else {
            questionQueue.add(message);
        }
    }

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

    public void toggleCancel() {
        cancel.set(!cancel.get());
    }
}
