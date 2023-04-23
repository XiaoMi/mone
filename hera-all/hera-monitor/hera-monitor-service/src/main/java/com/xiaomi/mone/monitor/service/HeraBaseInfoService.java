package com.xiaomi.mone.monitor.service;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.mone.app.api.model.HeraAppBaseInfoModel;
import com.xiaomi.mone.app.api.model.HeraAppBaseInfoParticipant;
import com.xiaomi.mone.app.api.model.HeraAppRoleModel;
import com.xiaomi.mone.app.api.service.HeraAppService;
import com.xiaomi.mone.monitor.bo.AlertGroupParam;
import com.xiaomi.mone.monitor.bo.PlatForm;
import com.xiaomi.mone.monitor.bo.UserInfo;
import com.xiaomi.mone.monitor.dao.HeraAppRoleDao;
import com.xiaomi.mone.monitor.dao.model.HeraAppRole;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.extension.PlatFormTypeExtensionService;
import com.xiaomi.mone.monitor.service.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gaoxihui
 * @date 2022/3/24 4:51 下午
 */
@Slf4j
@Service
public class HeraBaseInfoService {

    @Autowired
    HeraAppRoleDao heraAppRoleDao;
    @Autowired
    AlertGroupService alertGroupService;

    @Reference(registry = "registryConfig", check = false, interfaceClass = HeraAppService.class, group = "${dubbo.group.heraapp}")
    HeraAppService hearAppService;

    @Autowired
    PlatFormTypeExtensionService platFormTypeExtensionService;

    public Result addRole(HeraAppRoleModel model) {
        Integer integer = hearAppService.addRole(model);

        log.info("addRole param:{},result:{}", model.toString(), integer);

        if (integer.intValue() > 0) {
            return Result.success();
        }

        return Result.fail(ErrorCode.unknownError);
    }

    public Result delRole(Integer id) {

        Integer i = hearAppService.delRoleById(id);
        if (i.intValue() > 0) {
            return Result.success();
        }

        return Result.fail(ErrorCode.unknownError);

    }

    public Result queryRole(HeraAppRoleModel model, Integer pageNo, Integer pageCount) {

        if (pageNo == null || pageNo.intValue() < 1) {
            pageNo = 1;
        }
        if (pageCount == null || pageCount.intValue() < 1) {
            pageCount = 10;
        }

        Long aLong = hearAppService.countRole(model);

        PageData pd = new PageData();
        pd.setPage(pageNo);
        pd.setPageSize(pageCount);
        pd.setTotal(aLong);

        if (aLong == null || aLong.intValue() == 0) {
            log.info("查询hera app角色没有数据，param:{}", model.toString());
            return Result.success(pd);
        }

        List<HeraAppRoleModel> heraAppRoleModels = hearAppService.queryRole(model, pageNo, pageCount);

        pd.setList(heraAppRoleModels);

        return Result.success(pd);

    }

    public String getArea(String bindId, Integer plat, String serverEnv) {

        HeraAppBaseInfoModel appBaseInfo = this.getAppByBindId(bindId, plat);

        log.info("getArea#appBaseInfo :{},", appBaseInfo.toString());

        if (platFormTypeExtensionService.belongPlatForm(appBaseInfo.getPlatformType(), PlatForm.miCloud)) {
            String envsMap = appBaseInfo.getEnvsMap();
            if (StringUtils.isBlank(envsMap)) {
                return null;
            }
            EnvMapping envMapping = new Gson().fromJson(envsMap, EnvMapping.class);
            log.info("getArea# appId:{},serverEnv:{}, envMapping:{}", bindId, serverEnv, envMapping.toString());
            if (envMapping == null || CollectionUtils.isEmpty(envMapping.getAreas())) {
                return null;
            }

            List<Area> areas = envMapping.getAreas();
            for (Area area : areas) {
                List<Region> regions = area.getRegions();
                if (CollectionUtils.isEmpty(regions)) {
                    log.info("getArea,no regions found!bindId:{}", bindId);
                    return null;
                }
                //这里的serverEnv对应的是region的name
                for (Region region1 : regions) {
                    if (region1.getName().equals(serverEnv)) {
                        return area.getName();
                    }

                }
            }
        }

        return null;

    }

    public Result queryByParticipant(HeraAppBaseQuery query) {

        //适配到远程查询
        return queryByParticipantRemote(query);

    }

    public HeraAppBaseInfoModel getById(Integer id) {
        return this.getByIdRemote(id);
    }

