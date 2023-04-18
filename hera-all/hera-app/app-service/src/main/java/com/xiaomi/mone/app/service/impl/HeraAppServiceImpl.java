package com.xiaomi.mone.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiaomi.mone.app.api.model.HeraAppBaseInfoModel;
import com.xiaomi.mone.app.api.model.HeraAppBaseInfoParticipant;
import com.xiaomi.mone.app.api.model.HeraAppBaseQuery;
import com.xiaomi.mone.app.api.model.HeraAppRoleModel;
import com.xiaomi.mone.app.api.response.AppBaseInfo;
import com.xiaomi.mone.app.api.service.HeraAppService;
import com.xiaomi.mone.app.api.utils.AppTypeTransferUtil;
import com.xiaomi.mone.app.dao.HeraAppBaseInfoMapper;
import com.xiaomi.mone.app.dao.HeraAppExcessInfoMapper;
import com.xiaomi.mone.app.enums.PlatFormTypeEnum;
import com.xiaomi.mone.app.enums.ProjectTypeEnum;
import com.xiaomi.mone.app.enums.StatusEnum;
import com.xiaomi.mone.app.model.HeraAppBaseInfo;
import com.xiaomi.mone.app.model.HeraAppExcessInfo;
import com.xiaomi.mone.app.service.HeraAppBaseInfoService;
import com.xiaomi.mone.app.service.HeraAppRoleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.xiaomi.mone.app.common.Constant.GSON;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/10/29 15:05
 */
@Slf4j
@Service(registry = "registryConfig", interfaceClass = HeraAppService.class, group = "${dubbo.group}")
@org.springframework.stereotype.Service
public class HeraAppServiceImpl implements HeraAppService {

    private final HeraAppBaseInfoMapper heraAppBaseInfoMapper;

    private final HeraAppExcessInfoMapper heraAppExcessInfoMapper;

    private final HeraAppBaseInfoService heraAppBaseInfoService;

    private final HeraAppRoleService roleService;

    public HeraAppServiceImpl(HeraAppBaseInfoMapper heraAppBaseInfoMapper, HeraAppExcessInfoMapper heraAppExcessInfoMapper, @Lazy HeraAppBaseInfoServiceImpl heraAppBaseInfoService, HeraAppRoleService roleService) {
        this.heraAppBaseInfoMapper = heraAppBaseInfoMapper;
        this.heraAppExcessInfoMapper = heraAppExcessInfoMapper;
        this.heraAppBaseInfoService = heraAppBaseInfoService;
        this.roleService = roleService;
    }

    @Override
    public List<AppBaseInfo> queryAppInfoWithLog(String appName, Integer type) {
        if (Objects.nonNull(type)) {
            type = AppTypeTransferUtil.queryPlatformTypeWithLogType(type);
        }
        List<AppBaseInfo> appBaseInfos = heraAppBaseInfoMapper.queryAppInfoWithLog(appName, type);
        if (CollectionUtils.isNotEmpty(appBaseInfos)) {
            appBaseInfos = appBaseInfos.parallelStream().map(appBaseInfo -> {
                appBaseInfo.setPlatformName(PlatFormTypeEnum.getEnum(appBaseInfo.getPlatformType()).getName());
                appBaseInfo.setAppTypeName(ProjectTypeEnum.queryTypeByCode(appBaseInfo.getAppType()));
                return appBaseInfo;
            }).collect(Collectors.toList());
        }
        return appBaseInfos;
    }

    @Override
    public List<AppBaseInfo> queryAllExistsApp() {
        return queryAppInfoWithLog("", null);
    }

    @Override
    public AppBaseInfo queryById(Long id) {
        HeraAppBaseInfo heraAppBaseInfo = heraAppBaseInfoMapper.selectById(id);
        if (null != heraAppBaseInfo) {
            return generateAppBaseInfo(heraAppBaseInfo);
        }
        return null;
    }

    @Override
    public List<AppBaseInfo> queryByIds(List<Long> ids) {
        return heraAppBaseInfoMapper.queryByIds(ids);
    }

    @Override
    public AppBaseInfo queryByAppId(Long appId, Integer type) {
        QueryWrapper<HeraAppBaseInfo> queryWrapper = new QueryWrapper<HeraAppBaseInfo>().eq("status", StatusEnum.NOT_DELETED.getCode());
        queryWrapper.eq("bind_id", appId.toString());
        if (Objects.nonNull(type)) {
            Integer platformType = AppTypeTransferUtil.queryPlatformTypeWithLogType(type);
            queryWrapper.eq("platform_type", platformType);
        }
        HeraAppBaseInfo heraAppBaseInfo = heraAppBaseInfoMapper.selectOne(queryWrapper);
        if (null != heraAppBaseInfo) {
            return generateAppBaseInfo(heraAppBaseInfo);
        }
        return null;
    }

