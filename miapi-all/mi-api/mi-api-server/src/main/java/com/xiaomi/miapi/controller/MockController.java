package com.xiaomi.miapi.controller;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.miapi.util.SessionAccount;
import com.xiaomi.miapi.pojo.ApiMockExpect;
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
 * @author dongzhenxing
 * @date 2023/02/08
 * deal with mock request
 */
@Controller
@RequestMapping("/Mock")
public class MockController {
    @Resource
    private MockService mockService;

    @Autowired
    private LoginService loginService;

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

        return mockService.getMockExpectDetail(mockExpectID);
    }

    @ResponseBody
    @RequestMapping(value = "/getMockExpectList", method = RequestMethod.POST)
    public Result<Map<String,Object>> getMockExpectList(HttpServletRequest request,
                                                         HttpServletResponse response,
                                                         Integer apiID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[MockController.getMockExpectList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return mockService.getMockExpectList(apiID);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteMockExpect", method = RequestMethod.POST)
    public Result<Boolean> deleteMockExpect(HttpServletRequest request,
                                            HttpServletResponse response,
                                            Integer mockExpectID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[MockController.deleteMockExpect] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return mockService.deleteMockExpect(mockExpectID);
    }

    @ResponseBody
    @RequestMapping(value = "/enableMockExpect", method = RequestMethod.POST)
    public Result<Boolean> enableMockExpect(HttpServletRequest request,
                                            HttpServletResponse response,
                                            Integer mockExpectID, Integer enable) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[MockController.enableMockExpect] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
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
