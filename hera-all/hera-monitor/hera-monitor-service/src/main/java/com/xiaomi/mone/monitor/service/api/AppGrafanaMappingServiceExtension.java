package com.xiaomi.mone.monitor.service.api;

import com.xiaomi.mone.monitor.dao.model.GrafanaTemplate;

public interface AppGrafanaMappingServiceExtension {

    void setPlatFormByLanguage(GrafanaTemplate template, String appLanguage);
}
