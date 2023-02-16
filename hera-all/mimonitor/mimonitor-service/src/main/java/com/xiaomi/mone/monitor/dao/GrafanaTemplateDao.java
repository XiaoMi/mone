package com.xiaomi.mone.monitor.dao;

import com.xiaomi.mone.monitor.dao.mapper.AppScrapeJobMapper;
import com.xiaomi.mone.monitor.dao.mapper.GrafanaTemplateMapper;
import com.xiaomi.mone.monitor.dao.model.AppScrapeJob;
import com.xiaomi.mone.monitor.dao.model.AppScrapeJobExample;
import com.xiaomi.mone.monitor.dao.model.GrafanaTemplate;
import com.xiaomi.mone.monitor.dao.model.GrafanaTemplateExample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class GrafanaTemplateDao {
    @Autowired
    private GrafanaTemplateMapper grafanaTemplateMapper;

    public int insert(GrafanaTemplate template) {
        template.setCreateTime(new Date());
        template.setUpdateTime(new Date());
        try {
            int result = grafanaTemplateMapper.insert(template);
            if (result < 0) {
                log.warn("[GrafanaTemplateDao.insert] failed to insert GrafanaTemplateDao: {}", template.getName());
                return 0;
            }
        } catch (Exception e) {
            log.error("[GrafanaTemplateDao.insert] failed to insert GrafanaTemplateDao: {}, err: {}", template.getName(), e);
            return 0;
        }
        return 1;
    }

    public int update(GrafanaTemplate template) {
        template.setUpdateTime(new Date());
        try {
            int result = grafanaTemplateMapper.updateByPrimaryKeyWithBLOBs(template);
            if (result < 0) {
                log.warn("[GrafanaTemplateDao.update] failed to update GrafanaTemplateDao: {}", template.getName());
                return 0;
            }
        }catch (Exception e) {
            log.error("[GrafanaTemplateDao.update] failed to update GrafanaTemplateDao: {}, err: {}", template.getName(), e);
            return 0;
        }
        return 1;
    }

    public GrafanaTemplate fetchOneByName(String name) {
        try {
            GrafanaTemplateExample aje = new GrafanaTemplateExample();
            aje.setOrderByClause("id desc");
            aje.createCriteria().andNameEqualTo(name).andDeletedEqualTo(false);
            List<GrafanaTemplate> grafanaTemplates = grafanaTemplateMapper.selectByExampleWithBLOBs(aje);
            if (grafanaTemplates.size() >= 1) {
                return grafanaTemplates.get(0);
            }
            return null;
        } catch (Exception e) {
            log.error("[GrafanaTemplateDao.fetchOneByName] failed to fetchOneByName name: {}, err: {}", name, e);
            return null;
        }
    }

    public GrafanaTemplate fetchById(int id) {
        try {
            GrafanaTemplateExample aje = new GrafanaTemplateExample();
            aje.setOrderByClause("id desc");
            aje.createCriteria().andIdEqualTo(id).andDeletedEqualTo(false);
            List<GrafanaTemplate> grafanaTemplates = grafanaTemplateMapper.selectByExampleWithBLOBs(aje);
            if (grafanaTemplates.size() >= 1) {
                return grafanaTemplates.get(0);
            }
            return null;
        } catch (Exception e) {
            log.error("[GrafanaTemplateDao.fetchById] failed to fetchById id: {}, err: {}", id, e);
            return null;
        }
    }

    public int delete(int id) {
        try {
            //软删除
            GrafanaTemplate grafanaTemplate = grafanaTemplateMapper.selectByPrimaryKey(id);
            grafanaTemplate.setDeleted(true);
            int result = grafanaTemplateMapper.updateByPrimaryKeyWithBLOBs(grafanaTemplate);
            if (result < 0) {
                log.warn("[GrafanaTemplateDao.delete] failed to delete id: {}", id);
                return 0;
            }
            return result;
        }catch (Exception e) {
            log.error("[GrafanaTemplateDao.delete] failed to delete id: {}, err: {}", id, e);
            return 0;
        }
    }

    public List<GrafanaTemplate> list(int pageSize,int pageNo) {
        GrafanaTemplateExample aje = new GrafanaTemplateExample();
        aje.setOrderByClause("id desc");
        aje.setLimit(pageSize);
        aje.setOffset((pageNo-1) * pageSize);
        aje.createCriteria().andDeletedEqualTo(false);
        try {
            List<GrafanaTemplate> list = grafanaTemplateMapper.selectByExample(aje);
            if (list == null) {
                log.warn("[GrafanaTemplateDao.list] failed to search");
            }
            return list;
        }catch (Exception e) {
            log.error("[GrafanaTemplateDao.list] failed to search err: {}",e.toString());
            return null;
        }
    }

    public Long getTotal() {
        GrafanaTemplateExample aje = new GrafanaTemplateExample();
        aje.createCriteria().andDeletedEqualTo(false);
        try {
            Long result = grafanaTemplateMapper.countByExample(aje);
            if (result == null) {
                log.warn("[GrafanaTemplateDao.getTotal] failed to search");
            }
            return result;
        }catch (Exception e) {
            log.error("[GrafanaTemplateDao.getTotal] failed to search err: {}",e.toString());
        }
        return null;
    }

    public List<GrafanaTemplate> search(GrafanaTemplate template){

        GrafanaTemplateExample aje = new GrafanaTemplateExample();
        GrafanaTemplateExample.Criteria ca = aje.createCriteria();
        if(template.getAppType() != null){
            ca.andAppTypeEqualTo(template.getAppType());
        }

        if (template.getLanguage() != null){
            ca.andLanguageEqualTo(template.getLanguage());
        }

        if (template.getPlatform() != null){
            ca.andPlatformEqualTo(template.getPlatform());
        }
        ca.andDeletedEqualTo(false);
        return grafanaTemplateMapper.selectByExampleWithBLOBs(aje);

    }
}
