//package com.xiaomi.youpin.gwdash.service;
//
//
//import com.xiaomi.youpin.gwdash.bo.RemotingCommandBo;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.dubbo.config.annotation.Reference;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * @author wangyandong
// * @modify zhangzhiyong
// */
//@Component
//public class AgentManagerServiceWrapper {
//
//    @Reference(check = false, group = "${dubbo.group}", timeout = 4000, retries = 0)
//    private IAgentManagerService agentManagerService;
//
//    public void send(String address, int code, String body, long timeout, InvokeCallback callback) {
//        RemotingCommandBo remotingCommandBo = agentManagerService.send(address, code, body, timeout);
//        if (callback != null) {
//            callback.operationComplete(remotingCommandBo);
//        }
//    }
//
//    /**
//     * 非阻塞发布
//     * @param address
//     * @param code
//     * @param body
//     * @param timeout
//     */
//    public void asynSend(String address, int code, String body, long timeout) {
//         agentManagerService.asynSend(address, code, body, timeout);
//    }
//
//
//    public void send(int code, String body) {
//        agentManagerService.send(code, body);
//    }
//
//    public RemotingCommandBo send(String address, int code, String body, long timeout) {
//        return agentManagerService.send(address, code, body, timeout);
//    }
//
//    public String getClientAddress(String ip) {
//        if (StringUtils.isBlank(ip)) {
//            return null;
//        }
//        return agentManagerService.getClientAddress(ip);
//    }
//
//    public void closeClient(String address) {
//        agentManagerService.closeClient(address);
//    }
//
//    public List<String> clientList() {
//        return agentManagerService.clientList();
//    }
//
//    interface InvokeCallback {
//        void operationComplete(RemotingCommandBo remotingCommandBo);
//    }
//
//
//}
