package com.xiaomi.mock.controller;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.data.push.common.Result;
import com.xiaomi.mock.bo.EditMockDataBo;
import com.xiaomi.mock.bo.EnableMockBo;
import com.xiaomi.mock.bo.MockProxyBo;
import com.xiaomi.mock.common.FormBo;
import com.xiaomi.mock.common.HeaderUtil;
import com.xiaomi.mock.service.impl.ApiMockServiceImpl;
import com.xiaomi.mock.uitl.Md5Utils;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.mvc.MvcContext;

import javax.annotation.Resource;
import javax.script.ScriptException;
import java.util.*;

/**
 * @author dongzhenxing
 */
@Controller
public class ApiMockController {

    @Resource
    private ApiMockServiceImpl apiMockService;

    private static final Gson gson = new Gson();


    @RequestMapping(path = "/mock/**")
    public Object mockProxy(MvcContext context) throws ScriptException {
        HeaderUtil.setContentTypeUtf8(context);
        HeaderUtil.setCrosHeader(context);
        Map<String,String> headers = context.getHeaders();
        String paramsStr;
        String paramsMd5;

        JsonElement paramsJson = null;
        if ("GET".equals(context.getMethod())) {
            if (context.getParams().getAsJsonObject().size() == 0) {
                //没传参数
                paramsMd5 = Md5Utils.getMD5("");
            } else {
                //带参数
                List<FormBo> list = new ArrayList<>();
                JsonObject object = context.getParams().getAsJsonObject();
                object.entrySet().forEach(entry -> {
                    FormBo bo = new FormBo();
                    bo.setParamKey(entry.getKey());
                    bo.setParamValue(entry.getValue().getAsString());
                    list.add(bo);
                });
                //排序
                list.sort(Comparator.comparing(FormBo::getParamKey));

                List<Map<String, Object>> mapList = new ArrayList<>();
                list.forEach(formBo -> {
                    Map<String, Object> map = new HashMap<>(1);
                    map.put(formBo.getParamKey(), formBo.getParamValue());
                    mapList.add(map);
                });
                paramsMd5 = Md5Utils.getMD5(gson.toJson(mapList));
                paramsJson = context.getParams();
            }
            return apiMockService.getMockDataByProxyUrl(context.getPath(), paramsMd5,headers,paramsJson);
        } else if ("POST".equals(context.getMethod())) {
            //POST
            if (context.getParams() != null) {
                //带参数
                paramsStr = context.getParams().toString();
                paramsMd5 = Md5Utils.getMD5(paramsStr);
                paramsJson = context.getParams();
            } else {
                //不带参数
                paramsMd5 = Md5Utils.getMD5("");
            }
            return apiMockService.getMockDataByProxyUrl(context.getPath(), paramsMd5,headers,paramsJson);
        } else {
            Map<String, String> errRt = new HashMap<>();
            errRt.put("400", "不支持的请求方式");
            return errRt;
        }
    }

    @RequestMapping(path = "/http/mock/*")
    public Object httpApiMock(MvcContext context) throws ScriptException {
        HeaderUtil.setContentTypeUtf8(context);
        String paramsStr;
        String paramsMd5;

        if ("GET".equals(context.getMethod())) {
            if (context.getParams().getAsJsonObject().size() == 0) {
                //没传参数
                paramsMd5 = Md5Utils.getMD5("");
            } else {
                //带参数
                List<FormBo> list = new ArrayList<>();
                JsonObject object = context.getParams().getAsJsonObject();
                object.entrySet().forEach(entry -> {
                    FormBo bo = new FormBo();
                    bo.setParamKey(entry.getKey());
                    bo.setParamValue(entry.getValue().getAsString());
                    list.add(bo);
                });
                //排序
                list.sort(Comparator.comparing(FormBo::getParamKey));

                List<Map<String, Object>> mapList = new ArrayList<>();
                list.forEach(formBo -> {
                    Map<String, Object> map = new HashMap<>(1);
                    map.put(formBo.getParamKey(), formBo.getParamValue());
                    mapList.add(map);
                });
                paramsMd5 = Md5Utils.getMD5(gson.toJson(mapList));
            }
            return apiMockService.getMockDataByApi(context.getPath(), paramsMd5,null,null);
        } else if ("POST".equals(context.getMethod())) {
            //POST
            if (context.getParams() != null) {
                //带参数
                paramsStr = context.getParams().toString();
                paramsMd5 = Md5Utils.getMD5(paramsStr);
            } else {
                //不带参数
                paramsMd5 = Md5Utils.getMD5("");
            }
            return apiMockService.getMockDataByApi(context.getPath(), paramsMd5,null,null);
        } else {
            Map<String, String> errRt = new HashMap<>();
            errRt.put("400", "不支持的请求方式");
            return errRt;
        }
    }


