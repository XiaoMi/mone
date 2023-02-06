package com.xiaomi.miapi.service;

import com.xiaomi.miapi.common.bo.GatewayApiInfoBo;
import com.xiaomi.miapi.common.dto.ManualGatewayUpDTO;
import com.xiaomi.miapi.common.dto.UrlDTO;
import com.xiaomi.miapi.common.Result;

import java.util.List;
import java.util.Map;

public interface GatewayApiService {
    public Result<Boolean> manualUpdateGatewayApi(ManualGatewayUpDTO dto);

    public Result<List<Map<String, Object>>> getGatewayApiDetailByUrl(List<UrlDTO> urls);

    public Result<Map<String, Object>> getGatewayApiDetail(Integer userId, Integer projectID, Integer apiID);

    public Map<String, Object> getBasicGatewayApiDetail(Integer projectID, Integer apiID);

    public Result<Boolean> addGatewayApi(GatewayApiInfoBo bo, String apiHeader, String apiRequestParam, String apiResultParam, String apiErrorCodes);

    public Result<Boolean> batchAddGatewayApi(Integer projectID,Integer groupID,String env,String gatewayInfos,String username);

    public Result<Boolean> updateGatewayApi(GatewayApiInfoBo bo,String apiHeader,String apiRequestParam,String apiResultParam,String apiErrorCodes,Integer apiId,int alterType);

    public Result<Map<String,Object>> loadGatewayApiInfoFromRemote(String env,String url);

}
