package com.xiaomi.miapi.service.impl;

import com.google.gson.Gson;
import com.xiaomi.miapi.api.service.MiApiDataService;
import com.xiaomi.miapi.api.service.bo.DubboApplyDTO;
import com.xiaomi.miapi.api.service.bo.MiApiData;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.mapper.ApiMapper;
import com.xiaomi.miapi.service.DubboApiService;
import com.xiaomi.miapi.service.GatewayApiService;
import com.xiaomi.miapi.service.HttpApiService;
import com.xiaomi.miapi.util.RedisUtil;
import com.xiaomi.mone.dubbo.docs.annotations.ApiModule;
import com.xiaomi.mone.tpc.api.service.UserOrgFacade;
import com.xiaomi.mone.tpc.common.param.NullParam;
import com.xiaomi.mone.tpc.common.vo.OrgInfoVo;
import com.xiaomi.youpin.hermes.service.BusProjectService;
import com.xiaomi.youpin.infra.rpc.Result;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@DubboService(group = "${dubbo.group}",version = "1.0")
@ApiModule(value = "MiApi的接入数据服务", apiInterface = MiApiDataService.class)
class MiApiDataServiceImpl implements MiApiDataService {

    @DubboReference(check = false, group = "${ref.hermes.service.group}")
    private BusProjectService busProjectService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ApiMapper apiMapper;

    @Autowired
    private DubboApiService dubboApiService;

    @Autowired
    private HttpApiService httpApiService;

    @Autowired
    private GatewayApiService gatewayApiService;

    @DubboReference(registry = "stRegistry",check = false,group = "staging",version = "1.0")
    private UserOrgFacade userOrgFacade;

    public static final Gson gson = new Gson();

    @Override
    public Result<MiApiData> getMiApiData() {
        MiApiData miApiData = new MiApiData();
        miApiData.setProjectNum(busProjectService.getTotalAmount());
        miApiData.setApiNum(apiMapper.getApiNum());
        return Result.success(miApiData);
    }

    @Override
    public Result<Map<String,List<String>>> getMiApiUserData() {
        Map<String,List<String>> map = new HashMap<>();
        //api用户
        Set<String> usernames = new HashSet<>(apiMapper.getApiUsers());
        usernames.addAll(apiMapper.getTestApiUsers());

        usernames.forEach(account ->{
            NullParam param  = new NullParam();
            param.setAccount(account);
            param.setUserType(0);
            OrgInfoVo orgInfoVo = userOrgFacade.getOrgByAccount(param).getData();
            if (Objects.nonNull(orgInfoVo)){
                if (map.containsKey(orgInfoVo.getNamePath())){
                    map.get(orgInfoVo.getNamePath()).add(account);
                }else {
                    List<String> users = new ArrayList<>();
                    users.add(account);
                    map.put(orgInfoVo.getNamePath(),users);
                }
            }
        });
        return Result.success(map);
    }

    @Override
    public Result<Boolean> syncDubboCache() {
        return Result.success(true);
//        return Result.success(dubboApiService.syncDubboCache().getData());
    }

    @Override
    public Result<Boolean> feiShuDubboApplyCallback(DubboApplyDTO dto) {
        if (dto.getPass()){
            String rKey = String.join(":", dto.getServiceName(),dto.getGroupName(),dto.getVersion(),dto.getUsername(),dto.getUserId());
            redisUtil.saveEpKey(rKey, Consts.SUCCESS_MSG,60*60*24);
        }
        return Result.success(true);
    }

    @Override
    public List<Map<String, Object>> searchAllApiByKeyword(String keyword,Integer apiProtocol) {
        return apiMapper.searchAllApiByKeyword(keyword,apiProtocol);
    }

    @Override
    public String getApiDetailById(int projectID, int apiID, int apiRequestType) {
        Map<String, Object> resultMap;
        switch (apiRequestType){
            case Consts.HTTP_API_TYPE:
                resultMap = httpApiService.getBasicHttpApi(projectID,apiID);
                break;
            case Consts.DUBBO_API_TYPE:
                resultMap = dubboApiService.getBasicDubboApiDetail(projectID,apiID);
                break;
            case Consts.GATEWAY_API_TYPE:
                resultMap = gatewayApiService.getBasicGatewayApiDetail(projectID,apiID);
                break;
            default:
                resultMap = new HashMap<>();
        }
        return gson.toJson(resultMap);
    }
}