    public void deleAppById(Integer id) {

        HeraAppBaseInfoModel baseInfoModel = this.getById(id);

        Integer integer = this.deleteByIdRemote(id);
        if (integer.intValue() > 0) {
            log.info("deleAppById sucess!dataId:{}", id);
        } else {
            log.info("deleAppById fail!dataId:{}", id);
        }


        HeraAppRole role = new HeraAppRole();
        role.setAppId(baseInfoModel.getBindId());
        List<HeraAppRole> roles = heraAppRoleDao.query(role, 1, 1000);
        if (!CollectionUtils.isEmpty(roles)) {
            for (HeraAppRole roleTmp : roles) {
                Integer integer1 = heraAppRoleDao.delById(roleTmp.getId());
                if (integer1.intValue() > 0) {
                    log.info("del HeraAppRole AppById sucess!dataId:{}", id);
                } else {
                    log.info("del HeraAppRole AppById fail!dataId:{}", id);
                }
            }
        }
    }

    public void deleAppByBindIdAndPlat(String bindId, Integer plat) {

        if (StringUtils.isBlank(bindId) || plat == null) {
            log.error("invalid param,bindId:{},plat:{}", bindId, plat);
            return;
        }

        HeraAppBaseInfoModel query = new HeraAppBaseInfoModel();
        query.setBindId(bindId);
        query.setPlatformType(plat);

        List<HeraAppBaseInfoModel> list = this.query(query, null, null);

        if (CollectionUtils.isEmpty(list)) {
            log.info("deleAppByBindIdAndPlat no data found! bindId:{},plat:{}", bindId, plat);
        }

        for (HeraAppBaseInfoModel baseInfo : list) {
            Integer integer = hearAppService.delById(baseInfo.getId());
            if (integer.intValue() > 0) {
                log.info("deleAppByBindIdAndPlat success!baseInfo:{}", new Gson().toJson(baseInfo));
            } else {
                log.error("deleAppByBindIdAndPlat success!baseInfo:{}", new Gson().toJson(baseInfo));
            }
        }

    }

    public HeraAppBaseInfoModel getByBindIdAndName(String bindId, String appName) {
        if (StringUtils.isBlank(bindId) || StringUtils.isBlank(appName)) {
            log.error("getByBindIdAndName invalid param,bindId:{},appName:{}", bindId, appName);
            return null;
        }

        HeraAppBaseInfoModel query = new HeraAppBaseInfoModel();
        query.setBindId(bindId);
        query.setAppName(appName);

        List<HeraAppBaseInfoModel> list = this.query(query, null, null);

        if (CollectionUtils.isEmpty(list)) {
            log.info("HeraAppBaseInfo#getByBindIdAndName no data found,bindId:{}", bindId);
            return null;
        }

        return list.get(0);

    }

    public HeraAppBaseInfoModel getAppByBindId(String bindId, Integer platFromType) {

        if (StringUtils.isBlank(bindId)) {
            log.error("invalid param,bindId:{}", bindId);
            return null;
        }

        HeraAppBaseInfoModel query = new HeraAppBaseInfoModel();
        query.setBindId(bindId);
        query.setPlatformType(platFromType);

        List<HeraAppBaseInfoModel> list = this.query(query, null, null);

        if (CollectionUtils.isEmpty(list)) {
            log.info("HeraAppBaseInfo#getAppByBindId no data found,bindId:{}", bindId);
            return null;
        }

        return list.get(0);

    }

    public Result getAppMembersByAppId(String appId, Integer platForm, String user) {
        HeraAppRole role = new HeraAppRole();
        role.setAppId(appId);
        role.setAppPlatform(platForm);
        List<HeraAppRole> roles = heraAppRoleDao.query(role, 1, 1000);
        log.info("HeraBaseInfoService#getAppMembersByAppId appId:{}, platForm:{},result:{}",appId,platForm,new Gson().toJson(roles));

        PageData<Object> pageData = new PageData<>();
        pageData.setTotal(0l);

        if (CollectionUtils.isEmpty(roles)) {
            log.info("getAppMembersByAppId no data found!appId:{},platForm:{}", appId, platForm);
            return Result.success(pageData);
        }

        List<String> members = roles.stream().filter(t -> StringUtils.isNotBlank(t.getUser())).map(t1 -> t1.getUser()).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(members)) {
            return Result.success(pageData);
        }

        List<UserInfo> userList = Lists.newArrayList();

        AlertGroupParam param = new AlertGroupParam();
        param.setPage(1);
        param.setPageSize(50);

