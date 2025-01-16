package run.mone.m78.service.agent.state;

import com.google.common.base.Joiner;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.m78.service.dao.entity.MultimodalEnum;
import run.mone.m78.service.vo.BotVo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 * @date 2023/12/3 16:14
 */
@Slf4j
public class BotFsmManager {

    public static ConcurrentHashMap<String, FsmManager> map = new ConcurrentHashMap<>();


    public static boolean processMsg(String key, String msg) {
        return processMsg(key, msg, AnswerType.normal, new HashMap<>());
    }

    public static String key(String user, Long botId, String sessionId) {
        String key = Joiner.on("_").join(user, botId, sessionId);
        return key;
    }

    //启动状态机
    public static boolean startFsm(String topicId, String user, Long botId, BotVo botVo, String sessionId) {
        //有运行中的
        if (map.containsKey(key(user, botId, sessionId))) {
            return false;
        }
        FsmManager fsmManager = new FsmManager(topicId, user, botId, botVo, sessionId);
        fsmManager.init();
        BotFsmManager.map.put(fsmManager.key(), fsmManager);
        return true;
    }

    //如果状态机正在等待答案,则会传入状态机
    public static boolean processMsg(String key, String msg, AnswerType answerType, Map<String, String> meta) {
        FsmManager fsmManager = BotFsmManager.map.get(key);
        if (null != fsmManager) {
            //等待答案呢(那么这次的就不是提问)
            if (fsmManager.getFsm().getCurrentState() instanceof WaitQuestionState) {
                fsmManager.getFsm().getEventQueue().add(AthenaEvent.builder().content(msg).meta(meta).answerType(answerType).build());
                return true;
            }
        }
        return false;
    }


    //向状态机提问题
    public static AthenaEvent ask(String projectName, Map<String, String> map) {
        AthenaEvent event = AthenaEvent.builder().content(GlobalState.ASK).meta(map).answerType(AnswerType.empty).build();
        FsmManager fsmManager = BotFsmManager.map.get(projectName);
        if (null != fsmManager) {
            fsmManager.getFsm().getEventQueue().add(event);
        }
        return event;
    }

    public static void tell(String key, Map<String, String> map) {
        tell(key, map, AnswerType.empty, GlobalState.ASK, "", null);
    }

    public static void tell(String key, Map<String, String> map, AnswerType answerType, String content, String role, JsonObject input) {
        FsmManager fsmManager = BotFsmManager.map.get(key);
        if (null != fsmManager) {
            Integer multimodal = input.get("multimodal") == null ? MultimodalEnum.text.getCode() : input.get("multimodal").getAsInt();
            String mediaType = input.get("mediaType") == null ? null : input.get("mediaType").getAsString();
            AthenaEvent event = AthenaEvent.builder().content(content).meta(map).answerType(answerType).role(role).input(input).multimodal(multimodal).mediaType(mediaType).build();
            fsmManager.getFsm().getEventQueue().add(event);
        } else {
            log.info("{} fsmManager is null", key);
        }
    }


    //查询当前状态机所属状态
    public static String state(String key) {
        FsmManager fsmManager = BotFsmManager.map.get(key);
        if (null != fsmManager) {
            Object object = fsmManager.getFsm().getCurrentState();
            return object.getClass().getSimpleName() + ":" + object.getClass();
        }
        return "";
    }

    public static AthenaState getState(String key) {
        FsmManager fsmManager = BotFsmManager.map.get(key);
        if (null != fsmManager) {
            AthenaState object = fsmManager.getFsm().getCurrentState();
            return object;
        }
        return null;
    }

    /**
     * 停止指定会话的状态机
     * <p>
     * 该方法用于停止指定会话的状态机。它首先从 BotFsmManager 的 map 中获取与给定 key 相关联的 FsmManager 实例。
     * 如果找到了相应的 FsmManager 实例,则向其状态机的事件队列中添加一个 AthenaEvent 对象,该对象表示退出命令。
     * 这将导致状态机停止运行并退出当前会话。
     *
     * @param key 会话的唯一标识符
     */
    public static void stop(String key) {
        if(StringUtils.isNotEmpty(key) && key.endsWith("_|&|")) {
            String prefix = key.split("_|&|")[0];
            for (String s : BotFsmManager.map.keySet()) {
                if(s.startsWith(prefix)) {
                    stopSingle(s);
                }
            }
            return;
        }
        stopSingle(key);
    }

    private static void stopSingle(String key) {
        FsmManager fsmManager = BotFsmManager.map.get(key);
        if (null != fsmManager) {
            fsmManager.getFsm().getEventQueue().add(AthenaEvent.builder().content(GlobalState.EXIT_CMD).meta(new HashMap<>()).answerType(AnswerType.normal).build());
        }
    }


    public static void remove(String key) {
        BotFsmManager.map.remove(key);
    }

    public static void clearBotContextMsg(String key) {
        FsmManager fsmManager = BotFsmManager.map.get(key);
        if (null != fsmManager) {
            fsmManager.getFsm().getEventQueue().add(AthenaEvent.builder().content(GlobalState.CLEAR_MSG_CMD).meta(new HashMap<>()).answerType(AnswerType.normal).build());
        }
    }


}
