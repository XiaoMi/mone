package com.xiaomi.mone.log.manager.service;

import com.xiaomi.mone.app.api.model.HeraAppBaseInfoModel;
import com.xiaomi.mone.app.api.model.HeraAppBaseInfoParticipant;
import com.xiaomi.mone.app.api.model.HeraAppBaseQuery;
import com.xiaomi.mone.app.api.response.AppBaseInfo;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/11 17:00
 */
public interface HeraAppService {

    List<AppBaseInfo> queryAppInfoWithLog(String appName, Integer type);

    List<AppBaseInfo> queryAllExistsApp();

    AppBaseInfo queryById(Long id);

    List<AppBaseInfo> queryByIds(List<Long> ids);

    AppBaseInfo queryByAppId(Long appId, Integer type);

    Long countByParticipant(HeraAppBaseQuery query);

    List<HeraAppBaseInfoParticipant> queryByParticipant(HeraAppBaseQuery query);

    Long count(HeraAppBaseInfoModel baseInfo);

    List<HeraAppBaseInfoModel> query(HeraAppBaseInfoModel baseInfo,Integer pageCount,Integer pageNum);

    HeraAppBaseInfoModel getById(Integer id);

    int delById(Integer id);

    Long getAppCount();
}