        for (String userName : members) {
            param.setName(userName);
            Result<PageData<List<UserInfo>>> pageDataResult = alertGroupService.userSearch(user, param);
            log.info("alertGroupService#userSearch userName:{}, result:{}",userName,new Gson().toJson(pageDataResult));
            if (pageDataResult.getData().getTotal().intValue() > 0) {
                userList.addAll(pageDataResult.getData().getList().stream().filter(t -> userName.equals(t.getName())).collect(Collectors.toList()));
            }
        }

        pageData.setList(userList);
        pageData.setTotal(Long.valueOf(userList.size()));

        return Result.success(pageData);
    }

    public Long count(HeraAppBaseInfoModel baseInfo) {

        return this.countRemote(baseInfo);
    }

    public List<HeraAppBaseInfoModel> query(HeraAppBaseInfoModel baseInfo, Integer pageCount, Integer pageNum) {
        return this.queryRemote(baseInfo, pageCount, pageNum);
    }

    public List<HeraAppBaseInfoModel> queryRemote(HeraAppBaseInfoModel baseInfo, Integer pageCount, Integer pageNum) {

        List<HeraAppBaseInfoModel> baseInfoModels = hearAppService.query(baseInfo, null, null);
        if (CollectionUtils.isEmpty(baseInfoModels)) {
            return Lists.newArrayList();
        }

        return baseInfoModels;

    }

    public Long countRemote(HeraAppBaseInfoModel baseInfo) {
        return hearAppService.count(baseInfo);

    }

    public HeraAppBaseInfoModel getByIdRemote(Integer id) {
        HeraAppBaseInfoModel baseInfoModel = hearAppService.getById(id);
        if (baseInfoModel == null) {
            return null;
        }

        return baseInfoModel;
    }

    public int deleteByIdRemote(Integer id) {

        return hearAppService.delById(id);

    }

    public Result queryByParticipantRemote(HeraAppBaseQuery query) {


        com.xiaomi.mone.app.api.model.HeraAppBaseQuery queryRemote = new com.xiaomi.mone.app.api.model.HeraAppBaseQuery();
        BeanUtils.copyProperties(query, queryRemote);

        //MyParticipant只有值为yes才查询我参与的应用，传其他值均查询所有
        if (StringUtils.isBlank(query.getMyParticipant()) || !"yes".equals(query.getMyParticipant())) {
            query.setMyParticipant(null);
        }

        PageData pd = new PageData();

        Long aLong = hearAppService.countByParticipant(queryRemote);
        pd.setTotal(aLong);
        pd.setPage(query.getPage());
        pd.setPageSize(query.getPageSize());

        if (aLong != null && aLong.intValue() > 0) {
            List<HeraAppBaseInfoParticipant> list = new ArrayList<>();
            List<com.xiaomi.mone.app.api.model.HeraAppBaseInfoParticipant> heraAppBaseInfoParticipants = hearAppService.queryByParticipant(queryRemote);

            if (!CollectionUtils.isEmpty(heraAppBaseInfoParticipants)) {
                heraAppBaseInfoParticipants.forEach(t -> {
                    HeraAppBaseInfoParticipant heraAppBaseInfoParticipant = new HeraAppBaseInfoParticipant();
                    BeanUtils.copyProperties(t, heraAppBaseInfoParticipant);
                    list.add(heraAppBaseInfoParticipant);
                });
            }

            pd.setList(list);
        }

        return Result.success(pd);

    }


    public int insertOrUpdate(HeraAppBaseInfoModel heraAppBaseInfo) {
        if (null == heraAppBaseInfo) {
            log.error("[HeraBaseInfoDao.create] null heraAppBaseInfo");
            return 0;
        }

        heraAppBaseInfo.setCreateTime(new Date());
        heraAppBaseInfo.setUpdateTime(new Date());
        heraAppBaseInfo.setStatus(0);

        heraAppBaseInfo.setAppSignId(heraAppBaseInfo.getBindId() + "-" + heraAppBaseInfo.getPlatformType());

        try {
            int affected = hearAppService.insertOrUpdate(heraAppBaseInfo);
            if (affected < 1) {
                log.warn("[HeraBaseInfoDao.create] failed to insert heraAppBaseInfo: {}", heraAppBaseInfo.toString());
                return 0;
            }
        } catch (Exception e) {
            log.error("[HeraBaseInfoDao.create] failed to insert heraAppBaseInfo: {}, err: {}", heraAppBaseInfo.toString(), e);
            return 0;
        }
        return 1;
    }

}
