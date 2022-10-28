/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.gwdash.service;

import com.google.common.base.Stopwatch;
import com.xiaomi.youpin.gwdash.bo.GatewayServerInfo;
import com.xiaomi.youpin.gwdash.common.Consts;
import com.xiaomi.youpin.gwdash.context.DashServerContext;
import com.xiaomi.youpin.gwdash.dao.mapper.ApiInfoMapper;
import com.xiaomi.youpin.gwdash.dao.model.ApiInfoExample;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import com.xiaomi.youpin.gwdash.exception.CommonException;
import com.xiaomi.youpin.hermes.bo.response.Account;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import com.youpin.xiaomi.tesla.bo.*;
import com.youpin.xiaomi.tesla.service.TeslaOpsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @author goodjava@qq.com
 */
@Slf4j
@Service(group = "${manager.dubbo.group}")
public class TeslaOpsServiceImpl implements TeslaOpsService {

    @Autowired
    private DashServerContext opsServerContext;

    @Autowired(required = false)
    private ApiInfoMapper apiDOMapper;

    @Override
    public Result<String> ping() {
        return Result.success("ops pong");
    }

    @Autowired
    private ApiInfoService apiInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private ApiGroupClusterService apiGroupClusterService;

    @Autowired
    private TeslaGatewayServiceGroup teslaGatewayServiceGroup;

    /**
     * 更新内存中的服务器信息
     * 返回的是当前ops服务器的相关信息
     *
     * @param info
     * @return
     */
    @Override
    public Result<ServerInfo> updateGatewayInfo(GatewayInfo info) {
        GatewayServerInfo gsi = opsServerContext.addOrUpdateGateWayInfo(info);
        ServerInfo serverInfo = new ServerInfo();
        if (info.getGroup() == null) {
            info.setGroup("");
        }
        serverInfo.setAgentNum((int) opsServerContext.getOnlineAgentNum(info.getGroup()));
        serverInfo.setGroup(gsi.getGroup());
        return Result.success(serverInfo);
    }

    @Override
    public Result<String> getMachineGroupByIp(String ip) {
        com.xiaomi.youpin.gwdash.common.Result<GatewayServerInfo> gatewayServerInfoResult = agentService.getByIp(ip);
        String res = "";
        if (gatewayServerInfoResult.getCode() == CommonError.Success.getCode()) {
            res = gatewayServerInfoResult.getData().getGroup();
        }
        return Result.success(res);
    }

    /**
     * gateway 拉取api列表
     *
     * @return
     */
    @Deprecated
    @Override
    public Result<List<ApiInfo>> apiInfoList() {
        ApiInfoExample example = new ApiInfoExample();
        ApiInfoExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(Consts.STATUS_VALID);
        List<com.xiaomi.youpin.gwdash.dao.model.ApiInfo> list = apiDOMapper.selectByExampleWithBLOBs(example);
        Map<String, String> map = apiGroupClusterService.getDomainRefer(null);
        List<ApiInfo> data = list.stream().map(it -> {
            List<Integer> gids = Arrays.asList(it.getGroupId());
            return ApiInfoService.getTeslaApiInfo(it, ModifyType.Add, apiGroupClusterService.getApiDomainList(gids).get(it.getGroupId()), map);
        }).collect(Collectors.toList());
        return Result.success(data);
    }

    @Deprecated
    @Override
    public Result<ApiInfoList> apiInfoList(int pageNum, int pageSize) {
        return apiInfoList(pageNum, pageSize, null, 0);
    }

