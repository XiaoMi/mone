/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.codegen;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.codegen.bo.*;
import com.xiaomi.youpin.codegen.common.FileUtils;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Slf4j
public class HttpRequestGen {

    public static final String DUBBO_REQUEST_EXAMPLE = "dubbo-request/dubbo_request.tml";
    public static final String JAVA_REQUEST_EXAMPLE = "http-request/java_request.tml";
    public static final String CURL_REQUEST_EXAMPLE = "http-request/curl_request.tml";

    private static final Gson gson = new Gson();

    public static final Map<Integer, String> typeStringMap = new HashMap<Integer, String>() {
        {
            put(0, "String");
            put(3, "Integer");
            put(4, "Float");
            put(5, "Double");
            put(8, "Boolean");
            put(9, "Byte");
            put(11, "Long");
            put(14, "Double");
        }
    };

    public Result<String> generateDubboJavaReq(String serviceName,String methodName,String groupName,String versionName){
        String result = "";

        Map<String, Object> map = new HashMap<>();

        if (StringUtils.isNotEmpty(groupName)){
            String group = "group = "+ '"'+groupName+ '"' +',';
            map.put("group",group);
        }else {
            map.put("group","");
        }

        if (StringUtils.isNotEmpty(versionName)){
            String version = "version = "+ '"'+versionName+ '"' + ',';
            map.put("version",version);
        }else {
            map.put("version","");
        }

        char[] serviceNameArr = serviceName.toCharArray();
        if (Character.isUpperCase(serviceNameArr[0])) {
            serviceNameArr[0] = Character.toLowerCase(serviceNameArr[0]);
        }

        char[] methodNameArr = methodName.toCharArray();
        if (!Character.isUpperCase(methodNameArr[0])) {
            methodNameArr[0] = Character.toUpperCase(methodNameArr[0]);
        }

        map.put("ServiceName",serviceName);
        map.put("serviceName",new String(serviceNameArr));
        map.put("methodName",methodName);
        map.put("MethodName",new String(methodNameArr));
        String template = FileUtils.getTemplate(DUBBO_REQUEST_EXAMPLE);
        result = FileUtils.renderTemplate(template, map);
        return Result.success(result);
    }

    public Result<String> generateJavaReq(String apiName, int paramType,int parentParamType, String reqParams) {

        String result = "";
        try {
            Map<String, Object> map = new HashMap<>();

            char[] apiNameArr = apiName.toCharArray();
            if (!Character.isUpperCase(apiNameArr[0])) {
                apiNameArr[0] = Character.toUpperCase(apiNameArr[0]);
            }
            map.put("apiName", new String(apiNameArr));
            StringBuilder sb = new StringBuilder();
            if (paramType == Constants.FormParamType) {
                //表单 k，v
                List<HttpFormParamsBo> paramList = gson.fromJson(reqParams, new TypeToken<List<HttpFormParamsBo>>() {
                }.getType());
                paramList.forEach(param -> {
                    genFormParamCode(sb, param);
                });

            } else if (paramType == Constants.JsonParamType) {
                //import-json
                List<HttpJsonParamsBo> paramList = gson.fromJson(reqParams, new TypeToken<List<HttpJsonParamsBo>>() {
                }.getType());
                if (parentParamType == 12) {
                    HttpJsonParamsBo paramsBo = paramList.get(0);
                    sb.append("List<");
                    sb.append(typeStringMap.getOrDefault(paramsBo.getParamType(), "Object"));
                    sb.append("> list").append("= new ArrayList<");
                    sb.append(typeStringMap.getOrDefault(paramsBo.getParamType(), "Object"));
                    sb.append(">(); \n");
                    recursiveAppender(1, sb, paramsBo, 0);
                    sb.append("req.setParamList(list); \n");
                } else {
                    //最外层为object
                    sb.append("Object rootObj").append("= new Object(); \n");
                    paramList.forEach(param -> recursiveAppender(2, sb, param, 1));
                    sb.append("req.setParamObj(rootObj); \n");
                }
            } else {
                sb.append("req.setBody(");
                sb.append(reqParams);
                sb.append(");");
            }
            map.put("requestStr", sb.toString());
            String template = FileUtils.getTemplate(JAVA_REQUEST_EXAMPLE);
            result = FileUtils.renderTemplate(template, map);
        } catch (Exception e) {
            log.error("HttpRequestGen failed ", e);
            return Result.fail(GeneralCodes.InternalError, "InternalError");
        }
        return Result.success(result);
    }

