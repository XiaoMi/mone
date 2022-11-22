package com.xiaomi.miapi.service;

import com.alibaba.nacos.api.exception.NacosException;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.bo.*;
import com.xiaomi.miapi.common.dto.ManualDubboUpDTO;

import java.util.List;
import java.util.Map;

public interface DubboApiService {
    //添加dubbo类型接口
    public Result<Boolean> addDubboApi(ApiCacheItemBo apiBo);

    //添加dubbo类型接口
    public Result<Boolean> batchAddDubboApi(String apiEnv,List<BatchImportDubboApiBo> bos);

    //添加dubbo类型 api
    public Result<Boolean> updateDubboApi(ApiCacheItemBo apiBo,Integer apiId);

    public Result<Map<String,Object>> getAllModulesInfo(String env, String serviceName,String ip) throws NacosException;

    public Result<List<DubboService>> loadDubboApiServices(String serviceName, String env,String namespace);

    public Result<Boolean> manualUpdateDubboApi(ManualDubboUpDTO dto) throws NacosException;

    public Result<Map<String, Object>> getDubboApiDetail(Integer userId,Integer projectID, Integer apiID);

    public Map<String, Object> getBasicDubboApiDetail(Integer projectID, Integer apiID);

    public Result<ApiCacheItem> getDubboApiDetailFromRemote(String env, GetDubboApiRequestBo dubboApiRequestBo);

    public Result<Boolean> dubboApiUpdateNotify(DubboApiUpdateNotifyBo bo) throws InterruptedException;

}
