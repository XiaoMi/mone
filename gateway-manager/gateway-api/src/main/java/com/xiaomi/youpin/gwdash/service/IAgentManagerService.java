//package com.xiaomi.youpin.gwdash.service;
//
//import com.xiaomi.youpin.gwdash.bo.RemotingCommandBo;
//
//import java.util.List;
//
///**
// * @author wangyandong
// * @modify zhangzhiyong
// */
//public interface IAgentManagerService {
//
//    void send(int code, String body);
//
//    RemotingCommandBo send(String address, int code, String body, long timeout);
//
//    void asynSend(String address, int code, String body, long timout);
//
//    String getClientAddress(String ip);
//
//    void closeClient(String address);
//
//    List<String> clientList();
//}