    public Result<String> generateCurlReq(int methodType, String apiPath, Integer paramsType, String reqParams, List<ApiHeaderBo> headers) {

        String result = "";
        Map<String, Object> map = new HashMap<>();
        String methodName = "POST";
        switch (methodType) {
            case 0:
                methodName = "POST";
                break;
            case 1:
                methodName = "GET";
                break;
            case 2:
                methodName = "PUT";
                break;
            case 3:
                methodName = "DELETE";
                break;
            case 4:
                methodName = "HEAD";
                break;
            case 5:
                methodName = "OPTS";
                break;
            case 6:
                methodName = "PATCH";
                break;
        }
        map.put("apiMethod", methodName);
        if (apiPath.startsWith("/")) {
            apiPath = apiPath.substring(1);
        }
        StringBuilder headerSb = new StringBuilder();
        if (Objects.nonNull(headers) && !headers.isEmpty()) {
            headerSb.append("-H '");
            List<String> headerList = new ArrayList<>();
            headers.forEach(header -> {
                String pair = header.getHeaderName() + ":" + header.getHeaderValue();
                headerList.add(pair);
            });
            String headersStr = StringUtils.join(headerList, ';');
            headerSb.append(headersStr).append("'").append("\\");
        }
        map.put("headers", headerSb.toString());
        StringBuilder paramSb = new StringBuilder();
        if (methodName.equalsIgnoreCase("GET")) {
            List<HttpFormParamsBo> paramList = gson.fromJson(reqParams, new TypeToken<List<HttpFormParamsBo>>() {
            }.getType());
            List<String> pairList = new ArrayList<>();
            paramList.forEach(param -> {
                String pair;
                if (!param.getParamValue().isEmpty()) {
                    pair = param.getParamKey() + "=" + param.getParamValue();
                } else {
                    pair = param.getParamKey() + "=" + genByType(param.getParamType()).toString();
                }
                pairList.add(pair);
            });
            if (!pairList.isEmpty()){
                apiPath += "?";
                apiPath += StringUtils.join(pairList,'?');
            }
        } else {
            if (paramsType == Constants.FormParamType) {
                List<HttpFormParamsBo> paramList = gson.fromJson(reqParams, new TypeToken<List<HttpFormParamsBo>>() {
                }.getType());
                paramList.forEach(param -> {
                    paramSb.append("-d ");
                    String pair;
                    if (!param.getParamValue().isEmpty()) {
                        pair = param.getParamKey() + "=" + param.getParamValue();
                    } else {
                        pair = param.getParamKey() + "=" + genByType(param.getParamType()).toString();
                    }
                    paramSb.append("'").append(pair).append("' \\ \n");
                });
            } else if (paramsType == Constants.JsonParamType || paramsType == Constants.JsonParamRaw) {
                paramSb.append("-d '");
                paramSb.append(reqParams).append("'").append("   \\  \n");
            }
        }
        map.put("apiPath", apiPath);
        map.put("params", paramSb.toString());
        String template = FileUtils.getTemplate(CURL_REQUEST_EXAMPLE);
        result = FileUtils.renderTemplate(template, map);
        return Result.success(result);
    }

    private StringBuilder genFormParamCode(StringBuilder sb, HttpFormParamsBo param) {
        sb.append("req.set");
        char[] paramArr = param.getParamKey().toCharArray();
        if (!Character.isUpperCase(paramArr[0])) {
            paramArr[0] = Character.toUpperCase(paramArr[0]);
        }
        sb.append(new String(paramArr));
        sb.append('(');
        appendByType(sb, param);
        sb.append("); \n");
        return sb;
    }

