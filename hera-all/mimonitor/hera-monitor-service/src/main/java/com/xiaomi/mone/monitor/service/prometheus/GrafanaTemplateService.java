package com.xiaomi.mone.monitor.service.prometheus;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.dao.GrafanaTemplateDao;
import com.xiaomi.mone.monitor.dao.model.GrafanaTemplate;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.model.PageData;
import com.xiaomi.mone.monitor.service.model.prometheus.CreateTemplateParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author zhangxiaowei6
 * @date 2022/3/30
 */
@Slf4j
@Service
public class GrafanaTemplateService {

    @Autowired
    GrafanaTemplateDao grafanaTemplateDao;

    private final Gson gson = new Gson();

    public Result createGrafanaTemplate(CreateTemplateParam param) {
        //检验template json合法性
      /*  String checkResult = checkTemplateJson(param.getTemplate());
        if (!"ok".equals(checkResult)) {
            log.error("GrafanaTemplateService.createGrafanaTemplate request templateJson invalid {}",checkResult);
            return Result.fail(ErrorCode.invalidParamError);
        }*/
        //查询该模板是否已经存在
        if (grafanaTemplateDao.fetchOneByName(param.getName()) != null) {
            return Result.fail(ErrorCode.REPEAT_ADD_PROJECT);
        }
        GrafanaTemplate template = new GrafanaTemplate();
        template.setTemplate(param.getTemplate());
        template.setAppType(param.getAppType());
        template.setLanguage(param.getLanguage());
        template.setPlatform(param.getPlatform());
        template.setName(param.getName());
        template.setUrlParam(param.getUrlParam());
        template.setPanelIdList(param.getPanelIdList().trim());
        template.setDeleted(false);
        int dbResult = grafanaTemplateDao.insert(template);
        log.info("GrafanaTemplateService.createGrafanaTemplate request : {},dbResult: {}", param.getName(), dbResult);
        return Result.success(dbResult);
    }

    public Result deleteGrafanaTemplate(int id) {
        //先查找是否数据存在
        if (grafanaTemplateDao.fetchById(id) == null) {
            return Result.fail(ErrorCode.NO_DATA_FOUND);
        }
        int result = grafanaTemplateDao.delete(id);
        log.info("GrafanaTemplateService.deleteGrafanaTemplate id : {},dbResult: {}", id, result);
        return Result.success(result);
    }

    public Result getGrafanaTemplate(int id) {
        GrafanaTemplate grafanaTemplate = grafanaTemplateDao.fetchById(id);
        log.info("GrafanaTemplateService.getGrafanaTemplate id : {}", id);
        return Result.success(grafanaTemplate);
    }

    public Result listGrafanaTemplate(int pageSize,int pageNo) {
        PageData pd = new PageData();
        pd.setPage(pageNo);
        pd.setPageSize(pageSize);
        pd.setTotal(grafanaTemplateDao.getTotal());
        pd.setList(grafanaTemplateDao.list(pageSize,pageNo));
        log.info("GrafanaTemplateService.listGrafanaTemplate pageSize:{} pageNo: {}", pageSize, pageNo);
        return Result.success(pd);
    }

    public Result updateGrafanaTemplate(CreateTemplateParam param) {
        //检验template json合法性
        /*String checkResult = checkTemplateJson(param.getTemplate());
        if (!"ok".equals(checkResult)) {
            log.error("GrafanaTemplateService.updateGrafanaTemplate request templateJson invalid {}",checkResult);
            return Result.fail(ErrorCode.invalidParamError);
        }*/
        //查询该模板是否存在，不存在报错
        GrafanaTemplate grafanaTemplate = grafanaTemplateDao.fetchById(param.getId().intValue());
        if (grafanaTemplate == null) {
            return Result.fail(ErrorCode.NO_DATA_FOUND);
        }
        grafanaTemplate.setTemplate(param.getTemplate());
        grafanaTemplate.setAppType(param.getAppType());
        grafanaTemplate.setLanguage(param.getLanguage());
        grafanaTemplate.setPlatform(param.getPlatform());
        grafanaTemplate.setName(param.getName());
        grafanaTemplate.setUrlParam(param.getUrlParam());
        grafanaTemplate.setId(param.getId().intValue());
        grafanaTemplate.setPanelIdList(param.getPanelIdList().trim());
        int dbResult = grafanaTemplateDao.update(grafanaTemplate);
        log.info("GrafanaTemplateService.updateGrafanaTemplate request : {},dbResult: {}", param, dbResult);
        return Result.success(dbResult);
    }

    //检验传入的prometheus job json的合法性
    private String checkTemplateJson(String jobJson) {
        //模板中必须包含 panels,uid ,version
        try {
            JsonObject jsonObject = gson.fromJson(jobJson, JsonObject.class);
            String panels = jsonObject.get("panels").getAsString();
            String uid = jsonObject.get("uid").getAsString();
            String version = jsonObject.get("version").getAsString();
            if (StringUtils.isEmpty(panels) || StringUtils.isEmpty(uid) || StringUtils.isEmpty(version)) {
                return "Missing some request parameters";
            }
            return "ok";
        }catch (Exception e) {
            String errStr = "GrafanaTemplateService templateJson not right, error is: " + e;
            log.error(errStr);
            return errStr;
        }
    }
}
