package com.xiaomi.miapi.api.service;

import com.xiaomi.miapi.api.service.bo.DubboApplyDTO;
import com.xiaomi.miapi.api.service.bo.MiApiData;
import com.xiaomi.youpin.infra.rpc.Result;

import java.util.List;
import java.util.Map;

public interface MiApiDataService {
    Result<MiApiData> getMiApiData();

    Result<Map<String,List<String>>> getMiApiUserData();

    Result<Boolean> syncDubboCache();

    Result<Boolean> feiShuDubboApplyCallback(DubboApplyDTO dto);

    /**
     * mibench 搜索接口
     * @param keyword
     * @return
     */
    List<Map<String, Object>> searchAllApiByKeyword(String keyword,Integer apiProtocol);

    /**
     * mibench 获取接口详情
     * @param projectID
     * @param apiID
     * @param apiRequestType
     * @return
     */
    String getApiDetailById(int projectID,int apiID,int apiRequestType);

}
