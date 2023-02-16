package com.xiaomi.mone.log.manager.service;

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.dto.LogtailCollectTopDTO;
import com.xiaomi.mone.log.manager.model.dto.LogtailCollectTrendDTO;
import com.xiaomi.mone.log.manager.model.dto.SpaceCollectTopDTO;
import com.xiaomi.mone.log.manager.model.dto.SpaceCollectTrendDTO;

import java.io.IOException;
import java.util.List;

public interface LogCountService {

    Result<List<LogtailCollectTopDTO>> collectTop();

    Result<List<LogtailCollectTrendDTO>> collectTrend(Long tailId);

    void collectTopCount();

    void collectTrendCount(Long tailId);

    void collectLogCount(String thisDay) throws IOException;

    boolean isLogtailCountDone(String day);

    void deleteHistoryLogCount();

    void collectLogDelete(String day);

    void collectTrendRefresh();

    void showLogCountCache();

    Result<List<SpaceCollectTopDTO>> collectSpaceTop();

    void collectSpaceTopCount();

    void collectSpaceTrend();

    Result<List<SpaceCollectTrendDTO>> spaceCollectTrend(Long spaceId);

}
