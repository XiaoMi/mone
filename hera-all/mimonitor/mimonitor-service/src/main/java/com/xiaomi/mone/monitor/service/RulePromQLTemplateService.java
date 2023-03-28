/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xiaomi.mone.monitor.service;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.mone.monitor.bo.RulePromQLTemplateInfo;
import com.xiaomi.mone.monitor.bo.RulePromQLTemplateParam;
import com.xiaomi.mone.monitor.dao.RulePromQLTemplateDao;
import com.xiaomi.mone.monitor.dao.model.RulePromQLTemplate;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.model.PageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 *
 * @author zhanggaofeng1
 */
@Slf4j
@Service
public class RulePromQLTemplateService {

    @Autowired
    private RulePromQLTemplateDao rulePromQLTemplateDao;

    @NacosValue(value = "${prometheus.url}",autoRefreshed = true)
    private String prometheusUrl;
    @NacosValue(value = "${prometheus.check.url}",autoRefreshed = true)
    private String prometheusCheckUrl;

    /**
     * 添加模板
     * @param user
     * @param param
     * @return
     */
    public Result add(String user, RulePromQLTemplateParam param) {
        List<RulePromQLTemplate> templates = rulePromQLTemplateDao.getByName(user, param.getName());
        if (!CollectionUtils.isEmpty(templates)) {
            Result result = Result.fail(ErrorCode.invalidParamError);
            result.setMessage("名称不允许重复");
            return result;
        }
        RulePromQLTemplate template = new RulePromQLTemplate();
        BeanUtils.copyProperties(param, template);
        template.setCreater(user);
        if (!rulePromQLTemplateDao.insert(template)) {
            return Result.fail(ErrorCode.unknownError);
        }
        log.info("add PromQL template 成功：user={}, template={}",user, template);
        return Result.success(null);
    }

    /**
     * 编辑模板
     * @param user
     * @param param
     * @return
     */
    public Result edit(String user, RulePromQLTemplateParam param) {
        List<RulePromQLTemplate> templates = rulePromQLTemplateDao.getByName(user, param.getName());
        if (templates != null && templates.size() > 1) {
            Result result = Result.fail(ErrorCode.invalidParamError);
            result.setMessage("名称不允许重复");
            return result;
        }
        RulePromQLTemplate template = new RulePromQLTemplate();
        BeanUtils.copyProperties(param, template);
        template.setCreater(null);
        if (!rulePromQLTemplateDao.updateById(template)) {
            return Result.fail(ErrorCode.unknownError);
        }
        log.info("updateById PromQL template 成功：user={}, template={}",user, template);
        return Result.success(null);
    }


    public Result deleteById(String user, Integer templateId) {
        if (!rulePromQLTemplateDao.deleteById(templateId)) {
            return Result.fail(ErrorCode.unknownError);
        }
        log.info("deleteById PromQL template 成功：user={}, templateId={}",user, templateId);
        return Result.success(null);
    }

    public Result<PageData<List<RulePromQLTemplateInfo>>> search(String user, RulePromQLTemplateParam param) {
        RulePromQLTemplate template = new RulePromQLTemplate();
        PageData<List<RulePromQLTemplateInfo>> pageData = rulePromQLTemplateDao.searchByCond(user, param);
        log.info("query promQL template user={}, param={}, pageData={}", user, param, pageData);
        return Result.success(pageData);
    }

    public Result<String> testPromQL(String user, RulePromQLTemplateParam param) throws UnsupportedEncodingException {
        StringBuilder url = new StringBuilder();
        url.append(prometheusCheckUrl).append("graph?g0.expr=").append(URLEncoder.encode(param.getPromql(), "UTF-8")).append("&g0.tab=1&g0.stacked=0&g0.range_input=1h");
        return Result.success(url.toString());
    }

}