    private StringBuilder appendByType(StringBuilder sb, HttpParamBo param) {
        switch (param.getParamType()) {
            case 0:
                //string
                sb.append('"');
                if (StringUtils.isNotEmpty(param.getParamValue())) {
                    sb.append(param.getParamValue());
                } else {
                    sb.append(RandomStringUtils.randomAlphanumeric(10));
                }
                sb.append('"');
                break;
            case 3:
                //int
                if (StringUtils.isNotEmpty(param.getParamValue())) {
                    sb.append(param.getParamValue());
                } else {
                    sb.append(RandomUtils.nextInt());
                }
                break;
            case 4:
                //float
                if (StringUtils.isNotEmpty(param.getParamValue())) {
                    sb.append(param.getParamValue());
                } else {
                    sb.append(RandomUtils.nextFloat());
                }
                break;
            case 5 | 14:
                //double
                if (StringUtils.isNotEmpty(param.getParamValue())) {
                    sb.append(param.getParamValue());
                } else {
                    sb.append(RandomUtils.nextDouble());
                }
                break;
            case 8:
                //boolean
                if (StringUtils.isNotEmpty(param.getParamValue())) {
                    sb.append(param.getParamValue());
                } else {
                    sb.append(RandomUtils.nextBoolean());
                }
                break;
            case 11:
                //long
                if (StringUtils.isNotEmpty(param.getParamValue())) {
                    sb.append(param.getParamValue());
                } else {
                    sb.append(RandomUtils.nextLong());
                }
                break;
        }
        return sb;
    }

    private Object genByType(int paramType) {
        switch (paramType) {
            case 0:
                //string
                return RandomStringUtils.randomAlphanumeric(10);
            case 3:
                //int
                return RandomUtils.nextInt();
            case 4:
                //float
                return RandomUtils.nextFloat();
            case 5 | 14:
                //double
                return RandomUtils.nextDouble();
            case 8:
                //boolean
                return RandomUtils.nextBoolean();
            case 11:
                //long
                return RandomUtils.nextLong();
        }
        return "";
    }

    //parentType 1 array 2 obj
    private StringBuilder recursiveAppender(int parentType, StringBuilder sb, HttpJsonParamsBo param, int flag) {
        //基本类型
        if (param.getParamType() != 12 && param.getParamType() != 13) {
            //array
            if (parentType == 1) {
                sb.append("List<").append(typeStringMap.getOrDefault(param.getParamType(), "Object"));
                sb.append("> list").append(flag).append("= new ArrayList<");
                sb.append(typeStringMap.getOrDefault(param.getParamType(), "Object")).append(">(); \n");
                sb.append("list").append(flag).append(".add(");
                appendByType(sb, param);
                sb.append("); \n");
                sb.append("list").append(flag).append(".add(");
                appendByType(sb, param);
                sb.append("); \n");
            } else if (parentType == 2) {
                //obj
                sb.append("rootObj").append(".set");
                char[] paramArr = param.getParamKey().toCharArray();
                if (!Character.isUpperCase(paramArr[0])) {
                    paramArr[0] = Character.toUpperCase(paramArr[0]);
                }
                sb.append(new String(paramArr)).append("(");
                appendByType(sb, param);
                sb.append("); \n");
            }
        }
        if (param.getParamType() == 12) {
            //array
            sb.append("List<");
            sb.append(typeStringMap.getOrDefault(param.getParamType(), "Object"));
            sb.append("> list").append(flag).append("= new ArrayList<");
            sb.append(typeStringMap.getOrDefault(param.getParamType(), "Object"));
            sb.append(">(); \n");
            flag++;
            recursiveAppender(1, sb, param.getChildList().get(0), flag);
        } else if (param.getParamType() == 13) {
            sb.append("Object ").append(param.getParamKey()).append("= new Object(); \n");
            if (parentType == 1) {
                if (flag == 0) {
                    sb.append("list").append(".add(").append(param.getParamKey()).append("); \n");
                } else {
                    sb.append("list").append(flag - 1).append("+.add(").append(param.getParamKey()).append("); \n");
                }
            } else {
                sb.append("rootObj").append(".setObj(").append(param.getParamKey()).append("); \n");
            }

            int finalFlag1 = flag;
            param.getChildList().forEach(subParam -> {
                if (Objects.isNull(subParam.getChildList()) || subParam.getChildList().isEmpty()) {
                    sb.append(param.getParamKey()).append(".set");
                    char[] subParamArr = subParam.getParamKey().toCharArray();
                    if (!Character.isUpperCase(subParamArr[0])) {
                        subParamArr[0] = Character.toUpperCase(subParamArr[0]);
                    }
                    sb.append(new String(subParamArr)).append("(");
                    appendByType(sb, subParam);
                    sb.append("); \n");
                } else {
                    recursiveAppender(2, sb, subParam, finalFlag1 + 1);
                }
            });
        }
        return sb;
    }
}
