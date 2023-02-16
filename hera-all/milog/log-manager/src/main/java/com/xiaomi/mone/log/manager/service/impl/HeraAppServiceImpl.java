package com.xiaomi.mone.log.manager.service.impl;

import com.xiaomi.mone.app.api.model.HeraAppBaseInfoModel;
import com.xiaomi.mone.app.api.model.HeraAppBaseInfoParticipant;
import com.xiaomi.mone.app.api.model.HeraAppBaseQuery;
import com.xiaomi.mone.app.api.response.AppBaseInfo;
import com.xiaomi.mone.log.manager.service.HeraAppService;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/11 17:00
 */
@Slf4j
@Service
public class HeraAppServiceImpl implements HeraAppService {

    @Reference(interfaceClass = com.xiaomi.mone.app.api.service.HeraAppService.class, group = "$dubbo.env.group", check = false)
    private com.xiaomi.mone.app.api.service.HeraAppService heraAppService;

    @Override
    public List<AppBaseInfo> queryAppInfoWithLog(String appName, Integer type) {
        return heraAppService.queryAppInfoWithLog(appName, type);
    }

    @Override
    public List<AppBaseInfo> queryAllExistsApp() {
        return heraAppService.queryAllExistsApp();
    }

    @Override
    public AppBaseInfo queryById(Long id) {
        return heraAppService.queryById(id);
    }

    @Override
    public List<AppBaseInfo> queryByIds(List<Long> ids) {
        return heraAppService.queryByIds(ids);
    }

    @Override
    public AppBaseInfo queryByAppId(Long appId, Integer type) {
        return heraAppService.queryByAppId(appId, type);
    }

    @Override
    public Long countByParticipant(HeraAppBaseQuery query) {
        return heraAppService.countByParticipant(query);
    }

    @Override
    public List<HeraAppBaseInfoParticipant> queryByParticipant(HeraAppBaseQuery query) {
        return heraAppService.queryByParticipant(query);
    }

    @Override
    public Long count(HeraAppBaseInfoModel baseInfo) {
        return heraAppService.count(baseInfo);
    }

    @Override
    public List<HeraAppBaseInfoModel> query(HeraAppBaseInfoModel baseInfo, Integer pageCount, Integer pageNum) {
        return heraAppService.query(baseInfo, pageCount, pageNum);
    }

    @Override
    public HeraAppBaseInfoModel getById(Integer id) {
        return heraAppService.getById(id);
    }

    @Override
    public int delById(Integer id) {
        return heraAppService.delById(id);
    }

    @Override
    public Long getAppCount() {
        return heraAppService.getAppCount();
    }
}

