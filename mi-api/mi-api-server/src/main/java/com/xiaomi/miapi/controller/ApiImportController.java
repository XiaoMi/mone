package com.xiaomi.miapi.controller;

import com.xiaomi.miapi.util.SessionAccount;
import com.xiaomi.miapi.service.ApiImportService;
import com.xiaomi.miapi.service.impl.LoginService;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.exception.CommonError;
import com.xiaomi.youpin.hermes.service.BusProjectService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 接口导入controller
 */
@Controller
@RequestMapping("/ApiImport")
@Slf4j
public class ApiImportController {

    @Autowired
    private LoginService loginService;

    @Reference(check = false, group = "${ref.hermes.service.group}")
    private BusProjectService busProjectService;

    @Autowired
    private ApiImportService apiImportService;

    /**
     * 导入swagger json文件
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/importSwaggerApi", method = RequestMethod.POST)
    public Result<Integer> importSwaggerApi(HttpServletRequest request,
                                            HttpServletResponse response,
                                            String swaggerData,
                                            Integer projectID,
                                            boolean randomGen
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            log.warn("[ApiImportController.importSwaggerApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            log.warn("[ApiImportController.importSwaggerApi] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        if (!busProjectService.isMember(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "不是该项目成员");
            return null;
        } else {
            Result result;

            try {
                result = apiImportService.importSwagger(projectID, swaggerData,randomGen, account.getUsername());
            } catch (Exception e) {
                return Result.fail(CommonError.DataSyxNotSupported);
            }

            return result;
        }
    }

}
