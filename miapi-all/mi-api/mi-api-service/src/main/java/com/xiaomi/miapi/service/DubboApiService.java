package com.xiaomi.miapi.service;

import com.alibaba.nacos.api.exception.NacosException;
import com.xiaomi.miapi.bo.*;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.dto.ManualDubboUpDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DubboApiService {
    Result<Boolean> addDubboApi(ApiCacheItemBo apiBo);

    Result<Boolean> batchAddDubboApi(String apiEnv,List<BatchImportApiBo> bos);

    Result<Boolean> updateDubboApi(ApiCacheItemBo apiBo,Integer apiId);

    Result<Map<String,Object>> getAllModulesInfo(String env, String serviceName,String ip) throws NacosException;

    Result<Set<ServiceName>> loadApiServices(String serviceName);

    Result<List<ServiceName>> loadDubboApiServicesFromNacos(String serviceName,String env);

    Result<Boolean> manualUpdateDubboApi(ManualDubboUpDTO dto) throws NacosException;

    Result<Map<String, Object>> getDubboApiDetail(String username,Integer projectID, Integer apiID);

    Result<ApiCacheItem> getDubboApiDetailFromRemote(String env, GetApiBasicRequest dubboApiRequestBo);

    void dubboApiUpdateNotify(DubboApiUpdateNotifyBo bo);

}
