package com.xiaomi.mone.monitor.service.api;

import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.dao.model.AppMonitor;

public interface AlertHelperExtension {

    void buildAlertContent(StringBuilder content, JsonObject data);

    void buildDetailRedirectUrl(String user, AppMonitor app, String alert, JsonObject jsonSummary, JsonObject labels);
}
