package com.xiaomi.youpin.gwdash.service;

import com.xiaomi.youpin.gwdash.bo.GatewayApiInfo;
import com.xiaomi.youpin.gwdash.bo.openApi.GatewayApiInfoList;
import com.xiaomi.youpin.gwdash.bo.openApi.GetGatewayApiInfoListReq;
import com.xiaomi.youpin.infra.rpc.Result;

import java.util.Map;


/**
 * @author tsingfu
 */
public interface IGatewayOpenApi {
    Result<GatewayApiInfo> getGatewayApiInfo(String url,String tenant);

    Result<GatewayApiInfoList> getGatewayApiInfoList(GetGatewayApiInfoListReq req, String user);

    Result<Map<String,Object>> getApiGroupsByUserName(String userName);


}
