package run.mone.ultraman.state;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 * @date 2023/12/3 16:14
 */
public class ProjectFsmManager {

    public static ConcurrentHashMap<String, FsmManager> map = new ConcurrentHashMap<>();


    public static boolean processMsg(String projectName, String msg) {
        return processMsg(projectName, msg, AnswerType.normal, new HashMap<>());
    }

    //如果状态机正在等待答案,则会传入状态机
    public static boolean processMsg(String projectName, String msg, AnswerType answerType, Map<String, String> meta) {
        FsmManager fsmManager = ProjectFsmManager.map.get(projectName);
        if (null != fsmManager) {
            //等待答案呢(那么这次的就不是提问)
            if (fsmManager.getFsm().getCurrentState() instanceof WaitQuestionState) {
                fsmManager.getFsm().getEventQueue().add(AthenaEvent.builder().answer(msg).meta(meta).answerType(answerType).build());
                return true;
            }
        }
        return false;
    }


    //向状态机提问题
    public static AthenaEvent ask(String projectName, Map<String, String> map) {
        AthenaEvent event = AthenaEvent.builder().answer(GlobalState.ASK).meta(map).answerType(AnswerType.empty).build();
        FsmManager fsmManager = ProjectFsmManager.map.get(projectName);
        if (null != fsmManager) {
            fsmManager.getFsm().getEventQueue().add(event);
        }
        return event;
    }

    public static void tell(String projectName, Map<String, String> map) {
        AthenaEvent event = AthenaEvent.builder().answer(GlobalState.ASK).meta(map).answerType(AnswerType.empty).build();
        FsmManager fsmManager = ProjectFsmManager.map.get(projectName);
        if (null != fsmManager) {
            fsmManager.getFsm().getEventQueue().add(event);
        }
    }


    //查询当前状态机所属状态
    public static String state(String projectName) {
        FsmManager fsmManager = ProjectFsmManager.map.get(projectName);
        if (null != fsmManager) {
            Object object = fsmManager.getFsm().getCurrentState();
            return object.getClass().getSimpleName() + ":" + object.getClass();
        }
        return "";
    }

    public static void stop(String projectName) {
        FsmManager fsmManager = ProjectFsmManager.map.get(projectName);
        if (null != fsmManager) {
            fsmManager.getFsm().getEventQueue().add(AthenaEvent.builder().answer(GlobalState.EXIT_CMD).meta(new HashMap<>()).answerType(AnswerType.normal).build());
        }
    }


}
