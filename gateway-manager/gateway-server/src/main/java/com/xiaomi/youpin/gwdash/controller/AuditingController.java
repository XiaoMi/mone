package com.xiaomi.youpin.gwdash.controller;

import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.service.AuditingService;
import com.xiaomi.youpin.gwdash.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author tsingfu
 */
@RestController
@Slf4j
@RequestMapping("/api/auditing")
public class AuditingController {

    @Autowired
    private AuditingService auditingService;

    @Autowired
    private LoginService loginService;

    @RequestMapping(value = "/apply/api/group", method = RequestMethod.POST)
    public Result<Boolean> applyGroup(HttpServletRequest request, HttpServletResponse response,
                                     @RequestParam("groups") String groups,
                                      @RequestParam("groupNames") String groupNames) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            log.warn("[ApiGroupInfoController.updateApiGroup] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        return auditingService.applyApiGroupToFeishu(account.getName(), account.getUsername(), groups, groupNames);
//        return auditingService.applyApiGroup(account.getName(), account.getUsername(), groups, groupNames);
    }

}
