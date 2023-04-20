/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xiaomi.mone.monitor.service.helper;

import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.dao.model.AppMonitor;
import com.xiaomi.mone.monitor.service.api.AlertHelperExtension;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhanggaofeng1
 */
@Slf4j
@Component
public class AlertUrlHelper {

    @Autowired
    private AlertHelperExtension alertHelperExtension;


    public void buildDetailRedirectUrl(String user, AppMonitor app, String alert, JsonObject jsonSummary, JsonObject labels) {
        alertHelperExtension.buildDetailRedirectUrl(user, app, alert, jsonSummary, labels);
    }

}
