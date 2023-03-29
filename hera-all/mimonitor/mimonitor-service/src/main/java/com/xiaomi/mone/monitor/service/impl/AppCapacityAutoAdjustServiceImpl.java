package com.xiaomi.mone.monitor.service.impl;

import com.xiaomi.mone.monitor.dao.AppCapacityAutoAdjustDao;
import com.xiaomi.mone.monitor.dao.model.AppCapacityAutoAdjust;
import com.xiaomi.mone.monitor.service.AppCapacityAutoAdjustService;
import com.xiaomi.mone.monitor.service.GrafanaApiService;
import com.xiaomi.mone.monitor.service.bo.AppCapacityAutoAdjustBo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author gaoxihui
 * @date 2022/6/6 7:19 下午
 */

@Slf4j
//@Service(registry = "registryConfig",interfaceClass = AppCapacityAutoAdjustService.class, retries = 0,group = "${dubbo.group}")
public class AppCapacityAutoAdjustServiceImpl implements AppCapacityAutoAdjustService {

    @Autowired
    AppCapacityAutoAdjustDao appCapacityAutoAdjustDao;

    @Override
    public Boolean createOrUpData(AppCapacityAutoAdjustBo appCapacityAutoAdjustBo) {

        log.info("AppCapacityAutoAdjustDao#createOrUpData appCapacityAutoAdjustBo:{}",appCapacityAutoAdjustBo.toString());

        if(appCapacityAutoAdjustBo.getAppId() == null
                || appCapacityAutoAdjustBo.getPipelineId() == null
                || appCapacityAutoAdjustBo.getAutoCapacity() == null
                || appCapacityAutoAdjustBo.getMaxInstance() == null
                || StringUtils.isEmpty(appCapacityAutoAdjustBo.getContainer()) ){
            log.error("AppCapacityAutoAdjustDao#createOrUpData invalid param,appCapacityAutoAdjustBo:{}",appCapacityAutoAdjustBo.toString());
            return false;
        }

        /**
         * 这里maxInstance计算规则是：miline配置的副本数的2倍；
         * 为了保证数据一致性，miline不做*2处理，直接传递miline配置的副本数的原始值；
         */
        appCapacityAutoAdjustBo.setMaxInstance(appCapacityAutoAdjustBo.getMaxInstance() * 2);

        AppCapacityAutoAdjust query = new AppCapacityAutoAdjust();
        query.setAppId(appCapacityAutoAdjustBo.getAppId());
        query.setPipelineId(appCapacityAutoAdjustBo.getPipelineId());
        List<AppCapacityAutoAdjust> result = appCapacityAutoAdjustDao.query(query,null,null);

        if(CollectionUtils.isEmpty(result)){
            AppCapacityAutoAdjust insert = new AppCapacityAutoAdjust();
            BeanUtils.copyProperties(appCapacityAutoAdjustBo,insert);
            int i = appCapacityAutoAdjustDao.create(insert);
            if(i < 1){
                log.error("AppCapacityAutoAdjustDao#create new data fail,appCapacityAutoAdjustBo:{}",appCapacityAutoAdjustBo.toString());
                return false;
            }

            return true;
        }

        if(result.size() > 1){
            log.error("AppCapacityAutoAdjustDao#createOrUpData duplicate record found! appCapacityAutoAdjustBo:{}",appCapacityAutoAdjustBo.toString());
            return false;
        }

        AppCapacityAutoAdjust appCapacityAutoAdjust = result.get(0);
        BeanUtils.copyProperties(appCapacityAutoAdjustBo,appCapacityAutoAdjust);
        int update = appCapacityAutoAdjustDao.update(appCapacityAutoAdjust);
        if(update < 1){
            log.error("AppCapacityAutoAdjustDao# update data fail,appCapacityAutoAdjustBo:{}",appCapacityAutoAdjustBo.toString());
            return false;
        }

        log.info("AppCapacityAutoAdjustDao#createOrUpData success! appCapacityAutoAdjustBo:{}",appCapacityAutoAdjustBo.toString());

        return true;
    }
}
