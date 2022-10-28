///*
// *  Copyright 2020 Xiaomi
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package com.xiaomi.youpin.gwdash.service;
//
//import com.xiaomi.youpin.gwdash.bo.EnvInfo;
//import com.xiaomi.youpin.gwdash.bo.GatewayApiInfo;
//import com.xiaomi.youpin.gwdash.bo.openApi.ProjectEnvBo;
//import com.xiaomi.youpin.infra.rpc.Result;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * 　@description: gwdash dubbo api
// * 　@author zhenghao
// */
//public interface GwdashApiService {
//
//    Result<List<String>> getPhysicalIpByEnvId(long envId);
//
//    Result<Object> dockerCalculation();
//
//    /**
//     * 打开或者关闭路由(offline=true 下线服务 false 恢复线上服务)
//     *
//     * @param ids
//     * @param offline
//     * @return
//     */
//    Result<Boolean> offline(List<Long> ids, boolean offline);
//
//    /**
//     * 获取env列表信息(包括对应的projectId 和 label 信息)
//     * @param envIds
//     * @return
//     */
//    Result<List<EnvInfo>> envInfoList(List<Long> envIds);
//
//    /**
//     * qps top10
//     * @return
//     */
//    Result<Object> qps();
//
//    Result<GatewayApiInfo> getGatewayApiInfo(String url);
//    /**
//     * billing top10 任务
//     * @return
//     */
//    Result<Object> billingTopTenTask();
//
//    Result<List<String>> getAppNameByUsername(Integer accountId);
//
//    Result<List<Integer>> getUsersByAppName(String appName);
//
//    Result<Map<String, Object>> getAppsByUserName(String username,String appName,Boolean isShowAll,Integer page,Integer pageSize);
//
//    Result<Object> getProjectId(Long projectId);
//
//    Result<List<ProjectEnvBo>> getProjectEnvListByProjectId(Long projectId);
//
//    Result<List<ProjectEnvBo>> getAppListByUsername(Integer accountId);
//
//    Result<Map<String,Object>> getApiGroupsByUserName(String userName);
//
//}
