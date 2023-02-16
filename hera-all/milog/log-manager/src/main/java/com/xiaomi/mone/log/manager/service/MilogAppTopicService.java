package com.xiaomi.mone.log.manager.service;

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.bo.AccessMilogParam;
import com.xiaomi.mone.log.manager.model.bo.AppTopicParam;
import com.xiaomi.mone.log.manager.model.dto.*;
import com.xiaomi.mone.log.manager.model.page.PageInfo;
import com.xiaomi.mone.log.manager.model.vo.AccessMiLogVo;
import com.xiaomi.mone.log.manager.model.vo.LogPathTopicVo;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/27 11:20
 */
public interface MilogAppTopicService {

    Result<PageInfo<MilogAppConfigTailDTO>> queryAppTopicList(AppTopicParam param);

    Result<String> createTopic(Long appId, String appName);

    Result<String> updateExistsTopic(Long id, String existTopic);

    Result<List<MapDTO>> queryAllExistTopicList();

    Result<String> delTopicRecord(Long appId);

    Result<String> delTopicRecordAll();

    Result<List<MilogAppOpenVo>> queryAllMilogAppList();

    List<LogPathTopicVo> queryTopicConfigByAppId(Long milogAppId);

    Boolean synchronousMisApp(List<MisAppInfoDTO> data);

    Boolean synchronousRadarApp(List<RadarAppInfoDTO> radarAppInfoDTOS);

    Result<AccessMiLogVo> accessToMilog(AccessMilogParam milogParam);
}