    public AppBaseInfo generateAppBaseInfo(HeraAppBaseInfo heraAppBaseInfo) {
        AppBaseInfo appBaseInfo = heraAppBaseInfo.toAppBaseInfo();
        HeraAppExcessInfo appExcessInfo = heraAppExcessInfoMapper
                .selectOne(new QueryWrapper<HeraAppExcessInfo>().eq("app_base_id", heraAppBaseInfo.getId()));
        if (null != appExcessInfo) {
            appBaseInfo.setNodeIPs(appExcessInfo.getNodeIPs());
            appBaseInfo.setTreeIds(appExcessInfo.getTreeIds());
        }
        // 设置为log的平台类型
        Integer code = AppTypeTransferUtil.queryLogTypeWithPlatformType(heraAppBaseInfo.getPlatformType());
        appBaseInfo.setPlatformType(code);
        appBaseInfo.setPlatformName(PlatFormTypeEnum.getEnum(heraAppBaseInfo.getPlatformType()).getName());
        appBaseInfo.setAppTypeName(ProjectTypeEnum.queryTypeByCode(appBaseInfo.getAppType()));
        return appBaseInfo;
    }

    @Override
    public Long countByParticipant(HeraAppBaseQuery query) {
        return heraAppBaseInfoService.countByParticipant(query);
    }

    @Override
    public List<HeraAppBaseInfoParticipant> queryByParticipant(HeraAppBaseQuery query) {
        return heraAppBaseInfoService.queryByParticipant(query);
    }

    @Override
    public Integer insertOrUpdate(HeraAppBaseInfoModel baseInfo) {
        HeraAppBaseInfo appBaseInfo = generateHeraAppBaseInfo(baseInfo);
        // update
        if (null != baseInfo.getBindId()) {
            return heraAppBaseInfoMapper.updateByPrimaryKey(appBaseInfo);
        }
        return heraAppBaseInfoMapper.insert(appBaseInfo);
    }

    private HeraAppBaseInfo generateHeraAppBaseInfo(HeraAppBaseInfoModel appBaseInfoModel) {
        HeraAppBaseInfo heraAppBaseInfo = new HeraAppBaseInfo();
        try {
            BeanUtils.copyProperties(appBaseInfoModel, heraAppBaseInfo);
        } catch (Exception e) {
            log.error("getById copyProperties error,ori:{}", GSON.toJson(appBaseInfoModel), e);
        }
        return heraAppBaseInfo;
    }

    @Override
    public Long count(HeraAppBaseInfoModel baseInfo) {
        return heraAppBaseInfoService.count(baseInfo);
    }

    @Override
    public List<HeraAppBaseInfoModel> query(HeraAppBaseInfoModel baseInfo, Integer pageCount, Integer pageNum) {

        List<HeraAppBaseInfoModel> list = new ArrayList<>();

        List<HeraAppBaseInfo> query = heraAppBaseInfoService.query(baseInfo, pageCount, pageNum);

        if (CollectionUtils.isEmpty(query)) {
            return list;
        }

        query.forEach(t -> {
            HeraAppBaseInfoModel model = new HeraAppBaseInfoModel();
            BeanUtils.copyProperties(t, model);
            list.add(model);
        });

        return list;
    }

    @Override
    public HeraAppBaseInfoModel getById(Integer id) {

        HeraAppBaseInfo byId = heraAppBaseInfoService.getById(id);
        if (byId == null) {
            return null;
        }

        HeraAppBaseInfoModel model = new HeraAppBaseInfoModel();

        BeanUtils.copyProperties(byId, model);

        return model;
    }

    @Override
    public int delById(Integer id) {
        return heraAppBaseInfoService.delById(id);
    }

    @Override
    public Long getAppCount() {
        return heraAppBaseInfoMapper.countNormalData();
    }

    @Override
    public Integer delRoleById(Integer id) {
        return roleService.delById(id);
    }

    @Override
    public Integer addRole(HeraAppRoleModel roleModel) {
        return roleService.addRole(roleModel);
    }

    @Override
    public List<HeraAppRoleModel> queryRole(HeraAppRoleModel roleModel, Integer pageCount, Integer pageNum) {
        return roleService.query(roleModel, pageCount, pageNum);
    }

    @Override
    public Long countRole(HeraAppRoleModel roleModel) {
        return roleService.count(roleModel);
    }


}
