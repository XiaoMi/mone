package com.xiaomi.mone.monitor.service.helper;

import com.xiaomi.mone.log.api.model.dto.MontorAppDTO;
import com.xiaomi.mone.log.api.service.MilogOpenService;
import com.xiaomi.mone.monitor.bo.PlatForm;
import com.xiaomi.mone.monitor.dao.AppMonitorDao;
import com.xiaomi.mone.monitor.dao.model.AppMonitor;
import com.xiaomi.mone.monitor.service.extension.PlatFormTypeExtensionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @project: mimonitor
 * @author: zgf1
 * @date: 2021/12/9 10:30
 */
@Slf4j
@Component
public class ProjectHelper {

    @Reference(check = false, interfaceClass = MilogOpenService.class, group = "${dubbo.group}")
    private MilogOpenService milogOpenService;
    @Autowired
    private AppMonitorDao appMonitorDao;

    @Autowired
    private PlatFormTypeExtensionService platFormTypeExtensionService;

    /**
     * 是否入住日志系统
     *
     * @param projectId
     * @return
     */
    public boolean accessLogSys(String projectName, Long projectId, Integer appSource) {
        log.info("查询详情是否入住日志系统请求 projectName={}, projectId={}", projectName, projectId);
        if (StringUtils.isBlank(projectName) || projectId == null) {
            return true;
        }

        if (platFormTypeExtensionService.belongPlatForm(appSource, PlatForm.miCloud)) {
            return true;
        }

        AppMonitor app = appMonitorDao.getByAppIdAndName(projectId.intValue(), projectName);
        if (app == null) {
            log.info("查询详情是否入住日志系统请求，没有app信息; projectId={}, projectName={}", projectId, projectName);
            return true;
        }
        try {
            MontorAppDTO result = milogOpenService.queryHaveAccessMilog(app.getIamTreeId().longValue(), String.valueOf(projectId), appSource);
            log.info("查询详情是否入住日志系统请求iamId:{},projectId:{},appSource:{},响应 result={}", app.getIamTreeId(), projectId, appSource, result);
            if (result == null) {
                return false;
            }
            return result.getIsAccess();
        } catch (Exception e) {
            log.error("查询项目是否入住日志系统异常; app={}", app, e);
            return true;
        }
    }

}
