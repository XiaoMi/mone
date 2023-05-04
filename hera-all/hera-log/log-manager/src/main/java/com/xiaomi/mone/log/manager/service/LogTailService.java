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

    Result<List<MapDTO>> queryAppByStoreId(Long storeId);

    Result<List<AppTypeTailDTO>> queryAppTailByStoreId(Long storeId);

    Result<List<MilogLogStoreDO>> queryLogStoreByRegionEn(String nameEn);

    Result<List<MilogTailDTO>> getTailByStoreId(Long storeId);

    Result<Object> parseScriptTest(MlogParseParam mlogParseParam);

    Result<List<QuickQueryVO>> quickQueryByApp(Long milogAppId);

    void machineIpChange(HeraEnvIpVo heraEnvIpVo);
}
