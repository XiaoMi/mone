package com.xiaomi.mone.monitor.service.helper;

import com.xiaomi.mone.log.api.model.dto.MontorAppDTO;
import com.xiaomi.mone.log.api.service.MilogOpenService;
import com.xiaomi.mone.monitor.bo.PlatForm;
import com.xiaomi.mone.monitor.bo.PlatFormType;
import com.xiaomi.mone.monitor.dao.AppMonitorDao;
import com.xiaomi.mone.monitor.dao.model.AppMonitor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;

import org.apache.commons.lang3.StringUtils;
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

    /**
     * 是否入住日志系统
     * @param projectId
     * @return
     */
    public boolean accessLogSys(String projectName, Long projectId, Integer appSource) {
        log.info("查询详情是否入住日志系统请求 projectName={}, projectId={}", projectName, projectId);
        //todo，查询日志系统
        return true;
    }

}
