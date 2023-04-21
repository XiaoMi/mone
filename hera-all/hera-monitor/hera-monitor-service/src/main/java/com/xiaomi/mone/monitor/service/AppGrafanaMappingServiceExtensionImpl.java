package com.xiaomi.mone.monitor.service;

import com.xiaomi.mone.monitor.dao.model.GrafanaTemplate;
import com.xiaomi.mone.monitor.service.api.AppGrafanaMappingServiceExtension;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/4/21 11:32 AM
 */
@Service
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class AppGrafanaMappingServiceExtensionImpl implements AppGrafanaMappingServiceExtension {
    @Override
    public void setPlatFormByLanguage(GrafanaTemplate template, String appLanguage) {

    }

    @Override
    public void dealRequestGrafanaTemplateCode(Integer code, String bindId, String appName) {

    }
}