    /**
     * 根据分页拉取gateway api info列表
     * tenant 租户的概念
     */
    public Result<ApiInfoList> apiInfoList(int pageNum, int pageSize, String tenant, long teslaLastUpdateTime) {
        log.info("api info list:{}", tenant);

        if (StringUtils.isNotEmpty(tenant) && teslaLastUpdateTime > 0) {
            long lastUpdateTime = teslaGatewayServiceGroup.getLastUpdateTime(tenant);
            //不需要更新
            if (teslaLastUpdateTime >= lastUpdateTime) {
                ApiInfoList list = new ApiInfoList();
                list.setTenant(tenant);
                list.setLastUpdateTime(lastUpdateTime);
                list.setList(Lists.newArrayList());
                list.setTotal(0);
                //不需要更新
                list.setState(1);
                return Result.success(list);
            }
        }


        Stopwatch sw = Stopwatch.createStarted();
        ApiInfoExample example = new ApiInfoExample();
        example.setOffset((pageNum - 1) * pageSize);
        example.setLimit(pageSize);
        ApiInfoExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(Consts.STATUS_VALID);
        //按租户查询
        if (StringUtils.isNotEmpty(tenant)) {
            criteria.andTenementEqualTo(tenant);
        }
        List<com.xiaomi.youpin.gwdash.dao.model.ApiInfo> list = apiDOMapper.selectByExampleWithBLOBs(example);
        Map<String, String> map = apiGroupClusterService.getDomainRefer(tenant);
        //根据GroupId去重
        List<Integer> domainList = list.stream().map(com.xiaomi.youpin.gwdash.dao.model.ApiInfo::getGroupId).distinct().collect(Collectors.toList());
        Map<Integer, List<String>> domainsMap = apiGroupClusterService.getApiDomainList(domainList);
        List<ApiInfo> data = list.stream().map(it -> ApiInfoService.getTeslaApiInfo(it, ModifyType.Add, domainsMap.get(it.getGroupId()), map)).collect(Collectors.toList());

        ApiInfoExample totalExample = new ApiInfoExample();
        ApiInfoExample.Criteria c = totalExample.createCriteria().andStatusEqualTo(Consts.STATUS_VALID);
        if (StringUtils.isNotEmpty(tenant)) {
            c.andTenementEqualTo(tenant);
        }
        int count = (int) apiDOMapper.countByExample(totalExample);
        ApiInfoList apiInfoList = new ApiInfoList();
        apiInfoList.setPage(pageNum);
        apiInfoList.setList(data);
        apiInfoList.setPageSize(pageSize);
        apiInfoList.setTotal(count);
        apiInfoList.setLastUpdateTime(teslaGatewayServiceGroup.getLastUpdateTime(tenant));
        apiInfoList.setTenant(tenant);
        log.debug("apiInfoList tenant:{} {} pageNum:{} total:{} use time:{}ms", tenant, pageNum, pageSize, count, sw.elapsed(TimeUnit.MILLISECONDS));
        return Result.success(apiInfoList);
    }

    @Override
    public Result<ApiInfoList> apiInfoList(ApiInfoReq apiInfoReq) {
        return apiInfoList(apiInfoReq.getPageNum(), apiInfoReq.getPageSize(), apiInfoReq.getTenant(), apiInfoReq.getLastUpdateTime());
    }

    @Override
    public Result<List<PlugInfo>> pluginInfoList() {
        return Result.success(Lists.newArrayList());
    }


    @Override
    public Result<HashMap<String, AccountVo>> getAccountsByUrls(UrlInfoParam param) {
        log.info("[AccountController.getAccountByUrls] param: {}", param);

        if (!StringUtils.isNotEmpty(param.getUsername())) {
            return Result.fail(GeneralCodes.ParamError, "username不能为空");
        }

        if (!StringUtils.isNotEmpty(param.getToken())) {
            log.info("[AccountController.getAccountByUrl] getToken: {}", param.getToken());
            return Result.fail(GeneralCodes.ParamError, "username不能为空");
        }
        if (param.getUrls() == null || param.getUrls().size() <= 0) {
            return Result.fail(GeneralCodes.ParamError, "username不能为空");
        }
        Account account = userService.queryUserByName(param.getUsername());
        if (account == null || StringUtils.isEmpty(account.getToken()) || !Objects.equals(account.getToken(), param.getToken())) {
            throw new CommonException(CommonError.NotAuthorizedPluginOptError);
        }
        //参数校验结束
        HashMap<String, String> creatorsMap = apiInfoService.getCreatorsByUrls(param.getUrls());

        if (creatorsMap == null || creatorsMap.size() == 0) {
            log.warn("[AccountController.getAccountsByUrls] creators: {}", creatorsMap);
            return Result.success(new HashMap<>());
        }
        HashMap<String, AccountVo> accounts = new HashMap<>(creatorsMap.size());
        for (Map.Entry<String, String> e : creatorsMap.entrySet()) {
            Account user = userService.queryUserByName(e.getValue());
            if (user != null) {
                AccountVo accountVo = new AccountVo();
                BeanUtils.copyProperties(user, accountVo);
                accountVo.setId(user.getId().intValue());
                accounts.put(e.getKey(), accountVo);
            }
        }
        return Result.success(accounts);

    }

}
