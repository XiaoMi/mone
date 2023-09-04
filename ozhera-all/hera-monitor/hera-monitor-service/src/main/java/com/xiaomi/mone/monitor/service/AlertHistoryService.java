package com.xiaomi.mone.monitor.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.bo.AlertHistory;
import com.xiaomi.mone.monitor.bo.AlertHistoryDetailed;
import com.xiaomi.mone.monitor.bo.AlertHistoryParam;
import com.xiaomi.mone.monitor.dao.AppMonitorDao;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.helper.AlertHelper;
import com.xiaomi.mone.monitor.service.model.PageData;
import com.xiaomi.mone.monitor.service.prometheus.AlarmService;
import com.xiaomi.mone.monitor.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author zhanggaofeng1
 */
@Slf4j
@Service
public class AlertHistoryService {

    @Autowired
    private AlarmService alarmService;
    @Autowired
    private AlertHelper alertHelper;
    @Autowired
    private AppMonitorDao appMonitorDao;

    public Result<PageData<List<AlertHistory>>> metricList(String user, AlertHistoryParam param) {
        PageData<List<AlertHistory>> pageData = new PageData();
        pageData.setPage(param.getPage());
        pageData.setPageSize(param.getPageSize());
        pageData.setTotal(0L);
        Set<Integer> treeIdSet = null;
        if (param.getIamTreeId() != null) {
            treeIdSet = new HashSet<>();
            treeIdSet.add(param.getIamTreeId());
        } else {
            treeIdSet = appMonitorDao.selectTreeIdByOwnerOrCareUser(user);
        }
        if (CollectionUtils.isEmpty(treeIdSet)) {
            return Result.success(pageData);
        }
        JsonObject labels = alertHelper.buildLabels(param);
        Long startTime = CommonUtil.toSeconds(param.getStartTime());
        Long endTime = CommonUtil.toSeconds(param.getEndTime());
        Result<PageData> pageResult = alarmService.queryLatestEvents(treeIdSet, param.getAlertStat(), param.getAlertLevel(), startTime, endTime, param.getPage(), param.getPageSize(), labels);
        if (pageResult.getData() != null) {
            pageData.setTotal(pageResult.getData().getTotal());
            pageData.setList(alertHelper.buildAlertHistoryList((JsonElement) pageResult.getData().getList()));
        }
        return Result.success(pageData);
    }

    public Result<AlertHistoryDetailed> metricDetailed(String user, AlertHistoryParam param) {
        Result<JsonObject> dataResult = alarmService.getEventById(user, param.getIamTreeId(), param.getId());
        if (dataResult == null || dataResult.getData() == null) {
            return Result.fail(ErrorCode.ALERT_NOT_FOUND);
        }
        return Result.success(alertHelper.buildAlertHistoryDetailed(dataResult.getData()));
    }

    public Result metricResolved(String user, AlertHistoryParam param) {
        Long startTime = CommonUtil.toSeconds(param.getStartTime());
        Long endTime = CommonUtil.toSeconds(param.getEndTime());
        Result<JsonObject> dataResult = alarmService.resolvedEvent(user, param.getIamTreeId(), param.getAlertName(), param.getComment(), startTime, endTime);
        if (dataResult == null || dataResult.getCode() != ErrorCode.success.getCode()) {
            return Result.fail(ErrorCode.unknownError);
        }
        return Result.success(null);
    }

}
