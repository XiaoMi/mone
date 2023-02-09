package com.xiaomi.miapi.service;

import com.xiaomi.miapi.bo.GatewayApiInfoBo;
import com.xiaomi.miapi.dto.ManualGatewayUpDTO;
import com.xiaomi.miapi.dto.UrlDTO;
import com.xiaomi.miapi.common.Result;

import java.util.List;
import java.util.Map;

public interface GatewayApiService {
    Result<Boolean> manualUpdateGatewayApi(ManualGatewayUpDTO dto);

    Result<List<Map<String, Object>>> getGatewayApiDetailByUrl(List<UrlDTO> urls);

    Result<Map<String, Object>> getGatewayApiDetail(String username, Integer projectID, Integer apiID);

    Result<Boolean> addGatewayApi(GatewayApiInfoBo bo, String apiHeader, String apiRequestParam, String apiResultParam, String apiErrorCodes);

    Result<Boolean> batchAddGatewayApi(Integer projectID,Integer groupID,String env,String gatewayInfos,String username);

    Result<Boolean> updateGatewayApi(GatewayApiInfoBo bo,String apiHeader,String apiRequestParam,String apiResultParam,String apiErrorCodes,Integer apiId,int alterType);

    Result<Map<String,Object>> loadGatewayApiInfoFromRemote(String env,String url);

}
