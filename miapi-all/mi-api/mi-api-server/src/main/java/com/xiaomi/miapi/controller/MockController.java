package com.xiaomi.miapi.controller;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.miapi.util.SessionAccount;
import com.xiaomi.miapi.common.pojo.ApiMockExpect;
import com.xiaomi.miapi.service.MockService;
import com.xiaomi.miapi.service.impl.LoginService;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.exception.CommonError;
import com.xiaomi.youpin.hermes.service.BusProjectService;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * mock请求控制器
 */
@Controller
@RequestMapping("/Mock")
public class MockController {
    @Resource
    private MockService mockService;

    @Autowired
    private LoginService loginService;

    @Reference(check = false, group = "${ref.hermes.service.group}")
    private BusProjectService busProjectService;


    private static final Logger LOGGER = LoggerFactory.getLogger(MockController.class);

    @ResponseBody
    @RequestMapping(value = "/getMockExpectDetail", method = RequestMethod.POST)
    public Result<ApiMockExpect> getMockExpectDetail(HttpServletRequest request,
                                                     HttpServletResponse response,
                                                     Integer mockExpectID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[MockController.getMockExpectDetail] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[DocumentController.getMockExpectDetail] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        return mockService.getMockExpectDetail(mockExpectID);
    }

    @ResponseBody
    @RequestMapping(value = "/getMockExpectList", method = RequestMethod.POST)
    public Result<Map<String,Object>> getMockExpectList(HttpServletRequest request,
                                                         HttpServletResponse response,
                                                         Integer apiID, Integer projectID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[MockController.getMockExpectList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[DocumentController.getMockExpectList] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        if (!busProjectService.isMember(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "不是项目成员");
            return null;
        }
        return mockService.getMockExpectList(apiID);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteMockExpect", method = RequestMethod.POST)
    public Result<Boolean> deleteMockExpect(HttpServletRequest request,
                                            HttpServletResponse response,
                                            Integer mockExpectID, Integer projectID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[MockController.deleteMockExpect] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[DocumentController.deleteMockExpect] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        if (!busProjectService.isBusProjectAdmin(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "需要admin权限执行此操作");
            return null;
        }
        return mockService.deleteMockExpect(mockExpectID);
    }

    @ResponseBody
    @RequestMapping(value = "/enableMockExpect", method = RequestMethod.POST)
    public Result<Boolean> enableMockExpect(HttpServletRequest request,
                                            HttpServletResponse response,
                                            Integer mockExpectID, Integer enable, Integer projectID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[MockController.enableMockExpect] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[DocumentController.enableMockExpect] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        if (!busProjectService.isAboveWork(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "需要work以上权限执行此操作");
            return null;
        }

        return mockService.enableMockExpect(mockExpectID, enable);
    }

    @ResponseBody
    @RequestMapping(value = "/httpApiMock", method = RequestMethod.POST)
    public Result<String> httpApiMock(String mockUrl) {
        return mockService.httpApiMock(mockUrl);
    }

    @ResponseBody
    @RequestMapping(value = "/gatewayApiMock", method = RequestMethod.POST)
    public Result<String> gatewayApiMock(String mockUrl) {
        return mockService.gatewayApiMock(mockUrl);
    }

    @ResponseBody
    @RequestMapping(value = "/dubboApiMock", method = RequestMethod.POST)
    public Result<String> dubboApiMock(String mockUrl) {
        return mockService.dubboApiMock(mockUrl);
    }


    @ResponseBody
    @RequestMapping(value = "/previewMockData", method = RequestMethod.POST)
    public Result<Object> previewMockData(String mockRule, Integer mockDataType) {
        //json格式直接返回
        if (mockDataType == 1) {
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(mockRule);
            if (element instanceof JsonObject) {
                Map<String, Object> map = gson.fromJson(mockRule, new TypeToken<HashMap<String, Object>>() {
                }.getType());
                return Result.success(map);
            } else if (element instanceof JsonArray) {
                List<Object> list = gson.fromJson(mockRule, new TypeToken<List<Object>>() {
                }.getType());
                return Result.success(list);
            } else {
                return Result.success(mockRule);
            }
        }
        return Result.success(mockService.previewMockData(mockService.parseStructToJson(mockRule,false)));
    }

    /**
     * 自定义mock地址
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/selfConfMockUrl", method = RequestMethod.POST)
    public Result selfConfMockUrl(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Integer expectId,
                                  String originUrl,
                                  String newUrl
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.selfConfMockUrl] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        return mockService.selfConfMockUrl(expectId,originUrl, newUrl);
    }
}
