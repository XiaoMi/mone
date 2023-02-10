package com.xiaomi.miapi.service.impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.hera.trace.context.HeraContextUtil;
import com.xiaomi.miapi.bo.*;
import com.xiaomi.miapi.dto.TestCaseDirDTO;
import com.xiaomi.miapi.mapper.*;
import com.xiaomi.miapi.pojo.*;
import com.xiaomi.miapi.util.*;
import com.xiaomi.miapi.vo.MethodInfo;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.common.HttpResult;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.miapi.service.ApiTestService;
import com.xiaomi.miapi.common.exception.CommonError;
import com.xiaomi.youpin.hermes.bo.response.BusProjectRoleResp;
import com.xiaomi.youpin.hermes.entity.BusProject;
import com.xiaomi.youpin.hermes.service.BusProjectService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.cluster.router.address.Address;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ApiTestServiceImpl implements ApiTestService {

    @Autowired
    private ApiTestLogMapper apiTestLogMapper;

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private TestCaseGroupMapper testCaseGroupMapper;

    @Autowired
    private ApiTestCaseMapper apiTestCaseMapper;

    @Autowired
    private ApiEnvMapper envMapper;

    @Autowired
    ApiMapper apiMapper;

    @Resource(name = "stRegistry")
    private RegistryConfig stRegistryConfig;

    @Resource(name = "olRegistry")
    private RegistryConfig olRegistryConfig;

    @Resource(name = "nacosNamingSt")
    private NacosNaming nacosNamingSt;

    @Resource(name = "nacosNamingOl")
    private NacosNaming nacosNamingOl;

    private static final Gson gson = new Gson();
    private final GrpcReflectionCall grpcCall = new GrpcReflectionCall();

    @Override
    public Result<HttpResult> httpTest(HttpServletRequest servletReq, HttpTestBo request, String opUsername) {
        if (StringUtils.isBlank(request.getMethod()) || StringUtils.isBlank(request.getUrl())) {
            return Result.fail(CommonError.InvalidParamError);
        }
        //deal with local ip
        request.setUrl(judgeAndReplaceDomain(servletReq, request.getUrl()));
        Map<String, String> headers = new HashMap<>();
        try {
            if (StringUtils.isNotBlank(request.getHeaders())) {
                headers = ApiServiceImpl.gson.fromJson(request.getHeaders(), new TypeToken<Map<String, String>>() {
                }.getType());
            }
        } catch (Exception e) {
            log.error("[ApiTestService.httpTest] invalid header params, params: {}, err: {}", headers, e);
            return Result.fail(CommonError.JsonSerializeError);
        }
        if (request.getTimeout() <= 0) {
            request.setTimeout(500);
        }
        headers.putIfAbsent("accept-encoding", "utf-8");
        headers.putIfAbsent("Accept-Encoding", "utf-8");

        Map<String, String> params = null;

        HttpResult result;
        long start = System.currentTimeMillis();
        if (!request.getUseX5Filter()) {
            if (request.getMethod().equalsIgnoreCase("get")) {
                try {
                    if (StringUtils.isNotBlank(request.getBody())) {
                        params = ApiServiceImpl.gson.fromJson(request.getBody(), new TypeToken<Map<String, String>>() {
                        }.getType());
                    }
                } catch (Exception e) {
                    log.error("[ApiTestService.httpTest] invalid params, params: {}, err: {}", request.getBody(), e);
                    return Result.fail(CommonError.JsonSerializeError);
                }
                result = HttpUtils.get(request.getUrl(), headers, params, request.getTimeout());
            } else {
                result = HttpUtils.post(request.getUrl(), headers, request.getBody(), request.getTimeout());
            }
        } else {
            //use x5 filter
            headers.put("form_data", "true");
            Map<String, Object> x5Body = new HashMap<>();
            Map<String, String> x5Header = new HashMap<>();
            x5Header.put("appid", request.getAppID());
            if (Objects.nonNull(request.getX5Method()) && !request.getX5Method().isEmpty()) {
                x5Header.put("method", request.getX5Method());
            }
            String sign = genSign(request.getAppID(), request.getBody(), request.getAppkey());
            x5Header.put("sign", sign);
            x5Body.put("header", x5Header);
            x5Body.put("body", request.getBody());
            String data = ApiServiceImpl.gson.toJson(x5Body);
            Map<String, String> body = new HashMap<>();
            body.put("data", Base64.getEncoder().encodeToString(data.getBytes(Charsets.UTF_8)));
            result = HttpUtils.post(request.getUrl(), headers, gson.toJson(body), request.getTimeout());
        }

        if (Objects.isNull(result)) {
            return Result.fail(CommonError.ConnectRefused);
        }

        if (Objects.nonNull(result.getContent()) && Objects.nonNull(result.getHeaders())) {
            result.setCost(result.getTimestamp() - start);
            result.setSize(result.getContent().getBytes().length);
        }
        //record http req log
        ApiTestLog apiTestLog = new ApiTestLog();
        apiTestLog.setUrl(request.getUrl());
        apiTestLog.setOpUsername(opUsername);
        Date date = new Date();
        Timestamp opTime = new Timestamp(date.getTime());
        apiTestLog.setOpTime(opTime);
        if (Objects.nonNull(request.getBody())) {
            apiTestLog.setParam(request.getBody());
        }
        checkAndFillLog(apiTestLog);
        apiTestLogMapper.insert(apiTestLog);

        return Result.success(result);
    }

    @Override
    public Result<Object> dubboTest(DubboTestBo request, String opUsername) throws NacosException {
        if (request.isProduction()) {
            if (request.getGroup() == null) {
                request.setGroup("");
            }
            if (request.getVersion() == null) {
                request.setVersion("");
            }
        }
        Map<String, Object> resultMap = new HashMap<>();
        log.info("excecuteDubbo:{}", new Gson().toJson(request));

        MethodInfo methodInfo = new MethodInfo();
        if (Objects.isNull(request.getGroup())) {
            methodInfo.setGroup("");
        } else {
            methodInfo.setGroup(request.getGroup());
        }
        methodInfo.setMethodName(request.getMethodName());
        methodInfo.setServiceName(request.getInterfaceName());
        if (Objects.nonNull(request.getRetries())) {
            methodInfo.setRetries(request.getRetries());
        }
        if (Objects.nonNull(request.getTimeout())) {
            methodInfo.setTimeout(request.getTimeout());
        }
        if (StringUtils.isNotEmpty(request.getAddr())) {
            methodInfo.setAddr(request.getAddr());
        }
        if (StringUtils.isNotEmpty(request.getIp())) {
            methodInfo.setIp(request.getIp());
        }
        if (StringUtils.isNotEmpty(request.getVersion())) {
            methodInfo.setVersion(request.getVersion());
        }
        if (request.isGenParam()) {
            RpcContext.getContext().setAttachment(Constants.GENERIC_KEY, "youpin_json");
        }
        //accept attachment
        if (StringUtils.isNotEmpty(request.getAttachment())) {
            List<Attachment> attachments = ApiServiceImpl.gson.fromJson(request.getAttachment(), new TypeToken<List<Attachment>>() {
            }.getType());
            attachments.forEach(attachment -> {
                RpcContext.getContext().setAttachment(attachment.getKey(), attachment.getValue());
            });
        }
        if (StringUtils.isEmpty(request.getParamType())) {
            methodInfo.setParameterTypes(new String[]{});
        } else {
            String[] types = new Gson().fromJson(request.getParamType(), new TypeToken<String[]>() {
            }.getType());
            methodInfo.setParameterTypes(types);
        }

        if (StringUtils.isEmpty(request.getParameter())) {
            methodInfo.setArgs(new Object[]{});
        } else {
            Object[] params;
            try {
                params = new Gson().fromJson(request.getParameter(), new TypeToken<Object[]>() {
                }.getType());
            } catch (JsonSyntaxException e) {
                return Result.fail(CommonError.InvalidParamError);
            }
            methodInfo.setArgs(params);
        }
        Object res;
        long start = System.currentTimeMillis();
        res = call(methodInfo, request.isProduction());
        long end = System.currentTimeMillis();

        resultMap.put("cost", end - start);
        if (res != null) {
            resultMap.put("size", res.toString().getBytes().length);
        } else {
            resultMap.put("size", 0);
        }
        resultMap.put("res", res);
        //record dubbo api log
        ApiTestLog log = new ApiTestLog();
        log.setOpUsername(opUsername);
        log.setInterfaceName(request.getInterfaceName());
        log.setApiGroup(request.getGroup());
        log.setMethodName(request.getMethodName());
        log.setVersion(request.getVersion());
        if (request.isProduction()) {
            log.setEnv("online");
        } else {
            log.setEnv("staging");
        }
        if (Objects.nonNull(request.getIp())) {
            log.setIp(request.getIp());
        }
        log.setParam(request.getParameter());
        checkAndFillLog(log);
        apiTestLogMapper.insert(log);
        return Result.success(resultMap);
    }


    @Override
    public Object grpcTest(GrpcTestBo request, String opUsername) throws Exception {
        String[] addrs = request.getAddrs().split(",");
        String tmpAddr = addrs[new Random().nextInt(addrs.length)];
        final String serviceName = StringUtils.join(new String[]{request.getPackageName(), request.getInterfaceName()}, ".");
        String res = null;
        try {
            res = grpcCall.call(tmpAddr, serviceName + "." + request.getMethodName(), request.getParameter(), request.getTimeout());
        } catch (Exception e) {
            return HttpResult.success(HttpStatus.NO_CONTENT.value(), e.getMessage());
        }
        ApiTestLog log = new ApiTestLog();
        log.setOpUsername(opUsername);
        log.setInterfaceName(serviceName);
        log.setMethodName(request.getMethodName());
        log.setIp(tmpAddr);
        checkAndFillLog(log);
        apiTestLogMapper.insert(log);
        return HttpResult.success(HttpStatus.OK.value(), res);
    }

    @Override
    public Result<Boolean> createTestCaseDir(TestCaseDirDTO testCaseDir) {
        TestCaseGroup caseGroup = new TestCaseGroup();
        caseGroup.setCaseGroupName(testCaseDir.getName());
        if (testCaseDir.getGlobalCase() || Objects.isNull(testCaseDir.getApiId()) || testCaseDir.getApiId() == 0) {
            caseGroup.setApiId(0);
        } else {
            caseGroup.setApiId(testCaseDir.getApiId());
        }
        caseGroup.setAccountId(testCaseDir.getAccountId());
        caseGroup.setProjectId(testCaseDir.getProjectId());
        testCaseGroupMapper.insert(caseGroup);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> updateCaseName(int caseId, String caseName) {
        ApiTestCase apiTestCase = apiTestCaseMapper.selectByPrimaryKey(caseId);
        if (Objects.isNull(apiTestCase)) {
            return Result.fail(CommonError.CaseNotExist);
        }
        apiTestCase.setCaseName(caseName);
        apiTestCaseMapper.updateByPrimaryKey(apiTestCase);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> updateCaseDirName(int dirId, String dirName) {
        TestCaseGroup caseGroup = testCaseGroupMapper.selectByPrimaryKey(dirId);
        if (Objects.isNull(caseGroup)) {
            return Result.fail(CommonError.InvalidParamError);
        }
        caseGroup.setCaseGroupName(dirName);
        testCaseGroupMapper.updateByPrimaryKey(caseGroup);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> saveHttpTestCase(HttpTestCaseBo caseBo) {
        ApiTestCase testCase = new ApiTestCase();
        initApiTestCaseInfo(testCase);
        if (Objects.nonNull(caseBo.getApiId()) && caseBo.getApiId() != 0) {
            testCase.setApiId(caseBo.getApiId());
        } else {
            testCase.setApiId(0);
        }
        testCase.setAccountId(caseBo.getAccountId());
        testCase.setCaseName(caseBo.getCaseName());
        testCase.setCaseGroupId(caseBo.getCaseGroupId());
        testCase.setApiProtocal(Consts.HTTP_API_TYPE);
        testCase.setHttpMethod(caseBo.getHttpMethod());

        testCase.setRequestTimeout(caseBo.getRequestTimeout());
        testCase.setEnvId(caseBo.getEnvId());
        ApiEnv env = envMapper.selectByPrimaryKey(caseBo.getEnvId());
        if (Objects.isNull(env)) {
            return Result.fail(CommonError.InvalidParamError);
        }
        testCase.setUrl(caseBo.getUrl());
        testCase.setHttpDomian(env.getHttpDomain());
        testCase.setHttpHeaders(caseBo.getHttpHeaders());
        testCase.setHttpRequestBody(caseBo.getHttpRequestBody());
        if (caseBo.getUseX5Filter()) {
            testCase.setUseX5Filter(caseBo.getUseX5Filter());
            testCase.setX5AppId(caseBo.getX5AppId());
            testCase.setX5AppKey(caseBo.getX5AppKey());
        }
        testCase.setHttpReqBodyType(caseBo.getHttpReqBodyType());
        apiTestCaseMapper.insert(testCase);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> updateHttpTestCase(HttpTestCaseBo caseBo) {
        ApiTestCase apiTestCase = apiTestCaseMapper.selectByPrimaryKey(caseBo.getId());
        if (Objects.isNull(apiTestCase)) {
            return Result.fail(CommonError.CaseNotExist);
        }
        if (apiTestCase.getApiId() != 0) {
            //global case
            apiTestCase.setHttpMethod(caseBo.getHttpMethod());
            apiTestCase.setUrl(caseBo.getUrl());
        }
        //common
        ApiEnv env = envMapper.selectByPrimaryKey(caseBo.getEnvId());
        apiTestCase.setEnvId(caseBo.getEnvId());
        apiTestCase.setHttpDomian(env.getHttpDomain());
        apiTestCase.setRequestTimeout(caseBo.getRequestTimeout());
        apiTestCase.setUrl(caseBo.getUrl());
        apiTestCase.setHttpHeaders(caseBo.getHttpHeaders());
        apiTestCase.setHttpRequestBody(caseBo.getHttpRequestBody());
        apiTestCase.setUseX5Filter(caseBo.getUseX5Filter());
        if (caseBo.getUseX5Filter()) {
            apiTestCase.setX5AppId(caseBo.getX5AppId());
            apiTestCase.setX5AppKey(caseBo.getX5AppKey());
        } else {
            apiTestCase.setX5AppId("");
            apiTestCase.setX5AppKey("");
        }
        apiTestCase.setHttpReqBodyType(caseBo.getHttpReqBodyType());

        apiTestCaseMapper.updateByPrimaryKeyWithBLOBs(apiTestCase);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> saveGatewayTestCase(GatewayTestCaseBo caseBo) {
        ApiTestCase testCase = new ApiTestCase();
        initApiTestCaseInfo(testCase);
        if (Objects.nonNull(caseBo.getApiId()) && caseBo.getApiId() != 0) {
            testCase.setApiId(caseBo.getApiId());
        } else {
            testCase.setApiId(0);
        }
        testCase.setUrl(caseBo.getUrl());
        testCase.setAccountId(caseBo.getAccountId());
        testCase.setCaseName(caseBo.getCaseName());
        testCase.setCaseGroupId(caseBo.getCaseGroupId());
        testCase.setApiProtocal(Consts.GATEWAY_API_TYPE);
        testCase.setHttpMethod(caseBo.getHttpMethod());

        testCase.setRequestTimeout(caseBo.getRequestTimeout());
        testCase.setHttpDomian(caseBo.getGatewayDomain());
        testCase.setHttpHeaders(caseBo.getHttpHeaders());
        testCase.setHttpRequestBody(caseBo.getHttpRequestBody());
        if (caseBo.getUseX5Filter()) {
            testCase.setUseX5Filter(caseBo.getUseX5Filter());
            testCase.setX5AppId(caseBo.getX5AppId());
            testCase.setX5AppKey(caseBo.getX5AppKey());
        } else {
            testCase.setUseX5Filter(caseBo.getUseX5Filter());
        }
        testCase.setHttpReqBodyType(caseBo.getHttpReqBodyType());
        apiTestCaseMapper.insert(testCase);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> updateGatewayTestCase(GatewayTestCaseBo caseBo) {
        ApiTestCase apiTestCase = apiTestCaseMapper.selectByPrimaryKey(caseBo.getId());
        if (Objects.isNull(apiTestCase)) {
            return Result.fail(CommonError.CaseNotExist);
        }
        if (apiTestCase.getApiId() == 0) {
            //global case
            apiTestCase.setHttpMethod(caseBo.getHttpMethod());
            apiTestCase.setUrl(caseBo.getUrl());
        }
        //common
        apiTestCase.setHttpDomian(caseBo.getGatewayDomain());
        apiTestCase.setRequestTimeout(caseBo.getRequestTimeout());
        apiTestCase.setHttpHeaders(caseBo.getHttpHeaders());
        apiTestCase.setHttpRequestBody(caseBo.getHttpRequestBody());
        apiTestCase.setUseX5Filter(caseBo.getUseX5Filter());
        if (caseBo.getUseX5Filter()) {
            apiTestCase.setX5AppId(caseBo.getX5AppId());
            apiTestCase.setX5AppKey(caseBo.getX5AppKey());
        } else {
            apiTestCase.setX5AppId("");
            apiTestCase.setX5AppKey("");
        }
        apiTestCase.setHttpReqBodyType(caseBo.getHttpReqBodyType());

        apiTestCaseMapper.updateByPrimaryKeyWithBLOBs(apiTestCase);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> saveDubboTestCase(DubboTestCaseBo caseBo) {
        ApiTestCase testCase = new ApiTestCase();
        initApiTestCaseInfo(testCase);
        if (Objects.nonNull(caseBo.getApiId()) && caseBo.getApiId() != 0) {
            testCase.setApiId(caseBo.getApiId());
        } else {
            testCase.setApiId(0);
        }
        testCase.setAccountId(caseBo.getAccountId());
        testCase.setCaseName(caseBo.getCaseName());
        testCase.setCaseGroupId(caseBo.getCaseGroupId());
        testCase.setApiProtocal(Consts.DUBBO_API_TYPE);
        testCase.setRequestTimeout(caseBo.getRequestTimeout());
        testCase.setDubboRetryTime(caseBo.getRetry());

        testCase.setDubboEnv(caseBo.getEnv());
        if (StringUtils.isNotEmpty(caseBo.getDubboAddr())) {
            testCase.setDubboAddr(caseBo.getDubboAddr());
        }
        testCase.setDubboInterface(caseBo.getDubboInterface());
        testCase.setDubboMethodName(caseBo.getDubboMethodName());
        if (Objects.nonNull(caseBo.getDubboGroup())) {
            testCase.setDubboGroup(caseBo.getDubboGroup());
        } else {
            testCase.setDubboGroup("");
        }
        if (Objects.nonNull(caseBo.getDubboVersion())) {
            testCase.setDubboVersion(caseBo.getDubboVersion());
        } else {
            testCase.setDubboVersion("");
        }
        testCase.setDubboParamType(caseBo.getDubboParamType());
        testCase.setDubboParamBody(caseBo.getDubboParamBody());

        if (Objects.nonNull(caseBo.getUseGenericParam())) {
            testCase.setDubboIsGeneric(caseBo.getUseGenericParam());
        } else {
            testCase.setDubboIsGeneric(false);
        }
        if (caseBo.getUseAttachment()) {
            testCase.setDubboUseAttachment(caseBo.getUseAttachment());
            testCase.setDubboAttachment(caseBo.getAttachment());
        }
        apiTestCaseMapper.insert(testCase);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> updateDubboTestCase(DubboTestCaseBo caseBo) {
        ApiTestCase testCase = apiTestCaseMapper.selectByPrimaryKey(caseBo.getId());
        if (Objects.isNull(testCase)) {
            return Result.fail(CommonError.CaseNotExist);
        }
        if (testCase.getApiId() == 0) {
            testCase.setDubboEnv(caseBo.getEnv());
            testCase.setDubboInterface(caseBo.getDubboInterface());
            testCase.setDubboMethodName(caseBo.getDubboMethodName());
            if (Objects.nonNull(caseBo.getDubboGroup())) {
                testCase.setDubboGroup(caseBo.getDubboGroup());
            } else {
                testCase.setDubboGroup("");
            }
            if (Objects.nonNull(caseBo.getDubboVersion())) {
                testCase.setDubboVersion(caseBo.getDubboVersion());
            } else {
                testCase.setDubboVersion("");
            }
        }
        //common
        testCase.setAccountId(caseBo.getAccountId());
        testCase.setRequestTimeout(caseBo.getRequestTimeout());
        testCase.setDubboRetryTime(caseBo.getRetry());

        if (StringUtils.isNotEmpty(caseBo.getDubboAddr())) {
            testCase.setDubboAddr(caseBo.getDubboAddr());
        }
        testCase.setDubboParamType(caseBo.getDubboParamType());
        testCase.setDubboParamBody(caseBo.getDubboParamBody());

        if (Objects.nonNull(caseBo.getUseGenericParam())) {
            testCase.setDubboIsGeneric(caseBo.getUseGenericParam());
        } else {
            testCase.setDubboIsGeneric(false);
        }
        if (caseBo.getUseAttachment()) {
            testCase.setDubboAttachment(caseBo.getAttachment());
        }
        apiTestCaseMapper.updateByPrimaryKeyWithBLOBs(testCase);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> saveGrpcTestCase(GrpcTestCaseBo caseBo) {
        ApiTestCase testCase = new ApiTestCase();
        initApiTestCaseInfo(testCase);
        if (Objects.nonNull(caseBo.getApiId()) && caseBo.getApiId() != 0) {
            testCase.setApiId(caseBo.getApiId());
        } else {
            testCase.setApiId(0);
        }
        testCase.setAccountId(caseBo.getAccountId());
        testCase.setCaseName(caseBo.getCaseName());
        testCase.setCaseGroupId(caseBo.getCaseGroupId());
        testCase.setApiProtocal(Consts.GRPC_API_TYPE);
        testCase.setRequestTimeout(caseBo.getRequestTimeout());

        if (StringUtils.isNotEmpty(caseBo.getGrpcAddr())) {
            testCase.setGrpcServerAddr(caseBo.getGrpcAddr());
        }
        testCase.setGrpcAppName(caseBo.getAppName());
        testCase.setGrpcPackageName(caseBo.getPackageName());
        testCase.setGrpcInterfaceName(caseBo.getInterfaceName());
        testCase.setGrpcMethodName(caseBo.getMethodName());
        testCase.setGrpcParamBody(caseBo.getGrpcParamBody());

        apiTestCaseMapper.insert(testCase);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> updateGrpcTestCase(GrpcTestCaseBo caseBo) {
        ApiTestCase testCase = apiTestCaseMapper.selectByPrimaryKey(caseBo.getId());
        if (Objects.isNull(testCase)) {
            return Result.fail(CommonError.CaseNotExist);
        }

        if (testCase.getApiId() == 0) {
            if (!caseBo.getAppName().isEmpty()) {
                testCase.setGrpcAppName(caseBo.getAppName());
            }
            testCase.setGrpcAppName(caseBo.getAppName());
            testCase.setGrpcPackageName(caseBo.getPackageName());
            testCase.setGrpcInterfaceName(caseBo.getInterfaceName());
            testCase.setGrpcMethodName(caseBo.getMethodName());
        }
        testCase.setAccountId(caseBo.getAccountId());
        testCase.setRequestTimeout(caseBo.getRequestTimeout());

        if (StringUtils.isNotEmpty(caseBo.getGrpcAddr())) {
            testCase.setGrpcServerAddr(caseBo.getGrpcAddr());
        }
        testCase.setGrpcParamBody(caseBo.getGrpcParamBody());

        apiTestCaseMapper.updateByPrimaryKeyWithBLOBs(testCase);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> deleteCaseById(int caseId) {
        if (apiTestCaseMapper.deleteByPrimaryKey(caseId) > 0) {
            return Result.success(true);
        }
        return Result.fail(CommonError.UnknownError);
    }

    @Override
    @Transactional
    public Result<Boolean> deleteCaseGroup(int groupId) {

        TestCaseGroup caseGroup = testCaseGroupMapper.selectByPrimaryKey(groupId);
        if (Objects.isNull(caseGroup)) {
            return Result.fail(CommonError.InvalidParamError);
        }
        ApiTestCaseExample example = new ApiTestCaseExample();
        example.createCriteria().andCaseGroupIdEqualTo(groupId);
        if (apiTestCaseMapper.deleteByExample(example) >= 0) {
            testCaseGroupMapper.deleteByPrimaryKey(groupId);
            return Result.success(true);
        }
        return Result.fail(CommonError.UnknownError);
    }

    @Override
    public Result<ApiTestCase> getCaseDetailById(int caseId) {
        return Result.success(apiTestCaseMapper.selectByPrimaryKey(caseId));
    }

    @Override
    public Result<List<String>> getServiceMethod(String serviceName, String env) throws NacosException {
        List<String> methodNames;
        List<Instance> instanceList;
        if (env.equals("online")) {
            instanceList = nacosNamingOl.getAllInstances(serviceName);
        } else if (env.equals("staging")) {
            instanceList = nacosNamingSt.getAllInstances(serviceName);
        } else {
            instanceList = new ArrayList<>();
        }
        if (Objects.nonNull(instanceList) && !instanceList.isEmpty()) {
            Instance instance = instanceList.get(0);
            String[] methodsArr = instance.getMetadata().getOrDefault("methods", "").split(",");
            methodNames = Arrays.stream(methodsArr).collect(Collectors.toList());
            return Result.success(methodNames);
        }
        return Result.success(new ArrayList<>());
    }


    @Override
    public Result<List<CaseGroupAndCasesBo>> getCasesByApi(int projectId, int apiId) {
        List<CaseGroupAndCasesBo> groupAndCasesBos = new ArrayList<>();
        Api api = apiMapper.getApiInfo(projectId, apiId);
        if (Objects.isNull(api)) {
            return Result.fail(CommonError.APIDoNotExist);
        }
        TestCaseGroupExample example = new TestCaseGroupExample();
        example.createCriteria().andApiIdEqualTo(apiId);
        List<TestCaseGroup> caseGroups = testCaseGroupMapper.selectByExample(example);
        caseGroups.forEach(caseGroup -> {
            CaseGroupAndCasesBo groupAndCasesBo = new CaseGroupAndCasesBo();
            groupAndCasesBo.setCaseGroupId(caseGroup.getId());
            groupAndCasesBo.setCaseGroupName(caseGroup.getCaseGroupName());

            ApiTestCaseExample example1 = new ApiTestCaseExample();
            example1.createCriteria().andApiIdEqualTo(apiId).andCaseGroupIdEqualTo(caseGroup.getId());
            List<ApiTestCase> caseList = apiTestCaseMapper.selectByExampleWithBLOBs(example1);
            groupAndCasesBo.setCaseList(caseList);
            groupAndCasesBos.add(groupAndCasesBo);
        });
        return Result.success(groupAndCasesBos);
    }

    @Override
    public Result<List<CaseGroupAndCasesBo>> getCasesByProject(int projectId) {
        List<CaseGroupAndCasesBo> groupAndCasesBos = new ArrayList<>();
        TestCaseGroupExample example = new TestCaseGroupExample();
        example.createCriteria().andProjectIdEqualTo(projectId).andApiIdEqualTo(0);
        List<TestCaseGroup> caseGroups = testCaseGroupMapper.selectByExample(example);
        caseGroups.forEach(caseGroup -> {
            CaseGroupAndCasesBo groupAndCasesBo = new CaseGroupAndCasesBo();
            groupAndCasesBo.setCaseGroupId(caseGroup.getId());
            groupAndCasesBo.setCaseGroupName(caseGroup.getCaseGroupName());

            ApiTestCaseExample example1 = new ApiTestCaseExample();
            example1.createCriteria().andCaseGroupIdEqualTo(caseGroup.getId()).andApiIdEqualTo(0);
            List<ApiTestCase> caseList = apiTestCaseMapper.selectByExampleWithBLOBs(example1);
            groupAndCasesBo.setCaseList(caseList);
            groupAndCasesBos.add(groupAndCasesBo);
        });
        return Result.success(groupAndCasesBos);
    }

    public Object call(MethodInfo methodInfo, boolean isProduction) {
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setApplication(applicationConfig);
        if (isProduction) {
            reference.setRegistry(olRegistryConfig);
        } else {
            reference.setRegistry(stRegistryConfig);
        }
        reference.setInterface(methodInfo.getServiceName());
        reference.setGeneric(true);
        reference.setCheck(false);
        reference.setRetries(methodInfo.getRetries());
        reference.setGroup(methodInfo.getGroup());
        reference.setVersion(methodInfo.getVersion());
        reference.setTimeout(methodInfo.getTimeout());
        Map<String, String> map = new HashMap<>();
        map.put("router", "address");
        reference.setParameters(map);
        RpcContext.getContext().setAttachment(Constants.TIMEOUT_KEY, String.valueOf(methodInfo.getTimeout()));
        ReferenceConfigCache cache = ReferenceConfigCache.getCache();
        // cache.get方法中会缓存Reference对象，并且调用ReferenceConfig.get方法启动ReferenceConfig

        //测试的情况下不需要维护ref缓存，该缓存会导致切换nacos环境失效
        cache.destroyAll();

        GenericService genericService = cache.get(reference);

        /**
         * 指定ip定向调用
         */
        if (StringUtils.isNotEmpty(methodInfo.getAddr())) {
            String[] addr = methodInfo.getAddr().split(":");

            Pattern pattern = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");

            Matcher matcher = pattern.matcher(addr[0]); //验证IP地址有效性

            if (!matcher.matches()) {
                log.warn("error ip format");
                return Result.fail(CommonError.InvalidParamError);
            }
            Address address = new Address(addr[0], Integer.parseInt(addr[1]));
            RpcContext.getContext().setObjectAttachment("address", address);
        }

        Object res = null;
        try {
            res = genericService.$invoke(methodInfo.getMethodName(), methodInfo.getParameterTypes(), methodInfo.getArgs());
        } catch (Exception e) {
            res = e.getMessage();
        } finally {
            RpcContext.getContext().clearAttachments();
            RpcContext.getContext().remove(Constants.TRACE_ID);
        }
        return res;
    }

    private void checkAndFillLog(ApiTestLog log) {
        if (Objects.isNull(log.getApiGroup())) {
            log.setApiGroup("");
        }
        if (Objects.isNull(log.getEnv())) {
            log.setEnv("");
        }
        if (Objects.isNull(log.getInterfaceName())) {
            log.setInterfaceName("");
        }
        if (Objects.isNull(log.getIp())) {
            log.setIp("");
        }
        if (Objects.isNull(log.getMethodName())) {
            log.setMethodName("");
        }
        if (Objects.isNull(log.getParam())) {
            log.setParam("");
        }
        if (Objects.isNull(log.getVersion())) {
            log.setVersion("");
        }
        if (Objects.isNull(log.getUrl())) {
            log.setUrl("");
        }
    }

    private void initApiTestCaseInfo(ApiTestCase testCase) {
        testCase.setApiId(0);
        testCase.setHttpMethod("");
        testCase.setUrl("");
        testCase.setRequestTimeout(1000);
        testCase.setHttpHeaders("");
        testCase.setCaseName("Default case name");
        testCase.setHttpDomian("");
        testCase.setEnvId(0);
        testCase.setHttpReqBodyType(0);
        testCase.setDubboInterface("");
        testCase.setDubboMethodName("");
        testCase.setDubboGroup("");
        testCase.setDubboVersion("");
        testCase.setDubboAddr("");
        testCase.setDubboParamType("");
        testCase.setDubboIsGeneric(false);
        testCase.setDubboRetryTime(1);
        testCase.setDubboUseAttachment(false);
        testCase.setDubboAttachment("");
        testCase.setDubboEnv("");
        testCase.setUseX5Filter(false);
        testCase.setX5AppKey("");
        testCase.setX5AppId("");
        testCase.setGrpcPackageName("");
        testCase.setGrpcInterfaceName("");
        testCase.setGrpcMethodName("");
        testCase.setGrpcServerAddr("");
        testCase.setGrpcAppName("");
        testCase.setHttpRequestBody("");
        testCase.setDubboParamBody("");
        testCase.setGrpcParamBody("");
    }

    private String judgeAndReplaceDomain(HttpServletRequest request, String url) {
        String realIp;
        if (url.contains("127.0.0.1")) {
            realIp = IpUtil.getIpAddr(request);
            url = url.replaceAll("127.0.0.1", realIp);
        } else if (url.contains("localhost")) {
            realIp = IpUtil.getIpAddr(request);
            url = url.replaceAll("localhost", realIp);
        }
        return url;
    }


    /**
     * @param appId
     * @param body   入参
     * @param appKey 密钥 secret
     * @return 签名
     */
    public static String genSign(String appId, String body, String appKey) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(appId).append(body).append(appKey);
        String str = stringBuilder.toString();
        StringBuilder md5CodeBuffer = new StringBuilder();

        try {
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            md5Digest.update(str.getBytes());
            byte[] strBytesDigest = md5Digest.digest();

            // 将二进制字节信息转换为十六进制字符串
            String strHexDigest = "";
            for (int i = 0; i < strBytesDigest.length; i++) {
                strHexDigest = Integer.toHexString(strBytesDigest[i] & 0XFF);
                if (strHexDigest.length() == 1) {
                    md5CodeBuffer.append("0").append(strHexDigest);
                } else {
                    md5CodeBuffer.append(strHexDigest);
                }
            }
        } catch (Exception e) {
            return str;
        }

        return md5CodeBuffer.toString().toUpperCase();
    }
}