    @RequestMapping(path = "/httpApi/editMockData")
    public Result<Boolean> editHttpMockData(EditMockDataBo bo) {
        return apiMockService.editMockData(bo);
    }

    @RequestMapping(path = "/http/addUrlProxy")
    public Result<Boolean> addUrlProxy(MockProxyBo bo) {
        return apiMockService.addUrlProxy(bo);
    }

    @RequestMapping(path = "/dubbo/mock/*")
    public Object dubboApiMock(MvcContext context) throws ScriptException {
        HeaderUtil.setContentTypeUtf8(context);
        String paramsStr;
        String paramsMd5;

        if ("GET".equals(context.getMethod())) {
            if (context.getParams().getAsJsonObject().size() == 0) {
                //没传参数
                paramsMd5 = Md5Utils.getMD5("");
            } else {
                //带参数
                paramsStr = context.getParams().toString();
                paramsMd5 = Md5Utils.getMD5(paramsStr);
            }
            return apiMockService.getMockDataByApi(context.getPath(), paramsMd5,null,null);
        } else if ("POST".equals(context.getMethod())) {
            //POST
            if (context.getParams() != null) {
                //带参数
                paramsStr = context.getParams().toString();
                paramsMd5 = Md5Utils.getMD5(paramsStr);
            } else {
                //不带参数
                paramsMd5 = Md5Utils.getMD5("");
            }
            return apiMockService.getMockDataByApi(context.getPath(), paramsMd5,null,null);
        } else {
            Map<String, String> errRt = new HashMap<>();
            errRt.put("400", "不支持的请求方式");
            return errRt;
        }
    }

    @RequestMapping(path = "/dubboApi/editMockData")
    public Result<Boolean> editDubboMockData(EditMockDataBo bo) {
        return apiMockService.editMockData(bo);
    }

    @RequestMapping(path = "/gateway/mock/*")
    public Object gatewayApiMock(MvcContext context) throws ScriptException {
        HeaderUtil.setContentTypeUtf8(context);
        String paramsStr;
        String paramsMd5;

        if ("GET".equals(context.getMethod())) {
            if (context.getParams().getAsJsonObject().size() == 0) {
                //没传参数
                paramsMd5 = Md5Utils.getMD5("");
            } else {
                //带参数
                List<FormBo> list = new ArrayList<>();
                JsonObject object = context.getParams().getAsJsonObject();
                object.entrySet().forEach(entry -> {
                    FormBo bo = new FormBo();
                    bo.setParamKey(entry.getKey());
                    bo.setParamValue(entry.getValue().getAsString());
                    list.add(bo);
                });
                //排序
                list.sort(Comparator.comparing(FormBo::getParamKey));

                List<Map<String, Object>> mapList = new ArrayList<>();
                list.forEach(formBo -> {
                    Map<String, Object> map = new HashMap<>(1);
                    map.put(formBo.getParamKey(), formBo.getParamValue());
                    mapList.add(map);
                });
                paramsMd5 = Md5Utils.getMD5(gson.toJson(mapList));
            }
            return apiMockService.getMockDataByApi(context.getPath(), paramsMd5,null,null);
        } else if ("POST".equals(context.getMethod())) {
            //POST
            if (context.getParams() != null) {
                //带参数
                paramsStr = context.getParams().toString();
                paramsMd5 = Md5Utils.getMD5(paramsStr);
            } else {
                //不带参数
                paramsMd5 = Md5Utils.getMD5("");
            }
            return apiMockService.getMockDataByApi(context.getPath(), paramsMd5,null,null);
        } else {
            Map<String, String> errRt = new HashMap<>();
            errRt.put("400", "不支持的请求方式");
            return errRt;
        }
    }

    @RequestMapping(path = "/gatewayApi/editMockData")
    public Result<Boolean> editGatewayMockData(EditMockDataBo bo) {
        return apiMockService.editMockData(bo);
    }

    @RequestMapping(path = "/api/enableApiMock")
    public Result<Boolean> enableApiMock(EnableMockBo bo) {
        return apiMockService.enableApiMock(bo);
    }
}
