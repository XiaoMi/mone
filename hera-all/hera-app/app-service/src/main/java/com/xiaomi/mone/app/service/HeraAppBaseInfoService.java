package com.xiaomi.mone.app.service;

import com.xiaomi.mone.app.api.model.HeraAppBaseInfoModel;
import com.xiaomi.mone.app.api.model.HeraAppBaseInfoParticipant;
import com.xiaomi.mone.app.api.model.HeraAppBaseQuery;
import com.xiaomi.mone.app.enums.OperateEnum;
import com.xiaomi.mone.app.model.HeraAppBaseInfo;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/10/29 12:14
 */
public interface HeraAppBaseInfoService {

    HeraAppBaseInfo queryById(Long id);

    void deleAppByBindIdAndPlat(String bindId, Integer plat);

    HeraAppBaseInfo appBaseInfoOperate(HeraAppBaseInfo heraAppBaseInfo, OperateEnum operateEnum);

    Long count(HeraAppBaseInfoModel baseInfo);

    List<HeraAppBaseInfo> query(HeraAppBaseInfoModel baseInfo, Integer pageCount, Integer pageNum);

    HeraAppBaseInfo getById(Integer id);

    Long countByParticipant(HeraAppBaseQuery query);

    List<HeraAppBaseInfoParticipant> queryByParticipant(HeraAppBaseQuery query);

    int delById(Integer id);

}
