package com.xiaomi.mone.app.api.service;

import com.xiaomi.mone.app.api.model.HeraAppBaseInfoModel;
import com.xiaomi.mone.app.api.model.HeraAppBaseInfoParticipant;
import com.xiaomi.mone.app.api.model.HeraAppBaseQuery;
import com.xiaomi.mone.app.api.model.HeraAppRoleModel;
import com.xiaomi.mone.app.api.response.AppBaseInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/10/29 14:57
 */
public interface HeraAppService {

    List<AppBaseInfo> queryAppInfoWithLog(String appName, Integer type);

    List<AppBaseInfo> queryAllExistsApp();

    AppBaseInfo queryById(Long id);

    List<AppBaseInfo> queryByIds(List<Long> ids);

    AppBaseInfo queryByAppId(Long appId, Integer type);

    Long countByParticipant(HeraAppBaseQuery query);

    List<HeraAppBaseInfoParticipant> queryByParticipant(HeraAppBaseQuery query);

    Integer insertOrUpdate(HeraAppBaseInfoModel baseInfo);

    Long count(HeraAppBaseInfoModel baseInfo);

    List<HeraAppBaseInfoModel> query(HeraAppBaseInfoModel baseInfo, Integer pageCount, Integer pageNum);

    HeraAppBaseInfoModel getById(Integer id);

    int delById(Integer id);

    Long getAppCount();

    Integer delRoleById(Integer id);

    Integer addRole(HeraAppRoleModel roleModel);

    List<HeraAppRoleModel> queryRole(HeraAppRoleModel roleModel,Integer pageCount,Integer pageNum);

    Long countRole(HeraAppRoleModel roleModel);

}
