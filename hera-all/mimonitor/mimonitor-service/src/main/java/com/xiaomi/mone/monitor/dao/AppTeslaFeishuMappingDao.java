package com.xiaomi.mone.monitor.dao;

import com.xiaomi.mone.monitor.dao.mapper.AppTeslaFeishuMappingMapper;
import com.xiaomi.mone.monitor.dao.model.AppTeslaFeishuMapping;
import com.xiaomi.mone.monitor.dao.model.AppTeslaFeishuMappingExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author gaoxihui
 * @date 2021/11/19 5:39 下午
 */
@Slf4j
@Repository
public class AppTeslaFeishuMappingDao {

    @Autowired
    AppTeslaFeishuMappingMapper teslaFeishuMappingMapper;

    public Long count(String teslaGroup,String feishuGid,String remark){
        AppTeslaFeishuMappingExample example = new AppTeslaFeishuMappingExample();
        AppTeslaFeishuMappingExample.Criteria ca = example.createCriteria();
        ca.andStatusEqualTo(0);

        if (StringUtils.isNotBlank(teslaGroup)){
            ca.andTeslaGroupLike("%" + teslaGroup + "%");
        }

        if (StringUtils.isNotBlank(feishuGid)){
            ca.andFeishuGroupIdLike("%" + feishuGid + "%");
        }

        if (StringUtils.isNotBlank(remark)){
            ca.andRemarkLike("%" + remark+ "%");
        }

        return teslaFeishuMappingMapper.countByExample(example);
    }


    public List<AppTeslaFeishuMapping> list(String teslaGroup,String feishuGid,String remark, Integer page, Integer pageSize){

        AppTeslaFeishuMappingExample example = new AppTeslaFeishuMappingExample();

        example.setOffset((page-1) * pageSize);
        example.setLimit(pageSize);
        example.setOrderByClause("id desc");

        AppTeslaFeishuMappingExample.Criteria ca = example.createCriteria();

        ca.andStatusEqualTo(0);

        if (StringUtils.isNotBlank(teslaGroup)){
            ca.andTeslaGroupLike("%" + teslaGroup + "%");
        }

        if (StringUtils.isNotBlank(feishuGid)){
            ca.andFeishuGroupIdLike("%" + feishuGid + "%");
        }

        if (StringUtils.isNotBlank(remark)){
            ca.andRemarkLike("%" + remark+ "%");
        }

        return teslaFeishuMappingMapper.selectByExample(example);

    }

    public int create(AppTeslaFeishuMapping appTeslaFeishuMapping) {
        if (null == appTeslaFeishuMapping) {
            log.error("[AppTeslaFeishuMappingDao.create] null appTeslaFeishuMapping");
            return 0;
        }

        appTeslaFeishuMapping.setCreateTime(new Date());
        appTeslaFeishuMapping.setUpdateTime(new Date());
        appTeslaFeishuMapping.setStatus(0);

        try {
            int affected = teslaFeishuMappingMapper.insert(appTeslaFeishuMapping);
            if (affected < 1) {
                log.warn("[AppTeslaFeishuMappingDao.create] failed to insert appTeslaFeishuMapping: {}", appTeslaFeishuMapping.toString());
                return 0;
            }
        } catch (Exception e) {
            log.error("[AppTeslaFeishuMappingDao.create] failed to insert appTeslaFeishuMapping: {}, err: {}", appTeslaFeishuMapping.toString(), e);
            return 0;
        }
        return 1;
    }

    public int update(AppTeslaFeishuMapping appTeslaFeishuMapping) {
        if (null == appTeslaFeishuMapping) {
            log.error("[AppTeslaFeishuMappingDao.update] null appTeslaFeishuMapping");
            return 0;
        }

        appTeslaFeishuMapping.setUpdateTime(new Date());

        try {
            int affected = teslaFeishuMappingMapper.updateByPrimaryKey(appTeslaFeishuMapping);
            if (affected < 1) {
                log.warn("[AppTeslaFeishuMappingDao.update] failed to update appTeslaFeishuMapping: {}", appTeslaFeishuMapping.toString());
                return 0;
            }
        } catch (Exception e) {
            log.error("[AppTeslaFeishuMappingDao.update] failed to update appTeslaAlarmRule: {}, err: {}", appTeslaFeishuMapping.toString(), e);
            return 0;
        }
        return 1;
    }

    public int delete(Integer id) {

        int affected = 0;

        if (null == id) {
            log.error("[AppTeslaFeishuMappingDao.delete] null id");
            return affected;
        }

        try {
            affected = teslaFeishuMappingMapper.deleteByPrimaryKey(id);
            if (affected < 1) {
                log.warn("[AppTeslaFeishuMappingDao.delete] failed to delete id: {}", id);
                return affected;
            }
        } catch (Exception e) {
            log.error("[AppTeslaFeishuMappingDao.delete] failed to delete id: {}, err: {}", id, e);
            return 0;
        }
        return affected;
    }

}
