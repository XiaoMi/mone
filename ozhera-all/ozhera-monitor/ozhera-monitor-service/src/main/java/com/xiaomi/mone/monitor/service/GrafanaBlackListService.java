package com.xiaomi.mone.monitor.service;

import com.xiaomi.mone.monitor.bo.GrafanaBlackListParam;
import com.xiaomi.mone.monitor.dao.GrafanaBlackListDao;
import com.xiaomi.mone.monitor.dao.model.AppGrafanaBlackList;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.model.PageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GrafanaBlackListService {

    @Autowired
    GrafanaBlackListDao grafanaBlackListDao;

    public Result createBlackList(String serverName) {
        //插库
        List<AppGrafanaBlackList> blackListByServerName = grafanaBlackListDao.getBlackListByServerName(serverName);
        if (blackListByServerName.size() >= 1) {
            return Result.fail(ErrorCode.REPEAT_ADD_PROJECT);
        }
        Integer blackList = grafanaBlackListDao.createBlackList(serverName);
        if (blackList == null || blackList == -1 || blackList == 0) {
            log.error("GrafanaBlackListService.createBlackList error");
            return Result.fail(ErrorCode.unknownError);
        }
        return Result.success(blackList);
    }

    public Result getBlackList(String serverName) {
        //查库
        AppGrafanaBlackList blackList = grafanaBlackListDao.getBlackList(serverName);
        return Result.success(blackList);
    }

    public Result delBlackList(String serverName) {
        //删除
        Integer res = grafanaBlackListDao.delBlackListByServerName(serverName);
        if (res == null || res == 0 || res == -1) {
            log.error("GrafanaBlackListService.delBlackList error");
            return Result.fail(ErrorCode.unknownError);
        }
        return Result.success(res);
    }

    public Result getBlackListList(Integer page,Integer pageSize) {
        //获取列表
        PageData pd = new PageData();
        pd.setPage(page);
        pd.setPageSize(pageSize);
        pd.setTotal(grafanaBlackListDao.getTotalBlackList());
        pd.setList(grafanaBlackListDao.getAllBlackList(page, pageSize));
        return Result.success(pd);
    }

    public boolean isInBlackList(String serverName) {
        List<AppGrafanaBlackList> blackListByServerName = grafanaBlackListDao.getBlackListByServerName(serverName);
        if (blackListByServerName.size() >= 1) {
            return true;
        }
        return false;
    }


}
