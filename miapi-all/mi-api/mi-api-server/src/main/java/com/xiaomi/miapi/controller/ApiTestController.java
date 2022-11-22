package com.xiaomi.miapi.controller;

import com.alibaba.nacos.api.exception.NacosException;
import com.xiaomi.miapi.common.bo.*;
import com.xiaomi.miapi.common.dto.TestCaseDirDTO;
import com.xiaomi.miapi.common.pojo.DubboTestPermissionApplyDTO;
import com.xiaomi.miapi.util.SessionAccount;
import com.xiaomi.miapi.common.pojo.ApiTestCase;
import com.xiaomi.miapi.service.ApiTestService;
import com.xiaomi.miapi.service.impl.LoginService;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.common.HttpResult;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.exception.CommonError;
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
 * 接口测试controller
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

    /**
     * 添加用例文件夹
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ApiTestController.saveTestCaseDir] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        dto.setAccountId(account.getId().intValue());
        return apiTestService.createTestCaseDir(dto);
    }

    /**
     * 修改测试用例名
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ApiTestController.updateCaseName] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        return apiTestService.updateCaseName(caseId,caseName);
    }

    /**
     * 修改测试用例文件夹名
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ApiTestController.updateCaseDirName] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        return apiTestService.updateCaseDirName(dirId,dirName);
    }

    /**
     * 保存http测试用例
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ApiTestController.saveHttpTestCase] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        bo.setAccountId(account.getId().intValue());
        return apiTestService.saveHttpTestCase(bo);
    }

    /**
     * 保存http测试用例
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ApiTestController.updateHttpTestCase] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        bo.setAccountId(account.getId().intValue());
        return apiTestService.updateHttpTestCase(bo);
    }
    /**
     * 保存网关测试用例
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ApiTestController.saveGatewayTestCase] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        bo.setAccountId(account.getId().intValue());
        return apiTestService.saveGatewayTestCase(bo);
    }

    /**
     * 保存网关测试用例
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ApiTestController.updateGatewayTestCase] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        bo.setAccountId(account.getId().intValue());
        return apiTestService.updateGatewayTestCase(bo);
    }

    /**
     * 保存dubbo类型接口的测试用例
     *
     * @param bo
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ApiTestController.saveDubboTestCase] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        bo.setAccountId(account.getId().intValue());
        return apiTestService.saveDubboTestCase(bo);
    }

    /**
     * 更新dubbo类型接口的测试用例
     *
     * @param bo
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ApiTestController.updateDubboTestCase] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        bo.setAccountId(account.getId().intValue());
        return apiTestService.updateDubboTestCase(bo);
    }

    /**
     * 保存grpc类型接口的测试用例
     *
     * @param bo
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ApiTestController.saveGrpcTestCase] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        bo.setAccountId(account.getId().intValue());
        return apiTestService.saveGrpcTestCase(bo);
    }

    /**
     * 保存dubbo类型接口的测试用例
     *
     * @param bo
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ApiTestController.updateGrpcTestCase] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        bo.setAccountId(account.getId().intValue());
        return apiTestService.updateGrpcTestCase(bo);
    }

    /**
     * 删除用例
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ApiTestController.deleteCaseById] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        return apiTestService.deleteCaseById(caseId);
    }

    /**
     * 删除用例组
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ApiTestController.deleteCaseGroup] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        return apiTestService.deleteCaseGroup(groupId);
    }

    /**
     * 获取用例详情
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ApiTestController.getCaseDetailById] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        return apiTestService.getCaseDetailById(caseId);
    }

    /**
     * 获取接口下的case分组及列表
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ApiTestController.getCasesByApi] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        return apiTestService.getCasesByApi(projectId,apiId, account.getId().intValue());
    }

    /**
     * 获取项目下的case分组及列表
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ApiTestController.getCasesByProject] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        return apiTestService.getCasesByProject(projectId, account.getId().intValue());
    }

    /**
     * 获取接口方法
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ApiTestController.getServiceMethod] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        return apiTestService.getServiceMethod(serviceName,env);
    }

    /**
     * 申请线上dubbo接口测试权限审批
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/applyOnlineDubboTest", method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> applyOnlineDubboTest(
            HttpServletRequest request,
            HttpServletResponse response,
            DubboTestPermissionApplyDTO dto
    ) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            LOGGER.warn("[ApiTestController.applyOnlineDubboTest] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ApiTestController.applyOnlineDubboTest] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        dto.setUserId(account.getId().intValue());
        dto.setOperator(account.getUsername());
        return apiTestService.applyOnlineDubboTest(dto);
    }

}
