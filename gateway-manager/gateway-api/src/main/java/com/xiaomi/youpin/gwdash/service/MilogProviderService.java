//package com.xiaomi.youpin.gwdash.service;
//
//import com.xiaomi.youpin.gwdash.bo.*;
//import com.xiaomi.youpin.gwdash.bo.openApi.ProjectDeployInfoQuery;
//
//import java.util.List;
//
///**
// * @author wtt
// * @version 1.0
// * @description
// * @date 2021/7/22 17:26
// */
//public interface MilogProviderService {
//
//    MiLogMachineBo queryMachineInfoByProject(String projectName, String name);
//
//    Page<ProjectDeployInfoDTO> queryProjectDeployInfoList(ProjectDeployInfoQuery query);
//
//    /**
//     * 查询某台物理机器下部署的所有项目的projectId
//     *
//     * @param ip
//     * @return
//     */
//    List<String> queryAppsByIp(String ip);
//
//    List<MachineBo> queryIpsByAppId(Long projectId, Long projectEnvId, String envName);
//
//    /**
//     * 查询当前项目的所有环境信息
//     *
//     * @param projectId
//     * @return
//     */
//    List<SimplePipleEnvBo> querySimplePipleEnvBoByProjectId(Long projectId);
//
//    /**
//     * 查询某个用户拥有的所有项目
//     * @param userName
//     * @return
//     */
//    List<ProjectMemberDTO> queryProjectIdsByUserName(String userName);
//
//    /**
//     * 查询到被删除的项目
//     * @return
//     */
//    List<Long> queryDeleteProjectId();
//}
