package com.xiaomi.youpin.tesla.ip.listener;

import com.intellij.openapi.project.Project;
import com.xiaomi.youpin.tesla.ip.bo.chatgpt.LocalReq;
import com.xiaomi.youpin.tesla.ip.bo.robot.*;
import com.xiaomi.youpin.tesla.ip.service.LocalAiService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import run.mone.ultraman.AthenaContext;
import run.mone.ultraman.bo.ClientData;
import run.mone.ultraman.common.GsonUtils;
import run.mone.ultraman.state.AthenaEvent;
import run.mone.ultraman.state.ProjectFsmManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2023/12/10 15:40
 */
@Slf4j
public class LocalHandler {


    @SneakyThrows
    public static Pair<Integer, String> handler(Project project, Req req) {
        //本地调用(需要开启本地模式)
        if (req.getCmd().equals("local_call")) {
            return localCall(project, req);
        }

        //获取消息列表
        if (req.getCmd().equals("list_msg")) {
            List<MessageRes> listRes = ProjectAiMessageManager.getInstance().listMsg(project);
            return Pair.of(0, GsonUtils.gson.toJson(listRes));
        }

        //清空所有消息
        if (req.getCmd().equals("clear_msg")) {
            ProjectAiMessageManager.getInstance().clearMsg(project);
            return Pair.of(300, "");
        }

        //删除指定消息
        if (req.getCmd().equals("del_msg")) {
            ProjectAiMessageManager.getInstance().delMsg(project, req.getData().get("msgId"));
            return Pair.of(300, "");
        }

        //前端消息同步
        if (req.getCmd().equals("append_msg")) {
            MessageReq messageReq = GsonUtils.gson.fromJson(req.getData().get("data"), MessageReq.class);
            AiChatMessage<Object> aiChatMessage = AiChatMessage.builder().role(Role.valueOf(messageReq.getRole().toLowerCase())).message(messageReq.getMessage()).data(messageReq.getMessage()).build();
            MessageRes res = ProjectAiMessageManager.getInstance().appendMsg(project, aiChatMessage);
            return Pair.of(0, GsonUtils.gson.toJson(res));
        }

        //event
        if (req.getCmd().equals("event_msg")) {
            MessageReq messageReq = GsonUtils.gson.fromJson(req.getData().get("data"), MessageReq.class);
            messageReq.setProject(project.getName());
            EventRes res = ProjectAiMessageManager.getInstance().event(project, messageReq);
            return Pair.of(0, GsonUtils.gson.toJson(res));
        }

        //回滚到某个状态(就是对某个问题重新提问)
        if (req.getCmd().equals("state_rollback")) {
            int index = Integer.valueOf(req.getData().get("index"));
            Map<String, String> map = new HashMap<>();
            map.put("index", String.valueOf(index));
            map.put("question", "modify_state");
            ProjectFsmManager.tell(project.getName(), map);
        }

        //查询状态机信息
        if (req.getCmd().equals("state_ask")) {
            Map<String, String> map = new HashMap<>();
            map.put("question", "info");
            AthenaEvent event = ProjectFsmManager.ask(project.getName(), map);
            event.getAskLatch().await(15, TimeUnit.SECONDS);
            String result = event.getMeta().get("result");
            return Pair.of(0, result);
        }

        //前端同步过来数据
        if (req.getCmd().equals("sync_client_data")) {
            String data = req.getData().get("syncData");
            ClientData clientData = GsonUtils.gson.fromJson(data, ClientData.class);
            String projectName = project.getName();
            AthenaContext.ins().getClientDataMap().put(projectName, clientData);
            log.info("client sync data:{}", clientData);
        }


        return Pair.of(300, "");
    }

    /**
     * Executes a local call to a service using the provided project and request data, deserializes the 'data' field from the request, and invokes the local AI service if data is not empty. Returns a Pair with status code 300 and an empty string.
     */
    @NotNull
    private static Pair<Integer, String> localCall(Project project, Req req) {
        String data = req.getData().getOrDefault("data", "");
        if (!StringUtils.isEmpty(data)) {
            LocalReq localReq = GsonUtils.gson.fromJson(data, LocalReq.class);
            //直接本地调用chatgpt
            int num = Integer.valueOf(req.getData().getOrDefault("msg_num", "0"));
            LocalAiService.localCall(project, localReq.getMsgList(), false, num);
        }
        return Pair.of(300, "");
    }

}
