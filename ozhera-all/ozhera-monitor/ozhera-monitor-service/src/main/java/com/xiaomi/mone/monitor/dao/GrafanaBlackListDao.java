package com.xiaomi.mone.monitor.dao;

import com.xiaomi.mone.monitor.dao.mapper.AppGrafanaBlackListMapper;
import com.xiaomi.mone.monitor.dao.model.AppGrafanaBlackList;
import com.xiaomi.mone.monitor.dao.model.AppGrafanaBlackListExample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Repository
public class GrafanaBlackListDao {

    @Resource
    private AppGrafanaBlackListMapper appGrafanaBlackListMapper;

    public Integer createBlackList(String serverName) {
        AppGrafanaBlackList appGrafanaBlackList = new AppGrafanaBlackList();
        try {
            appGrafanaBlackList.setServerName(serverName);
            int insertRes = appGrafanaBlackListMapper.insert(appGrafanaBlackList);
            log.info("GrafanaBlackListDao.createBlackList res:{}", insertRes);
            return insertRes;
        } catch (Exception e) {
            log.error("GrafanaBlackListDao.createBlackList error :{}", e.getMessage());
            return -1;
        }
    }

    public AppGrafanaBlackList getBlackList(String serverName) {
        AppGrafanaBlackListExample aje = new AppGrafanaBlackListExample();
        aje.createCriteria().andServerNameEqualTo(serverName);
        List<AppGrafanaBlackList> appGrafanaBlackLists = appGrafanaBlackListMapper.selectByExample(aje);
        if (appGrafanaBlackLists.size() >= 1) {
            log.info("GrafanaBlackListDao.getBlackList res:{}", appGrafanaBlackLists.get(0));
            return appGrafanaBlackLists.get(0);
        }
        return null;
    }

    public List<AppGrafanaBlackList> getBlackListByServerName(String serverName) {
        AppGrafanaBlackListExample aje = new AppGrafanaBlackListExample();
        aje.createCriteria().andServerNameEqualTo(serverName);
        List<AppGrafanaBlackList> appGrafanaBlackLists = appGrafanaBlackListMapper.selectByExample(aje);
        return appGrafanaBlackLists;
    }

    public Integer delBlackList(Integer id) {
        try {
            int res = appGrafanaBlackListMapper.deleteByPrimaryKey(id);
            log.info("GrafanaBlackListDao.delBlackList res:{}", res);
            return res;
        } catch (Exception e) {
            log.error("GrafanaBlackListDao.delBlackList error :{}", e.getMessage());
            return -1;
        }
    }

    public Integer delBlackListByServerName(String serverName) {
        try {
            AppGrafanaBlackListExample aje = new AppGrafanaBlackListExample();
            aje.createCriteria().andServerNameEqualTo(serverName);
            int res = appGrafanaBlackListMapper.deleteByExample(aje);
            log.info("GrafanaBlackListDao.delBlackList res:{}", res);
            return res;
        } catch (Exception e) {
            log.error("GrafanaBlackListDao.delBlackList error :{}", e.getMessage());
            return -1;
        }
    }

    public long getTotalBlackList() {
        AppGrafanaBlackListExample aje = new AppGrafanaBlackListExample();

        long total = appGrafanaBlackListMapper.countByExample(aje);
        log.info("GrafanaBlackListDao.getTotalBlackList total:{}", total);
        return total;
    }

    public List<AppGrafanaBlackList> getAllBlackList(Integer pageNo, Integer pageSize) {
        AppGrafanaBlackListExample aje = new AppGrafanaBlackListExample();
        aje.setOrderByClause("id desc");
        aje.setLimit(pageSize);
        aje.setOffset((pageNo - 1) * pageSize);
        try {
            List<AppGrafanaBlackList> list = appGrafanaBlackListMapper.selectByExample(aje);
            if (list == null) {
                log.warn("GrafanaBlackListDao.getAllBlackList failed to search");
            }
            return list;
        } catch (Exception e) {
            log.error("GrafanaBlackListDao.getAllBlackList failed to search err: {}", e.toString());
            return null;
        }
    }

}
