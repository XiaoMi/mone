package com.xiaomi.youpin.gwdash.controller;

import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.Project;
import com.xiaomi.youpin.gwdash.service.LoginService;
import com.xiaomi.youpin.gwdash.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author goodjava@qq.com
 * 项目管理
 */
@RestController
@Slf4j
public class ProjectController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private ProjectService projectService;

    /**
     * 获取项目列表
     *
     * @param request
     * @param param
     * @return
     */
    @RequestMapping(value = "/api/project/list", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Map<String, Object>> list(HttpServletRequest request, @RequestBody Project param) {
        SessionAccount account = loginService.getAccountFromSession(request);
        return projectService.listProjects(account, param);
    }

    @RequestMapping(value = "/api/project/getApplicationNames", method = RequestMethod.GET)
    public Result<Set<String>> getApplicationNames(HttpServletRequest request, HttpServletResponse response) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        return projectService.getApplicationNames();
    }

}
