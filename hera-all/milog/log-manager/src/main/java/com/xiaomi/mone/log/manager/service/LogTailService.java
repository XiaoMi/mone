package com.xiaomi.mone.log.manager.service;

import com.xiaomi.mone.app.api.response.AppBaseInfo;
import com.xiaomi.mone.app.model.vo.HeraEnvIpVo;
import com.xiaomi.mone.log.api.enums.MachineRegionEnum;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.bo.MilogLogtailParam;
import com.xiaomi.mone.log.manager.model.bo.MlogParseParam;
import com.xiaomi.mone.log.manager.model.dto.*;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTailDo;
import com.xiaomi.mone.log.manager.model.vo.QuickQueryVO;

import java.util.List;
import java.util.Map;

public interface LogTailService {

    Result<MilogTailDTO> newMilogLogTail(MilogLogtailParam param);

    void sengMessageNewTail(MilogLogtailParam param, MilogLogTailDo milogLogtailDo, MilogLogStoreDO milogLogStore);

    MilogLogTailDo buildLogTailDo(MilogLogtailParam param, MilogLogStoreDO milogLogStore, AppBaseInfo appBaseInfo, String creator);

    void sengMessageToAgent(Long milogAppId, MilogLogTailDo logtailDo);

    void sengMessageToStream(MilogLogTailDo mt, Integer type);

    void handleNaocsConfigByMotorRoom(MilogLogTailDo mt, String motorRoomEn, Integer type, Integer projectType);

    Result<MilogTailDTO> getMilogLogtailById(Long id);

    Result<Map<String, Object>> getMilogLogBypage(Long storeId, int page, int pagesize);

    Result<Map<String, Object>> getLogTailCountByStoreId(Long storeId);

    Result<List<MilogTailDTO>> getMilogLogtailByIds(List<Long> ids);

    Result<Void> updateMilogLogTail(MilogLogtailParam param);

    void updateSendMsg(MilogLogTailDo milogLogtailDo, List<String> oldIps);

    Result<Void> deleteMilogLogTail(Long id);

    void sendMessageOnDelete(MilogLogTailDo mt, MilogLogStoreDO logStoreDO);

    Result<List<MapDTO>> getAppInfoByName(String appName, Integer type);

    /**
     * 如果应用为milog-agent,获取所有的机器列表通过额外的接口
     *
     * @param milogAppId
     * @param deployWay
     * @return
     */
    Result<List<MilogAppEnvDTO>> getEnInfosByAppId(Long milogAppId, Integer deployWay);

    Result<List<String>> getTailNamesBystoreId(String tail, Integer appType, Long id);

    Result<List<MapDTO<String, String>>> tailRatelimit();

    /***
     * miline 动态扩缩容
     * @param projectInfo
     */
    void dockerScaleDynamic(DockerScaleBo projectInfo);

    MilogTailDTO milogLogtailDO2DTO(MilogLogTailDo milogLogtailDo);

    Result<List<SimpleAppEnvDTO>> getRegionZonesByAppId(Long milogAppId, String machineRoom);

    List<PodDTO> regionDTOTransferSimpleAppDTOs(List<RegionDTO> neoAppInfos, MachineRegionEnum
            machineRoom);

    Result<List<MapDTO>> queryAppByStoreId(Long storeId);

    Result<List<AppTypeTailDTO>> queryAppTailByStoreId(Long storeId);

    Result<List<MilogLogStoreDO>> queryLogStoreByRegionEn(String nameEn);

    Result<List<MilogTailDTO>> getTailByStoreId(Long storeId);

    Result<Object> parseScriptTest(MlogParseParam mlogParseParam);

    void handleK8sTopicTail(K8sMachineChangeDTO machineChangeDTO);

    void k8sPodIpsSend(Long tailId, List<String> podIps, List<String> podNamePrefix, Integer appType);

    Result<List<QuickQueryVO>> quickQueryByApp(Long milogAppId);

    /**
     * 比较机器列表并发送消息
     * 1.找到配置的log-agent的机器列表
     * 2.查询到最新的
     * 比较最新的是否比库中的多
     * 如果多，修改库，发送消息
     * 否则只修改库
     */
    void casOttMachines(String source);

    BaseMilogRpcConsumerService queryConsumerService(String source);

    void machineIpChange(HeraEnvIpVo heraEnvIpVo);
}
