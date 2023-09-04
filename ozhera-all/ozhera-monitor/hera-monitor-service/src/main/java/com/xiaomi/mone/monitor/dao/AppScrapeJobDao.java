package com.xiaomi.mone.monitor.dao;

import com.xiaomi.mone.monitor.dao.mapper.AlertManagerRulesMapper;
import com.xiaomi.mone.monitor.dao.mapper.AppScrapeJobMapper;
import com.xiaomi.mone.monitor.dao.model.AlertManagerRules;
import com.xiaomi.mone.monitor.dao.model.AppScrapeJob;
import com.xiaomi.mone.monitor.dao.model.AppScrapeJobExample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author zhangxiaowei6
 */

@Slf4j
@Repository
public class AppScrapeJobDao {
    @Autowired
    private AppScrapeJobMapper appScrapeJobMapper;

    //插入一条job
    public int insertScrapeJob(AppScrapeJob appScrapeJob) {
        appScrapeJob.setCreateTime(new Date());
        appScrapeJob.setUpdateTime(new Date());
        try {
            int result = appScrapeJobMapper.insert(appScrapeJob);
            if (result < 0) {
                log.warn("[AppScrapeJobDao.insert] failed to insert AppScrapeJobDao: {}", appScrapeJob.toString());
                return 0;
            }
        } catch (Exception e) {
            log.error("[AppScrapeJobDao.insert] failed to insert AppScrapeJobDao: {}, err: {}", appScrapeJob.toString(), e);
            return 0;
        }
        return 1;
    }

    //查询一条job
    public AppScrapeJob searchScrapeJob(Integer id) {
        try {
            AppScrapeJob result = appScrapeJobMapper.selectByPrimaryKey(id);
            if (result == null) {
                log.warn("[AppScrapeJobDao.search] failed to search AppScrapeJobDao id: {}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("[AppScrapeJobDao.search] failed to search err: {} ,id: {}", e, id);
            return null;
        }
    }

    //获取job list
    public List<AppScrapeJob> searchScrapeJobList(int pageSize,int pageNo) {
        AppScrapeJobExample aje = new AppScrapeJobExample();
        aje.setOrderByClause("id desc");
        aje.setLimit(pageSize);
        aje.setOffset((pageNo-1) * pageSize);
        aje.createCriteria().andStatusEqualTo((byte) 1);
        try {
            List<AppScrapeJob> list = appScrapeJobMapper.selectByExampleWithBLOBs(aje);
            if (list == null) {
                log.warn("[AppScrapeJobDao.searchScrapeJobList] failed to search");
            }
            return list;
        }catch (Exception e) {
            log.error("[AppScrapeJobDao.searchScrapeJobList] failed to search err: {}",e.toString());
            return null;
        }
    }

    //获取成功总数
    public Long getJobSuccessTotal() {
        AppScrapeJobExample aje = new AppScrapeJobExample();
        aje.createCriteria().andStatusEqualTo((byte) 1);
        try {
            Long result = appScrapeJobMapper.countByExample(aje);
            if (result == null) {
                log.warn("[AppScrapeJobDao.searchScrapeJobList] failed to search");
            }
            return result;
        }catch (Exception e) {
            log.error("[AppScrapeJobDao.getJobSuccessTotal] failed to search err: {}",e.toString());
        }
        return null;
    }

    //更新一条job
    public int updateScrapeJob(AppScrapeJob appScrapeJob) {
        try {
            appScrapeJob.setUpdateTime(new Date());
            int result = appScrapeJobMapper.updateByPrimaryKeyWithBLOBs(appScrapeJob);
            if (result < 0) {
                log.warn("[AppScrapeJobDao.update] failed to update AppScrapeJob: {}", appScrapeJob);
                return 0;
            }
            return result;
        } catch (Exception e) {
            log.error("[AppScrapeJobDao.update] failed to update AppScrapeJob : {} err: {}", appScrapeJob, e);
            return 0;
        }
    }
}