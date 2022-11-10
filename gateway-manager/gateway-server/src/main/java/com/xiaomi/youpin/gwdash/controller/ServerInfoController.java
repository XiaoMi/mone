package com.xiaomi.youpin.gwdash.controller;

import com.google.common.collect.Maps;
import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.dao.model.TCustomeConfig;
import com.xiaomi.youpin.gwdash.service.CustomConfigService;
import com.xiaomi.youpin.gwdash.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author tsingfu
 */
@RestController
@RequestMapping("/api/server")
public class ServerInfoController {

    @Value("${server.serverEnv}")
    private String serverEnv;

    @Value("${tracking.web.url}")
    private String url;

    @Value("${tracking.web.website}")
    private String website;

    @Autowired
    private LoginService loginService;

    @Autowired
    private CustomConfigService customConfigService;

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public Map<String, Object> info(HttpServletRequest request) {
        Map<String, Object> map = Maps.newHashMap();
        SessionAccount sessionAccount = loginService.getAccountAndResourceFromSession(request);
        TCustomeConfig tCustomeConfig = customConfigService.get("mione_server_env");
        map.put("user", sessionAccount);
        if (null != tCustomeConfig) {
            map.put("serverEnv", tCustomeConfig.getContent());
        } else {
            map.put("serverEnv", serverEnv);
        }
        return map;
    }
}
