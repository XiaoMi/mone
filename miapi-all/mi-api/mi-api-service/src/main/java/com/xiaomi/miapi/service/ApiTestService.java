package com.xiaomi.miapi.service;

import com.alibaba.nacos.api.exception.NacosException;
import com.xiaomi.miapi.bo.*;
import com.xiaomi.miapi.common.HttpResult;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.dto.TestCaseDirDTO;
import com.xiaomi.miapi.pojo.ApiTestCase;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ApiTestService {

    Result<HttpResult> httpTest(HttpServletRequest servletReq, HttpTestBo request, String opUsername);

    Result<Object> dubboTest(DubboTestBo request, String opUsername, Integer userId) throws NacosException;

    Object grpcTest(GrpcTestBo request, String opUsername) throws Exception;

    Result<Boolean> createTestCaseDir(TestCaseDirDTO dto);

    Result<Boolean> updateCaseName(int caseId,String caseName);

    Result<Boolean> updateCaseDirName(int dirId,String dirName);

    Result<Boolean> saveHttpTestCase(HttpTestCaseBo request);

    Result<Boolean> updateHttpTestCase(HttpTestCaseBo request);

    Result<Boolean> saveGatewayTestCase(GatewayTestCaseBo request);

    Result<Boolean> updateGatewayTestCase(GatewayTestCaseBo request);

    Result<Boolean> saveDubboTestCase(DubboTestCaseBo testCaseBo);

    Result<Boolean> updateDubboTestCase(DubboTestCaseBo testCaseBo);

    Result<Boolean> saveGrpcTestCase(GrpcTestCaseBo testCaseBo);

    Result<Boolean> updateGrpcTestCase(GrpcTestCaseBo testCaseBo);

    Result<Boolean> deleteCaseById(int caseId);

    Result<Boolean> deleteCaseGroup(int groupId);

    Result<ApiTestCase> getCaseDetailById(int caseId);

    Result<List<String>> getServiceMethod(String serviceName,String env) throws NacosException;

    Result<List<CaseGroupAndCasesBo>> getCasesByApi(int projectId, int apiId, int accountId);

    Result<List<CaseGroupAndCasesBo>> getCasesByProject(int projectId, int accountId);
}
