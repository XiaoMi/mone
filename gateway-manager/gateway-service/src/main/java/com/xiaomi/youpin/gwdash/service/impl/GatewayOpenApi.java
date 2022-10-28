package com.xiaomi.youpin.gwdash.service.impl;


import com.alibaba.nacos.common.utils.StringUtils;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.gwdash.bo.ApiInfoDetail;
import com.xiaomi.youpin.gwdash.bo.ApiInfoListResult;
import com.xiaomi.youpin.gwdash.bo.GatewayApiInfo;
import com.xiaomi.youpin.gwdash.bo.ListParam;
import com.xiaomi.youpin.gwdash.bo.openApi.GatewayApiInfoList;
import com.xiaomi.youpin.gwdash.bo.openApi.GetGatewayApiInfoListReq;
import com.xiaomi.youpin.gwdash.common.Keys;
import com.xiaomi.youpin.gwdash.dao.model.ApiInfo;
import com.xiaomi.youpin.gwdash.service.ApiGroupInfoService;
import com.xiaomi.youpin.gwdash.service.ApiInfoService;
import com.xiaomi.youpin.gwdash.service.IGatewayOpenApi;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import com.youpin.xiaomi.tesla.bo.Flag;
import com.youpin.xiaomi.tesla.bo.FlagCal;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.xiaomi.youpin.gwdash.common.Consts.ROLE_ADMIN;


/**
 * @author tsingfu
 */
@Slf4j
@Service(interfaceClass = IGatewayOpenApi.class, retries = 0, group = "${dubbo.group}")
public class GatewayOpenApi implements IGatewayOpenApi {

    @Autowired
    private ApiInfoService apiInfoService;

    @Autowired
    ApiGroupInfoService apiGroupInfoService;

    @Autowired
    private Redis redis;

    @Override
    public Result<GatewayApiInfo> getGatewayApiInfo(String url, String tenant) {
        List<ApiInfo> apiInfos = apiInfoService.getApiInfoDetailByUrl(url, tenant);
        if (apiInfos == null || apiInfos.size() == 0) {
            return Result.fail(GeneralCodes.NotFound, "该url不存在");
        }
        GatewayApiInfo gatewayApiInfo = new GatewayApiInfo();
        try {
            ApiInfo it = apiInfos.get(0);
            BeanUtils.copyProperties(it, gatewayApiInfo);
            FlagCal cal = new FlagCal(it.getFlag());
            gatewayApiInfo.setAllowMock(cal.isAllow(Flag.ALLOW_MOCK));
            String mockData = redis.get(Keys.mockKey(it.getId()));
            if (StringUtils.isEmpty(mockData)) {
                mockData = "";
            }
            gatewayApiInfo.setMockData(mockData);

            String mockDataDesc = redis.get(Keys.mockDescKey(it.getId()));
            if (StringUtils.isBlank(mockDataDesc)) {
                mockDataDesc = "";
            }

            gatewayApiInfo.setMockDataDesc(mockDataDesc);

        } catch (BeansException e) {
            return Result.fail(GeneralCodes.InternalError, "复制api信息出错");
        }
        return Result.success(gatewayApiInfo);
    }

    @Override
    public Result<GatewayApiInfoList> getGatewayApiInfoList(GetGatewayApiInfoListReq req, String user) {
        ListParam listParam = new ListParam();
        listParam.setPageNo(req.getPage());
        listParam.setPageSize(req.getPageSize());
        listParam.setUrlString(req.getUrl());
        listParam.setName(req.getName());
        ApiInfoListResult apiInfoListResult = apiInfoService.getApiList(listParam, user, new ArrayList<>(), ROLE_ADMIN).getData();

        GatewayApiInfoList res = new GatewayApiInfoList();
        res.setPage(req.getPage());
        res.setPageSize(req.getPageSize());
        List<ApiInfoDetail> apiInfoDetails = apiInfoListResult.getInfoList();
        List<GatewayApiInfo> gatewayApiInfos = apiInfoDetails.stream().map(it -> {
            GatewayApiInfo gatewayApiInfo = new GatewayApiInfo();
            BeanUtils.copyProperties(it, gatewayApiInfo);
            return gatewayApiInfo;
        }).collect(Collectors.toList());
        res.setList(gatewayApiInfos);
        res.setTotal(apiInfoListResult.getTotal());

        return Result.success(res);
    }

    @Override
    public Result<Map<String, Object>> getApiGroupsByUserName(String userName) {
        Map<String, Object> apiGroupByUserName = apiGroupInfoService.getApiGroupByUserName(userName);
        return Result.success(apiGroupByUserName);
    }
}
