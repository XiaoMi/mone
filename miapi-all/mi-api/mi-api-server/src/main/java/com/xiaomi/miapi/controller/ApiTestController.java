package com.xiaomi.miapi.controller;

import com.alibaba.nacos.api.exception.NacosException;
import com.xiaomi.miapi.bo.*;
import com.xiaomi.miapi.dto.TestCaseDirDTO;
import com.xiaomi.miapi.pojo.DubboTestPermissionApplyDTO;
import com.xiaomi.miapi.util.SessionAccount;
import com.xiaomi.miapi.pojo.ApiTestCase;
import com.xiaomi.miapi.service.ApiTestService;
import com.xiaomi.miapi.service.impl.LoginService;
import com.xiaomi.miapi.common.HttpResult;
import com.xiaomi.miapi.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 * deal with api test request
 */
@Controller
@RequestMapping("/ApiTest")
public class ApiTestController {
    @Autowired
    private LoginService loginService;

    @Autowired
    private ApiTestService apiTestService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiTestController.class);

    @RequestMapping(value = "/httpTest", method = RequestMethod.POST)
    @ResponseBody
    public Result<HttpResult> httpTest(
            HttpTestBo bo,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            LOGGER.warn("[ApiTestController.httpTest] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return apiTestService.httpTest(request,bo, account.getUsername());
    }


    @RequestMapping(value = "/grpcTest", method = RequestMethod.POST)
    @ResponseBody
    public Result<Object> grpcTest(
            GrpcTestBo bo,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {

        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            LOGGER.warn("[ApiTestController.grpcTest] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return Result.success(apiTestService.grpcTest(bo, account.getUsername()));
    }

    @RequestMapping(value = "/dubboTest", method = RequestMethod.POST)
    @ResponseBody
    public Result<Object> dubboTest(
            DubboTestBo bo,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException, NacosException {

        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            LOGGER.warn("[ApiTestController.dubboTest] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (Objects.isNull(bo.getGroup())){
            bo.setGroup("");
        }
        if (Objects.isNull(bo.getVersion())){
            bo.setVersion("");
        }
        return apiTestService.dubboTest(bo, account.getUsername(),account.getId().intValue());
    }

    @RequestMapping(value = "/saveTestCaseDir", method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> saveTestCaseDir(
            TestCaseDirDTO dto,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            LOGGER.warn("[ApiTestController.saveTestCaseDir] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        dto.setAccountId(account.getId().intValue());
        return apiTestService.createTestCaseDir(dto);
    }

    @RequestMapping(value = "/updateCaseName", method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> updateCaseName(
            HttpServletRequest request,
            HttpServletResponse response,
            Integer caseId,
            String caseName
    ) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            LOGGER.warn("[ApiTestController.updateCaseName] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return apiTestService.updateCaseName(caseId,caseName);
    }

    @RequestMapping(value = "/updateCaseDirName", method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> updateCaseDirName(
            HttpServletRequest request,
            HttpServletResponse response,
            Integer dirId,
            String dirName
    ) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            LOGGER.warn("[ApiTestController.updateCaseDirName] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return apiTestService.updateCaseDirName(dirId,dirName);
    }

    @RequestMapping(value = "/saveHttpTestCase", method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> saveHttpTestCase(
            HttpTestCaseBo bo,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            LOGGER.warn("[ApiTestController.saveHttpTestCase] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        bo.setAccountId(account.getId().intValue());
        return apiTestService.saveHttpTestCase(bo);
    }

    @RequestMapping(value = "/updateHttpTestCase", method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> updateHttpTestCase(
            HttpTestCaseBo bo,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            LOGGER.warn("[ApiTestController.updateHttpTestCase] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        bo.setAccountId(account.getId().intValue());
        return apiTestService.updateHttpTestCase(bo);
    }

    @RequestMapping(value = "/saveGatewayTestCase", method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> saveGatewayTestCase(
            GatewayTestCaseBo bo,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            LOGGER.warn("[ApiTestController.saveGatewayTestCase] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        bo.setAccountId(account.getId().intValue());
        return apiTestService.saveGatewayTestCase(bo);
    }

    @RequestMapping(value = "/updateGatewayTestCase", method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> updateGatewayTestCase(
            GatewayTestCaseBo bo,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            LOGGER.warn("[ApiTestController.updateGatewayTestCase] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        bo.setAccountId(account.getId().intValue());
        return apiTestService.updateGatewayTestCase(bo);
    }

    @RequestMapping(value = "/saveDubboTestCase", method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> saveDubboTestCase(
            DubboTestCaseBo bo,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            LOGGER.warn("[ApiTestController.saveDubboTestCase] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        bo.setAccountId(account.getId().intValue());
        return apiTestService.saveDubboTestCase(bo);
    }

    @RequestMapping(value = "/updateDubboTestCase", method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> updateDubboTestCase(
            DubboTestCaseBo bo,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            LOGGER.warn("[ApiTestController.updateDubboTestCase] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        bo.setAccountId(account.getId().intValue());
        return apiTestService.updateDubboTestCase(bo);
    }

    @RequestMapping(value = "/saveGrpcTestCase", method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> saveGrpcTestCase(
            GrpcTestCaseBo bo,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            LOGGER.warn("[ApiTestController.saveGrpcTestCase] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        bo.setAccountId(account.getId().intValue());
        return apiTestService.saveGrpcTestCase(bo);
    }

    @RequestMapping(value = "/updateGrpcTestCase", method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> updateGrpcTestCase(
            GrpcTestCaseBo bo,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            LOGGER.warn("[ApiTestController.updateGrpcTestCase] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        bo.setAccountId(account.getId().intValue());
        return apiTestService.updateGrpcTestCase(bo);
    }

    @RequestMapping(value = "/deleteCaseById", method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> deleteCaseById(
            HttpServletRequest request,
            HttpServletResponse response,
            Integer caseId
    ) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            LOGGER.warn("[ApiTestController.deleteCaseById] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return apiTestService.deleteCaseById(caseId);
    }

    @RequestMapping(value = "/deleteCaseGroup", method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> deleteCaseGroup(
            HttpServletRequest request,
            HttpServletResponse response,
            Integer groupId
    ) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            LOGGER.warn("[ApiTestController.deleteCaseGroup] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return apiTestService.deleteCaseGroup(groupId);
    }

    @RequestMapping(value = "/getCaseDetailById", method = RequestMethod.POST)
    @ResponseBody
    public Result<ApiTestCase> getCaseDetailById(
            HttpServletRequest request,
            HttpServletResponse response,
            Integer caseId
    ) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            LOGGER.warn("[ApiTestController.getCaseDetailById] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return apiTestService.getCaseDetailById(caseId);
    }

    @RequestMapping(value = "/getCasesByApi", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<CaseGroupAndCasesBo>> getCasesByApi(
            HttpServletRequest request,
            HttpServletResponse response,
            Integer projectId,
            Integer apiId
    ) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            LOGGER.warn("[ApiTestController.getCasesByApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return apiTestService.getCasesByApi(projectId,apiId, account.getId().intValue());
    }

    @RequestMapping(value = "/getCasesByProject", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<CaseGroupAndCasesBo>> getCasesByProject(
            HttpServletRequest request,
            HttpServletResponse response,
            Integer projectId
    ) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            LOGGER.warn("[ApiTestController.getCasesByProject] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return apiTestService.getCasesByProject(projectId, account.getId().intValue());
    }

    @RequestMapping(value = "/getServiceMethod", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<String>> getServiceMethod(
            HttpServletRequest request,
            HttpServletResponse response,
            String serviceName,
            String env
    ) throws IOException, NacosException {

        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            LOGGER.warn("[ApiTestController.getServiceMethod] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return apiTestService.getServiceMethod(serviceName,env);
    }
}
