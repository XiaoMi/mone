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
//import com.xiaomi.youpin.gwdash.bo.MachineLabels;
//import com.xiaomi.youpin.gwdash.bo.MachineBo;
//import com.xiaomi.youpin.gwdash.bo.Page;
//import com.xiaomi.youpin.gwdash.bo.openApi.ProjectDeployInfoQuery;
//import org.nutz.dao.sql.Sql;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * @author tsingfu
// */
//public interface MachineManagementService {
//
//    public Boolean setMachineLabels(Sql... sqls);
//
//    public Sql setMachineLableSql(String ip, String key, String value);
//
//    public Boolean setMachineLabel(String ip, String key, String value);
//
//    public MachineLabels getLabelsByIp(String ip);
//
//    public List<MachineBo> queryMachineListByLabel(String key, String value);
//
//    public List<MachineBo> queryMachineListByLabel(String group, String key, String value);
//
//    public List<MachineBo> queryMachineListByLikeLabel(String group, String key, String value);
//
//    public MachineBo queryMachineListByIp(String ip);
//
//    public List<MachineBo> queryMachineList();
//
//    /**
//     * 查询用户依靠project关联的所有机器
//     * @param username
//     * @return
//     */
//    public List<MachineBo> queryMachinesByUsername(String username);
//
//
//    void updateLastUpdateTime(String ip, long time);
//
//    int countWithIp(String ip);
//
//    void addMachine(MachineBo machineBo);
//
//    void updateMachine(MachineBo machineBo);
//
//    MachineLabels queryMachineLabels(String ip);
//
//    /**
//     * 需改准备的标签
//     */
//    boolean updatePrepareLabels(long id, MachineLabels prepareLabels);
//
//    void removePrepareLabels(long machineId, long projectId);
//
//    /**
//     * 查询应用部署的机器
//     * @param appName
//     * @return
//     */
//    List<MachineBo> queryMachineListByAppName(String appName);
//
//    /**
//     * 分页查询机器
//     * @param query
//     * @return
//     */
//    Page<MachineBo> queryMachineListByPage(ProjectDeployInfoQuery query);
//}