package run.mone.local.docean.fsm.bo;


import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import run.mone.local.docean.fsm.BotContext;
import run.mone.local.docean.fsm.BotState;
import run.mone.local.docean.util.JsonElementUtils;
import run.mone.local.docean.fsm.MemoryData;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;


@Data
@Slf4j
public class FlowContext {

    boolean exit;

    //直接退出循环
    volatile boolean quit;

    /**
     * 停止flow流
     */
    private AtomicBoolean cancel = new AtomicBoolean(false);

    private int index;

    private List<BotState> botList;

    private BotState currentBot;

    private boolean cycle;

    private List<MemoryData> memory = Lists.newArrayList();

    private LinkedBlockingQueue<String> questionQueue = new LinkedBlockingQueue<>();


    private LinkedBlockingQueue<String> answerQueue = new LinkedBlockingQueue<>();

    private Map<Integer, Map<String, ? extends ItemData>> referenceData = new HashMap<>();

    private Map<Integer, List<JsonElement>> batchParamMap = new HashMap<>();

    private Map<Integer, List<Object>> batchReslutMap = new HashMap<>();

    private FlowRes flowRes = new FlowRes();

    private long startTime;

    private Map<Integer, Integer> nodeGoToTimes = new HashMap<>();

    private Map<String, String> meta = new HashMap<>();

    private String m78RpcAddr;

    private boolean isFinalEnd;

    private BotContext botContext;


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

    public JsonElement queryFieldValueFromReferenceData(int flowId, String field) {
        log.info("queryFieldValueFromReferenceData flowId:{} field:{}", flowId, field);
        JsonElement jsonElement = new JsonPrimitive("");
        if (this.referenceData.containsKey(flowId)){
            Map<String, ? extends ItemData> itemDataMap = this.referenceData.get(flowId);
            if (!field.contains(".")) {
                if (itemDataMap.containsKey(field)){
                    return itemDataMap.get(field).getValue();
                } else {
                    return jsonElement;
                }
            }
            try {
                return JsonElementUtils.queryFieldValue(itemDataMap.get(field.split("\\.")[0]).getValue(), field.substring(field.indexOf(".")+1));
            } catch (Exception e){
                log.error("queryFieldValueFromReferenceData error. flowId:" + flowId, e);
            }
        }
        log.warn("referenceData does not contain {}", flowId);
        return jsonElement;

    }

    public int addAndGetNodeGoToTimes(int nodeId){
        nodeGoToTimes.compute(nodeId, (k, v) -> {
            if (v == null) {
                return 1;
            } else {
                return ++v;
            }
        });
        return nodeGoToTimes.get(nodeId);
    }
}
