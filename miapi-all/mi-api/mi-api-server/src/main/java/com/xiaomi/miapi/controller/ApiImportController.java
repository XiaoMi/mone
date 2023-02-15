package com.xiaomi.miapi.controller;

import com.xiaomi.miapi.util.SessionAccount;
import com.xiaomi.miapi.service.ApiImportService;
import com.xiaomi.miapi.service.impl.LoginService;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.exception.CommonError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 * deal with api import request
 */
@Controller
@RequestMapping("/ApiImport")
@Slf4j
public class ApiImportController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private ApiImportService apiImportService;

    /**
     * import swagger json file
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
        Result result;
        try {
            result = apiImportService.importSwagger(projectID, swaggerData, randomGen, account.getUsername());
        } catch (Exception e) {
            return Result.fail(CommonError.DataSyxNotSupported);
        }
        return result;
    }

}
