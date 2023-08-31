package com.xiaomi.mone.monitor.dao;

import com.xiaomi.mone.monitor.dao.mapper.AppServiceMarketMapper;
import com.xiaomi.mone.monitor.dao.model.AppScrapeJob;
import com.xiaomi.mone.monitor.dao.model.AppScrapeJobExample;
import com.xiaomi.mone.monitor.dao.model.AppServiceMarket;
import com.xiaomi.mone.monitor.dao.model.AppServiceMarketExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author zhangxiaowei6
 */

@Slf4j
@Repository
public class AppServiceMarketDao {
    @Autowired
    private AppServiceMarketMapper appServiceMarketMapper;

    //插入一条大盘数据
    public int insertServiceMarket(AppServiceMarket appServiceMarket) {
        appServiceMarket.setCreateTime(new Date());
        appServiceMarket.setUpdateTime(new Date());
        try {
            int result = appServiceMarketMapper.insert(appServiceMarket);
            if (result < 0) {
                log.warn("[AppServiceMarketDao.insert] failed to insert AppServiceMarketDao: {}", appServiceMarket.toString());
                return 0;
            }
        } catch (Exception e) {
            log.error("[AppServiceMarketDao.insert] failed to insert AppServiceMarketDao: {}, err: {}", appServiceMarket.toString(), e);
            return 0;
        }
        return 1;
    }

    //删除一条大盘数据
    public int deleteServiceMarket(Integer id) {
        try {
            int result = appServiceMarketMapper.deleteByPrimaryKey(id);
            if (result < 0) {
                log.warn("[AppServiceMarketDao.update] failed to delete AppServiceMarketDaoId: {}", id);
                return 0;
            }
            return result;
        } catch (Exception e) {
            log.error("[AppServiceMarketDao.update] failed to delete AppServiceMarketDaoId : {} err: {}", id, e);
            return 0;
        }
    }

    //更新一条大盘数据
    public int updateServiceMarket(AppServiceMarket appServiceMarket) {
        System.out.println("1233333333333333333333");
        appServiceMarket.setUpdateTime(new Date());
        try {
            System.out.println(appServiceMarket.getMarketName());
            int result = appServiceMarketMapper.updateByPrimaryKey(appServiceMarket);
            if (result < 0) {
                log.warn("[AppServiceMarketDao.update] failed to update AppServiceMarketDao: {}", appServiceMarket.toString());
                return 0;
            }
            return result;
        } catch (Exception e) {
            log.error("[AppServiceMarketDao.update] failed to update AppServiceMarketDao : {} err: {}", appServiceMarket.toString(), e);
            return 0;
        }
    }

    //查看一条大盘数据
    public AppServiceMarket SearchAppServiceMarket(Integer id) {
        try {
            AppServiceMarket result = appServiceMarketMapper.selectByPrimaryKey(id);
            if (result == null) {
                log.warn("[AppServiceMarketDao.search] failed to search AppServiceMarketDao id: {}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("[AppServiceMarketDao.search] failed to search err: {} ,id: {}", e, id);
            return null;
        }
    }

    //获取list
    public List<AppServiceMarket> SearchAppServiceMarketList(int pageNo,int pageSize,String creator,String marketName,String serviceName) {
        AppServiceMarketExample aje = new AppServiceMarketExample();
        aje.setOrderByClause("create_time desc");
        aje.setLimit(pageSize);
        aje.setOffset((pageNo-1) * pageSize);
        AppServiceMarketExample.Criteria ca  = aje.createCriteria();
        if (StringUtils.isNotEmpty(creator) ) {
            System.out.println(creator);
            ca.andCreatorLike("%" + creator + "%");
        }
        if (StringUtils.isNotEmpty(marketName) ) {
            ca.andMarketNameLike("%" + marketName + "%");
        }
        if (StringUtils.isNotEmpty(serviceName) ) {
            ca.andServiceListLike("%" + serviceName + "%");
        }

        try {
            List<AppServiceMarket> list = appServiceMarketMapper.selectByExample(aje);
            if (list == null) {
                log.warn("[AppServiceMarketDao.SearchAppServiceMarketList failed to search");
            }
            return list;
        }catch (Exception e) {
            log.error("[AppServiceMarketDao.SearchAppServiceMarketList failed to search err: {}",e.toString());
            return null;
        }
    }

    //获取总数
    public Long getTotal(String creator,String marketName,String serviceName) {
        AppServiceMarketExample aje = new AppServiceMarketExample();
        AppServiceMarketExample.Criteria ca  = aje.createCriteria();
        if (StringUtils.isNotEmpty(creator) ) {
            System.out.println(creator);
            ca.andCreatorLike("%" + creator + "%");
        }
        if (StringUtils.isNotEmpty(marketName) ) {
            ca.andMarketNameLike("%" + marketName + "%");
        }
        if (StringUtils.isNotEmpty(serviceName) ) {
            ca.andServiceListLike("%" + serviceName + "%");
        }
        try {
            Long result = appServiceMarketMapper.countByExample(aje);
            if (result == null) {
                log.warn("[AppServiceMarketDao.getTotal failed");
            }
            return result;
        }catch (Exception e) {
            log.error("[AppServiceMarketDao.getTotal failed err: {}",e.toString());
        }
        return null;
    }
}
