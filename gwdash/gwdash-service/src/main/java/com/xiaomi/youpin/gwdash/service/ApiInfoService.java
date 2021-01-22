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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.gwdash.bo.*;
import com.xiaomi.youpin.gwdash.common.*;
import com.xiaomi.youpin.gwdash.dao.ApiGroupInfoDao;
import com.xiaomi.youpin.gwdash.dao.ApiInfoDao;
import com.xiaomi.youpin.gwdash.dao.DebugRecordDao;
import com.xiaomi.youpin.gwdash.dao.mapper.ApiInfoMapper;
import com.xiaomi.youpin.gwdash.dao.model.ApiGroupInfo;
import com.xiaomi.youpin.gwdash.dao.model.ApiInfo;
import com.xiaomi.youpin.gwdash.dao.model.ApiInfoExample;
import com.xiaomi.youpin.gwdash.dao.model.DebugRecord;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import com.xiaomi.youpin.gwdash.exception.CommonException;
import com.youpin.xiaomi.tesla.bo.*;
import com.youpin.xiaomi.tesla.service.TeslaGatewayService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.lang.random.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ApiInfoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiInfoService.class);
    @Autowired
    private Dao dao;

    @Autowired
    private ApiInfoDao apiInfoDao;


    @Autowired
    private ApiInfoMapper apiInfoMapper;

    @Autowired
    private DebugRecordDao debugRecordDao;


    @Reference(cluster = "broadcast", check = false, interfaceClass = TeslaGatewayService.class, group = "${dubbo.group}")
    private TeslaGatewayService teslaGatewayService;

    @Autowired
    private Redis redis;

    @Value("${server.gwUrl}")
    private String gatewaySvrUrl;

    @Autowired
    private ApiGroupInfoDao groupInfoDao;


    public Result<Void> newApiInfo(ApiInfoParam param, String cname) {
        CheckResult chkResult = BizUtils.chkNewApiInfoParam(param);

        if (!chkResult.isValid()) {
            LOGGER.error("[ApiInfoService.newApiInfo] invalid param, check msg: {}, param: {}",
                    chkResult.getMsg(), param);
            return new Result<>(chkResult.getCode(), chkResult.getMsg());
        }

        if (apiInfoDao.existUrl(param.getUrl(), 0L)) {
            return Result.fail(CommonError.UrlExistError);
        }

        ApiInfo apiInfo = new ApiInfo();
        try {
            BeanUtils.copyProperties(param, apiInfo);
        } catch (Exception e) {
            LOGGER.error("[ApiInfoService.newApiInfo] failed to execute bean copy for ApiInfoParam, param: {}, err: {}",
                    param, e);
            return new Result<>(CommonError.BeanCopyError.code, CommonError.BeanCopyError.message);
        }

        apiInfo.setCreator(cname);
        apiInfo.setUpdater(cname);

        FlagCal flagCal = new FlagCal();
        //允许缓存
        if (param.isAllowCache()) {
            flagCal.enable(Flag.ALLOW_CACHE);
        } else {
            flagCal.disable(Flag.ALLOW_CACHE);
        }

        //允许mock数据
        if (param.isAllowMock()) {
            flagCal.enable(Flag.ALLOW_MOCK);
        } else {
            flagCal.disable(Flag.ALLOW_MOCK);
        }

        //允许打日志
        if (param.isAllowLog()) {
            flagCal.enable(Flag.ALLOW_LOG);
        } else {
            flagCal.disable(Flag.ALLOW_LOG);
        }

        //允许调用script
        if (param.isAllowScript()) {
            flagCal.enable(Flag.ALLOW_SCRIPT);
        } else {
            flagCal.disable(Flag.ALLOW_SCRIPT);
        }

        // 允许跨域
        if (param.isAllowCors()) {
            flagCal.enable(Flag.ALLOW_CORS);
        } else {
            flagCal.disable(Flag.ALLOW_CORS);
        }

        // 允许使用token
        if (param.isAllowToken()) {
            flagCal.enable(Flag.ALLOW_TOKEN);
        } else {
            flagCal.disable(Flag.ALLOW_TOKEN);
        }

        // 是否离线
        if (param.isOffline()) {
            flagCal.enable(Flag.OFF_LINE);
        } else {
            flagCal.disable(Flag.OFF_LINE);
        }

        // 是否需要鉴权
        if (param.isAllowAuth()) {
            flagCal.enable(Flag.ALLOW_AUTH);
        } else {
            flagCal.disable(Flag.ALLOW_AUTH);
        }

        // 是否使用QPS限制
        if (param.isUseQpsLimit()) {
            flagCal.enable(Flag.USE_QPS_LIMIT);
        } else {
            flagCal.disable(Flag.USE_QPS_LIMIT);
        }

        // 是否基于ip防刷
        if (param.isAllowIpAntiBrush()) {
            flagCal.enable(Flag.IP_ANTI_BRUSH);
        } else {
            flagCal.disable(Flag.IP_ANTI_BRUSH);
        }

        // 是否基于uid防刷
        if (param.isAllowUidAntiBrush()) {
            flagCal.enable(Flag.UID_ANTI_BRUSH);
        } else {
            flagCal.disable(Flag.UID_ANTI_BRUSH);
        }

        // API是否支持preview环境
        if (param.isAllowPreview()) {
            flagCal.enable(Flag.ALLOW_PREVIEW);
        } else {
            flagCal.disable(Flag.ALLOW_PREVIEW);
        }

        //放入计算后的flag 存入数据库
        apiInfo.setFlag(flagCal.getFlag());

        apiInfo.setDsIds(param.getDsIds());
        apiInfo.setFilterParams(param.getFilterParams());
        BizUtils.setApiDefaults(apiInfo);

        int inserted = apiInfoDao.newApiInfo(apiInfo);
        LOGGER.debug("[ApiInfoService.newApiInfo] apiInfo: {}", apiInfo);
        ScriptInfo scriptInfo = null;

        if (inserted <= 0) {
            LOGGER.error("[ApiInfoService.newApiInfo] failed to insert api info into db, api info: {}", apiInfo);
            return new Result<>(CommonError.UnknownError.code, CommonError.UnknownError.message);
        } else {
            String mockData = param.getMockData();
            if (StringUtils.isBlank(mockData)) {
                mockData = "";
            }
            redis.set(Keys.mockKey(apiInfo.getId()), mockData);

            String mockDataDesc = param.getMockDataDesc();
            if (StringUtils.isBlank(mockDataDesc)) {
                mockDataDesc = "";
            }

            redis.set(Keys.mockDescKey(apiInfo.getId()), mockDataDesc);

            Gson gson = new Gson();
            List<String> params = Lists.newArrayList();
            if (StringUtils.isNotBlank(param.getScriptParams())) {
                try {
                    params = gson.fromJson(param.getScriptParams(), new TypeToken<List<String>>() {
                    }.getType());
                } catch (Exception e) {
                    LOGGER.error("[ApiInfoService.newApiInfo] failed to parse script params, raw data: {}, err: {}",
                            param.getScriptParams(), e);
                }
            }
            scriptInfo = new ScriptInfo(String.valueOf(apiInfo.getId()), param.getScriptMethodName(), param.getScript(),
                    params, param.getScriptType());

            GitScriptInfo gitScriptInfo = new GitScriptInfo(param.getGitProjectId(), param.getGitToken(), param.getGitPath(),
                    param.getGitBranch(), param.getCommit());

            redis.set(Keys.gitScriptKey(apiInfo.getId()), gson.toJson(gitScriptInfo));

            LOGGER.info("[ApiInfoService.newApiInfo] param: {}, apiInfo: {}", param, apiInfo);
            if (StringUtils.isNotBlank(param.getGroupConfig())) {
                redis.set(Keys.groupConfigKey(apiInfo.getId()), param.getGroupConfig());
            }
        }
        notifyGatewayUpdate(apiInfo, ModifyType.Add);


        return new Result<>(CommonError.Success.code, CommonError.Success.message);
    }

    private void notifyGatewayUpdate(ApiInfo apiInfo, ModifyType modifyType) {
        //通知gateway api 路由发生了变更
        try {
            com.youpin.xiaomi.tesla.bo.ApiInfo info = getTeslaApiInfo(apiInfo, modifyType);
            teslaGatewayService.updateApiInfo(info);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    public boolean hasCollected(long apiInfoId, String username) {
        List<UserCollectionInfo> list = dao.query(UserCollectionInfo.class, Cnd.where("apiInfoId", "=", apiInfoId).and("username", "=", username).and("status", "=", 1));
        return list.size() > 0;
    }

    public static com.youpin.xiaomi.tesla.bo.ApiInfo getTeslaApiInfo(ApiInfo apiInfo, ModifyType modifyType) {
        DubboApiInfo dubboApiInfo = new DubboApiInfo();
        if (RouteType.isDubbo(apiInfo.getRouteType())) {
            dubboApiInfo.setGroup(apiInfo.getServiceGroup());
            dubboApiInfo.setServiceName(apiInfo.getServiceName());
            dubboApiInfo.setMethodName(apiInfo.getMethodName());
            dubboApiInfo.setVersion(apiInfo.getServiceVersion());
            dubboApiInfo.setParamTemplate(apiInfo.getParamTemplate());
        }

        ResponseConfig rc = new ResponseConfig();
        rc.setContentType(apiInfo.getContentType());

        com.youpin.xiaomi.tesla.bo.ApiInfo info = new com.youpin.xiaomi.tesla.bo.ApiInfo(
                apiInfo.getId(), new Date(), modifyType,
                apiInfo.getUrl(), apiInfo.getPath(), apiInfo.getFlag(),
                apiInfo.getRouteType(), dubboApiInfo,
                rc, null, getFilterInfoMap(apiInfo), null,
                apiInfo.getTimeout(), null, apiInfo.getInvokeLimit(), apiInfo.getQpsLimit(),
                apiInfo.getHttpMethod(), apiInfo.getToken(), apiInfo.getDsIds(),
                apiInfo.getIpAntiBrushLimit(), apiInfo.getUidAntiBrushLimit(),
                apiInfo.getFilterParams(), apiInfo.getCacheExpire());

        return info;
    }

    public static Map<String, FilterInfo> getFilterInfoMap(ApiInfo apiInfo) {
        String filterInfo = apiInfo.getFilterParams();
        if (StringUtils.isNotEmpty(filterInfo)) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<List<FilterInfo>>() {
                }.getType();
                List<FilterInfo> filterInfoList = gson.fromJson(filterInfo, type);
                return filterInfoList.stream().collect(Collectors.toMap(FilterInfo::getName, it -> it));
            } catch (Exception ex) {
                LOGGER.error("setFilterInfoMap error:{} {} {}", apiInfo.getId(), filterInfo, ex.getMessage());
            }
        }
        return Maps.newHashMap();
    }

    public Result<ApiInfoListResult> getApiList(ListParam param, int pageNo, int pageSize, List<Integer> gids, int role, String curUser) {
        int offset = (pageNo - 1) * pageSize;

        ApiInfoListResult ret = new ApiInfoListResult();


        int qty = apiInfoDao.getApiQty(param, gids, role, curUser);
        ret.setTotal(qty);

        List<ApiInfo> apis = apiInfoDao.getApiList(param, offset, pageSize, gids, role, curUser);
        LOGGER.info(" List<ApiInfo> apis :{}", apis);

        if (null == apis || apis.size() == 0) {
            apis = new ArrayList<>();

        }

        Gson gson = new Gson();
        //计算flag 状态 并获取 Mock数据和脚本信息
        List<ApiInfoDetail> apiVos = apis.stream().map(it -> {
//            //分组名
//            it.setGroupName(groupInfoDao.getApiGroupByGid(it.getGroupId()).getName());
            ApiInfoDetail vo = new ApiInfoDetail();
            vo.setDsIds(it.getDsIds());
            BeanUtils.copyProperties(it, vo);
            FlagCal cal = new FlagCal(it.getFlag());
            vo.setAllowCache(cal.isAllow(Flag.ALLOW_CACHE));
            vo.setAllowLog(cal.isAllow(Flag.ALLOW_LOG));
            vo.setAllowMock(cal.isAllow(Flag.ALLOW_MOCK));
            vo.setAllowScript(cal.isAllow(Flag.ALLOW_SCRIPT));
            vo.setAllowCors(cal.isAllow(Flag.ALLOW_CORS));
            vo.setAllowToken(cal.isAllow(Flag.ALLOW_TOKEN));
            vo.setOffline(cal.isAllow(Flag.OFF_LINE));
            vo.setAllowAuth(cal.isAllow(Flag.ALLOW_AUTH));
            vo.setUseQpsLimit(cal.isAllow(Flag.USE_QPS_LIMIT));
            vo.setAllowIpAntiBrush(cal.isAllow(Flag.IP_ANTI_BRUSH));
            vo.setAllowUidAntiBrush(cal.isAllow(Flag.UID_ANTI_BRUSH));
            vo.setAllowPreview(cal.isAllow(Flag.ALLOW_PREVIEW));
            vo.setGroupName(groupInfoDao.getApiGroupByGid(it.getGroupId()).getName());
            //添加收藏信息
            vo.setHasCollected(hasCollected(it.getId(), curUser));
            String mockData = redis.get(Keys.mockKey(it.getId()));
            if (StringUtils.isEmpty(mockData)) {
                mockData = "";
            }
            vo.setMockData(mockData);

            String mockDataDesc = redis.get(Keys.mockDescKey(it.getId()));
            if (StringUtils.isBlank(mockDataDesc)) {
                mockDataDesc = "";
            }

            vo.setMockDataDesc(mockDataDesc);

            String groupConfig = redis.get(Keys.groupConfigKey(it.getId()));
            if (StringUtils.isEmpty(groupConfig)) {
                groupConfig = "";
            }
            vo.setGroupConfig(groupConfig);

            String scriptData = redis.get(Keys.scriptKey(it.getId()));
            if (!StringUtils.isBlank(scriptData)) {
                try {
                    ScriptInfo scriptInfo = gson.fromJson(scriptData, ScriptInfo.class);
                    vo.setScript(scriptInfo.getScript());
                    vo.setScriptMethodName(scriptInfo.getMethodName());
                    vo.setScriptParams(gson.toJson(scriptInfo.getParams()));
                    vo.setScriptType(scriptInfo.getScriptType());
                } catch (Exception e) {
                    LOGGER.error("[ApiInfoService.getApiList] failed to parse script info for ApiInfo {}, err: {}",
                            it.getId(), e);
                }
            }

            String gitScriptData = redis.get(Keys.gitScriptKey(it.getId()));
            if (!StringUtils.isBlank(gitScriptData)) {
                try {
                    GitScriptInfo gitScriptInfo = gson.fromJson(gitScriptData, GitScriptInfo.class);
                    vo.setGitProjectId(gitScriptInfo.getGitProjectId());
                    vo.setGitToken(gitScriptInfo.getGitToken());
                    vo.setGitPath(gitScriptInfo.getGitPath());
                    vo.setGitBranch(gitScriptInfo.getGitBranch());
                    vo.setCommit(gitScriptInfo.getCommit());
                } catch (Exception e) {
                    LOGGER.error("[ApiInfoService.getApiList] failed to parse script info for ApiInfo {}, err: {}",
                            it.getId(), e);
                }
            }

            return vo;
        }).collect(Collectors.toList());


        ret.setInfoList(apiVos);
        LOGGER.debug("[ApiInfoService.getApiList] pageNo: {}, pageSize: {}, result: {}",
                pageNo, pageSize, ret);

        return new Result<>(CommonError.Success.code, CommonError.Success.message, ret);
    }


    public String export(LongIDsParam param) {
        ApiInfoExample example = new ApiInfoExample();
        ApiInfoExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(param.getIds());
        List<ApiInfo> list = apiInfoMapper.selectByExampleWithBLOBs(example);
        return new Gson().toJson(list);
//        if (list.size() > 0) {
//            ApiInfo apiInfo = list.get(0);
//            return new Gson().toJson(apiInfo);
//        }
//        return "";
    }

    /**
     * 导入api
     *
     * @return
     */
    public boolean importApi(String str, String creator) {
        Type type = new TypeToken<List<ApiInfo>>() {
        }.getType();
        List<ApiInfo> apiInfos = new Gson().fromJson(str, type);
        apiInfos.stream().forEach(apiInfo -> {
            if (apiInfo.getGroupId() == 0) {
                int gid = getGidByUrl(apiInfo.getUrl());
                apiInfo.setGroupId(gid);
            }
            // 分环境的filter ID对不上 直接抹掉
            apiInfo.setFilterParams("");
            long now = System.currentTimeMillis();
            apiInfo.setCtime(now);
            apiInfo.setUtime(now);

            if (null == apiInfo.getParamTemplate()) {
                apiInfo.setParamTemplate("");
            }
            if (existUrl(apiInfo.getUrl(), null)) {
                apiInfo.setUrl(apiInfo.getUrl() + "_Copy");
            }
            if (StringUtils.isNotEmpty(creator)) {
                apiInfo.setCreator(creator);
                apiInfo.setUpdater(creator);
            }
            apiInfo.setId(null);
        });

        batchInsert(apiInfos);
        return true;
    }

    @Transactional
    private void batchInsert(List<ApiInfo> apiInfos) {
       apiInfoMapper.batchInsert(apiInfos);
    }

    public int getGidByUrl(String url) {
        int gid = 0;
        List<String> urlSplited = Arrays.asList(url.split("/"));
        if (urlSplited.size() < 3) {
            return 0;
        }
        String urlPrefix = String.join("/", Arrays.asList(urlSplited.get(0), urlSplited.get(1), urlSplited.get(2)));
        urlPrefix += "/";
        ApiGroupInfo apiGroupInfo = groupInfoDao.getApiGroupByBaseUrl(urlPrefix);
        if (apiGroupInfo != null) {
            gid = apiGroupInfo.getGid();
        }
        return gid;
    }


    public Result<Integer> delApiInfo(LongIDsParam param, List<Integer> gids, int role) {
        if (param == null || param.getIds() == null || param.getIds().size() <= 0) {
            LOGGER.error("[ApiInfoService.delApiGroup] invalid id list param: {}", param);
            return new Result<>(CommonError.InvalidIDParamError.code, "无效的id参数列表");
        }


        ApiInfoExample example = new ApiInfoExample();
        ApiInfoExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(param.getIds());
        if (role != Consts.ROLE_ADMIN) {
            criteria.andGroupIdIn(gids);
        }
        List<ApiInfo> list = apiInfoMapper.selectByExample(example);


        int deleted = apiInfoDao.delApiInfo(param.getIds());
        //将收藏表里面对应的项目置为0
        long utime = System.currentTimeMillis();
        param.getIds().stream().forEach(deletedId ->
                dao.update(UserCollectionInfo.class, Chain.make("status", 0).add("utime", utime), Cnd.where("apiInfoId", "=", deletedId).and("status", "=", 1)));


        if (deleted > 0) {
            list.stream().forEach(it -> redis.del(Keys.mockKey(it.getId())));
        }

        LOGGER.debug("[ApiInfoService.delApiInfo] {} api groups have been deleted, ids: {}", deleted, param.getIds());

        notifyGatewayDel(list);

        return new Result<>(CommonError.Success.code, CommonError.Success.message, deleted);
    }

    private void notifyGatewayDel(List<ApiInfo> list) {
        try {
            list.stream().forEach(it -> {
                com.youpin.xiaomi.tesla.bo.ApiInfo apiInfo = getTeslaApiInfo(it, ModifyType.Delete);
                teslaGatewayService.updateApiInfo(apiInfo);
            });
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    public Boolean updateApiInfoCreator(ApiInfoUpdateCreatorParam apiInfoUpdateCreatorParam) {
        List<ApiInfo> apiInfos = apiInfoDao.getApiInfoById(apiInfoUpdateCreatorParam.getId());
        ApiInfo apiInfo = new ApiInfo();
        if (apiInfos.size() > 0) {
            apiInfo = apiInfos.get(0);
        } else {
            return false;
        }
        try {
            apiInfo.setCreator(apiInfoUpdateCreatorParam.getUsername());
            apiInfo.setUpdater(apiInfoUpdateCreatorParam.getOperator());
            Long now = System.currentTimeMillis();
            apiInfo.setUtime(now);
            apiInfoDao.updateApiInfoById(apiInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        LOGGER.info("user:{} updateCreator for {} to {}", apiInfoUpdateCreatorParam.getOperator(), apiInfoUpdateCreatorParam.getUrl(), apiInfoUpdateCreatorParam.getUsername());
        return true;
    }

    public Result<Void> updateApiInfo(ApiInfoUpdateParam param, String uname) {
        CheckResult checkResult = BizUtils.chkUpdateApiInfoParam(param);

        if (!checkResult.isValid()) {
            LOGGER.error("[ApiInfoService.updateApiInfo] invalid param, check msg: {}, param: {}",
                    checkResult.getMsg(), param);
            return new Result<>(checkResult.getCode(), checkResult.getMsg());
        }

        if (apiInfoDao.existUrl(param.getUrl(), param.getId())) {
            return Result.fail(CommonError.UrlExistError);
        }

        ApiInfo apiInfo = new ApiInfo();

        try {
            BeanUtils.copyProperties(param, apiInfo);
        } catch (Exception e) {
            LOGGER.error("[ApiInfoService.updateApiInfo] failed to execute bean copy for ApiInfo, param: {}, err: {}", param, e);
            return new Result<>(CommonError.BeanCopyError.code, CommonError.BeanCopyError.message);
        }

        apiInfo.setUpdater(uname);

        FlagCal flagCal = new FlagCal();
        //允许缓存
        if (param.isAllowCache()) {
            flagCal.enable(Flag.ALLOW_CACHE);
        } else {
            flagCal.disable(Flag.ALLOW_CACHE);
        }

        //允许mock数据
        if (param.isAllowMock()) {
            flagCal.enable(Flag.ALLOW_MOCK);
        } else {
            flagCal.disable(Flag.ALLOW_MOCK);
        }

        //允许打日志
        if (param.isAllowLog()) {
            flagCal.enable(Flag.ALLOW_LOG);
        } else {
            flagCal.disable(Flag.ALLOW_LOG);
        }

        //允许调用script
        if (param.isAllowScript()) {
            flagCal.enable(Flag.ALLOW_SCRIPT);
        } else {
            flagCal.disable(Flag.ALLOW_SCRIPT);
        }

        // 允许跨域
        if (param.isAllowCors()) {
            flagCal.enable(Flag.ALLOW_CORS);
        } else {
            flagCal.disable(Flag.ALLOW_CORS);
        }

        // 允许token
        if (param.isAllowToken()) {
            flagCal.enable(Flag.ALLOW_TOKEN);
        } else {
            flagCal.disable(Flag.ALLOW_TOKEN);
        }


        // 是否离线
        if (param.isOffline()) {
            flagCal.enable(Flag.OFF_LINE);
        } else {
            flagCal.disable(Flag.OFF_LINE);
        }

        // 是否需要鉴权
        if (param.isAllowAuth()) {
            flagCal.enable(Flag.ALLOW_AUTH);
        } else {
            flagCal.disable(Flag.ALLOW_AUTH);
        }

        // 是否使用QPS限制
        if (param.isUseQpsLimit()) {
            flagCal.enable(Flag.USE_QPS_LIMIT);
        } else {
            flagCal.disable(Flag.USE_QPS_LIMIT);
        }

        // 是否基于ip防刷
        if (param.isAllowIpAntiBrush()) {
            flagCal.enable(Flag.IP_ANTI_BRUSH);
        } else {
            flagCal.disable(Flag.IP_ANTI_BRUSH);
        }

        // 是否基于uid防刷
        if (param.isAllowUidAntiBrush()) {
            flagCal.enable(Flag.UID_ANTI_BRUSH);
        } else {
            flagCal.disable(Flag.UID_ANTI_BRUSH);
        }

        // API是否支持preview环境
        if (param.isAllowPreview()) {
            flagCal.enable(Flag.ALLOW_PREVIEW);
        } else {
            flagCal.disable(Flag.ALLOW_PREVIEW);
        }

        apiInfo.setFlag(flagCal.getFlag());
        apiInfo.setDsIds(param.getDsIds());
        apiInfo.setFilterParams(param.getFilterParams());
        // 默认 所有值都会被传回
        BizUtils.setApiDefaults(apiInfo);

        ScriptInfo scriptInfo = null;
        apiInfo.setDsIds(param.getDsIds());
        int affected = apiInfoDao.updateApiInfoById(apiInfo);
        if (affected <= 0) {
            LOGGER.warn("[ApiInfoService.updateApiInfo] nothing updated, param: {}, apiInfo: {}", param, apiInfo);
        } else {
            String mockData = param.getMockData();
            if (StringUtils.isEmpty(mockData)) {
                mockData = "";
            }
            redis.set(Keys.mockKey(apiInfo.getId()), mockData);

            String mockDataDesc = param.getMockDataDesc();
            if (StringUtils.isBlank(mockDataDesc)) {
                mockDataDesc = "";
            }
            redis.set(Keys.mockDescKey(apiInfo.getId()), mockDataDesc);

            Gson gson = new Gson();
            List<String> params = Lists.newArrayList();
            if (StringUtils.isNotBlank(param.getScriptParams())) {
                try {
                    params = gson.fromJson(param.getScriptParams(), new TypeToken<List<String>>() {
                    }.getType());
                } catch (Exception e) {
                    LOGGER.error("[ApiInfoService.updateApiInfo] failed to parse script params, raw data: {}, err: {}",
                            param.getScriptParams(), e);
                }
            }
            scriptInfo = new ScriptInfo(String.valueOf(apiInfo.getId()), param.getScriptMethodName(), param.getScript(),
                    params, param.getScriptType());

            redis.set(Keys.scriptKey(apiInfo.getId()), gson.toJson(scriptInfo));

            GitScriptInfo gitScriptInfo = new GitScriptInfo(param.getGitProjectId(), param.getGitToken(), param.getGitPath(),
                    param.getGitBranch(), param.getCommit());

            redis.set(Keys.gitScriptKey(apiInfo.getId()), gson.toJson(gitScriptInfo));

            if (param.getGroupConfig() == null) {
                param.setGroupConfig("");
            }
            redis.set(Keys.groupConfigKey(apiInfo.getId()), param.getGroupConfig());
        }

        LOGGER.debug("[ApiInfoService.updateApiInfo] {} api info updated, param: {}", affected, param);

        try {
            com.youpin.xiaomi.tesla.bo.ApiInfo info = getTeslaApiInfo(apiInfo, ModifyType.Modify);

            teslaGatewayService.updateApiInfo(info);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }

        return new Result<>(CommonError.Success.code, CommonError.Success.message);
    }

    /**
     * 判断指定url是否已经在数据库中存在了，或者是否会覆盖已经存在的url
     *
     * @param url
     * @return
     */
    public boolean existUrl(String url, Long id) {
        if (StringUtils.isBlank(url)) {
            return false;
        }

        return apiInfoDao.existUrl(url, id);
    }

    public List<ApiInfo> getApiInfoDetailByUrl(String url) {
        List<ApiInfo> list = apiInfoDao.getApiInfoDetailByUrl(url);
        return list;

    }


    public Result<DebugRecord> getDebugRecordByAid(int aid) {
        DebugRecord record = debugRecordDao.getRecordByAid(aid);
        if (record == null) {
            LOGGER.debug("[ApiInfoService.getDebugRecordByAid] debug record of api id {} not exist", aid);
            return Result.success(null);
        }

        List<ApiInfo> list = apiInfoDao.getApiInfoById(aid);
        ApiInfo apiInfo = list.size() > 0 ? list.get(0) : null;
        if (apiInfo != null) {
            record.setUrl(apiInfo.getUrl());
            record.setHttpMethod(apiInfo.getHttpMethod());
            record.setTimeout(apiInfo.getTimeout());
        }

        adapterScriptDebug(aid, record);

        return Result.success(record);
    }

    private void adapterScriptDebug(int aid, DebugRecord record) {
        if (redis.exists(Keys.scriptDebugKey(String.valueOf(aid)))) {
            Map<String, String> map = new HashMap<>();
            map.put("scriptDebug", redis.get(Keys.scriptDebugKey(String.valueOf(aid))));
            record.setExt(new Gson().toJson(map));
        }
    }

    public Result<DebugRecord> debug(ApiDebugParam param) {
        CheckResult chkResult = BizUtils.chkApiDebugParam(param);
        if (!chkResult.isValid()) {
            LOGGER.error("[ApiInfoService.debug] invalid param: {}, msg: {}", param, chkResult.getMsg());
            return new Result<>(CommonError.InvalidParamError.code, chkResult.getMsg());
        }

        if (param.getTimeout() == null || param.getTimeout() <= 0) {
            param.setTimeout(Consts.DEFAULT_API_TIMEOUT);
        }

        HttpResult result = null;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> headers = new HashMap<>();
        try {
            if (StringUtils.isNotBlank(param.getHeaders())) {
                headers = gson.fromJson(param.getHeaders(), new TypeToken<Map<String, String>>() {
                }.getType());
            }
        } catch (Exception e) {
            LOGGER.error("[ApiInfoService.debug] invalid headers, headers: {}, err: {}", headers, e);
            throw new CommonException(CommonError.InvalidParamError.code, "无效的headers格式，请使用JSON");
        }
        headers.put("Script-Debug", "on");

        LOGGER.info("debug url:{}", gatewaySvrUrl + param.getUrl());


        if (Consts.HTTP_METHOD_GET.equals(param.getHttpMethod())) {
            Map<String, String> params = null;
            try {
                if (StringUtils.isNotBlank(param.getParams())) {
                    params = gson.fromJson(param.getParams(), new TypeToken<Map<String, String>>() {
                    }.getType());
                }
            } catch (Exception e) {
                LOGGER.error("[ApiInfoService.debug] invalid params, params: {}, err: {}", params, e);
                throw new CommonException(CommonError.InvalidParamError.code, "无效的params格式，请使用JSON");
            }
            result = HttpUtils.get(gatewaySvrUrl + param.getUrl(),
                    headers,
                    params,
                    param.getTimeout());
        } else {
            result = HttpUtils.post(gatewaySvrUrl + param.getUrl(),
                    headers,
                    param.getParams(),
                    param.getTimeout());
        }

        String resultStr = gson.toJson(result);

        DebugRecord record = new DebugRecord();

        try {
            BeanUtils.copyProperties(param, record);
        } catch (Exception e) {
            LOGGER.error("[ApiInfoService.debug] failed to execute bean copy when creating db object, param: {}, err: {}",
                    param, e);
            throw new CommonException(CommonError.BeanCopyError.code, "执行结果存储时发生BeanCopy错误");
        }
        record.setResult(resultStr);
        long now = System.currentTimeMillis();
        record.setCtime(now);
        record.setUtime(now);
        // 获取filter日志信息
        List<String> filterLogList = redis.lrange("debug_filter_log_" + param.getUrl());
        record.setFilterLog(gson.toJson(filterLogList));

        debugRecordDao.insertOrUpdate(record);
        adapterScriptDebug(param.getAid(), record);

        return Result.success(record);
    }

    public HashMap<String, String> getCreatorsByUrls(ArrayList<String> urls) {
        HashMap<String, String> urlMap = new HashMap<String, String>();
        for (String url :
                urls) {
            List<ApiInfo> list = apiInfoDao.getApiInfoDetailByUrl(url);
            if (list.size() > 0) {
                urlMap.put(url, list.get(0).getCreator());
            } else {
                urlMap.put(url, null);
            }
        }
        return urlMap;
//        List<String> creators=new ArrayList<>();
//        for (String url:
//             urls) {
//            List<ApiInfo> list=apiInfoDao.getApiInfoDetailByUrl(url);
//            if(list.size()>0){
//                creators.add(list.get(0).getCreator());
//            }
//        }
//        System.out.println(creators);
//        return creators;
    }

    public Result<Boolean> setPriority(int id, int priority) {
        List<ApiInfo> list = apiInfoDao.getApiInfoById(id);
        if (null != list && list.size() > 0) {
            ApiInfo apiInfo = list.get(0);
            apiInfo.setPriority(priority);
            apiInfoDao.updateApiInfoById(apiInfo);
            return Result.success(true);
        }
        return Result.success(false);
    }

}
