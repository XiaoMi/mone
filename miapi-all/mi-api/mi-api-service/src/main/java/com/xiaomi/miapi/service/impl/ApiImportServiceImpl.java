package com.xiaomi.miapi.service.impl;

import com.google.gson.*;
import com.xiaomi.miapi.pojo.Api;
import com.xiaomi.miapi.pojo.ApiGroup;
import com.xiaomi.miapi.dto.HttpFormParamBo;
import com.xiaomi.miapi.dto.HttpJsonParamBo;
import com.xiaomi.miapi.mapper.ApiGroupMapper;
import com.xiaomi.miapi.service.ApiImportService;
import com.xiaomi.miapi.service.HttpApiService;
import com.xiaomi.miapi.service.MockService;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.exception.CommonError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class ApiImportServiceImpl implements ApiImportService {

    @Autowired
    ApiGroupMapper apiGroupMapper;

    @Autowired
    HttpApiService httpApiService;

    @Autowired
    private MockService mockService;

    public static final Gson gson = new Gson();

    public JsonParser parser = new JsonParser();

    @Transactional
    public Result<Integer> importSwagger(Integer projectID, String data, boolean randomGen, String userName) {
        int apiCount = 0;
        JsonObject swagger = (JsonObject) parser.parse(data);
        if (swagger != null) {
            JsonObject paths = swagger.getAsJsonObject("paths");
            if (Objects.nonNull(paths)) {
                //record group info
                Map<String, Integer> groupInfo = new HashMap<>();
                for (String apiURI : paths.keySet()) {
                    JsonObject apiInfoList = paths.getAsJsonObject(apiURI);
                    for (String requestType : apiInfoList.keySet()) {
                        JsonObject apiInfo = apiInfoList.getAsJsonObject(requestType);
                        Api api = new Api();
                        if (Objects.isNull(apiInfo.get("summary"))) {
                            api.setApiName(apiInfo.get("operationId").getAsString());
                        } else {
                            api.setApiName(apiInfo.get("summary").getAsString());
                        }
                        String groupName = apiInfo.getAsJsonArray("tags").get(0).getAsString();
                        ApiGroup apiGroup = apiGroupMapper.getGroupByName(projectID, groupName);
                        if (Objects.isNull(apiGroup)) {
                            if (Objects.isNull(groupInfo.get(groupName))) {
                                apiGroup = new ApiGroup();
                                apiGroup.setGroupName(groupName);
                                apiGroup.setProjectID(projectID);
                                apiGroup.setSystemGroup(false);
                                apiGroup.setGroupDesc("import from swagger");
                                if (apiGroupMapper.addApiGroup(apiGroup) < 1) {
                                    log.error("import from swagger,add group error,groupName:{},projectID:{}", groupName, projectID);
                                    continue;
                                }
                                groupInfo.put(groupName, apiGroup.getGroupID());
                            }
                        } else {
                            groupInfo.put(groupName, apiGroup.getGroupID());
                        }
                        api.setApiURI(apiURI);
                        api.setApiProtocol(Consts.HTTP_API_TYPE);
                        switch (requestType) {
                            case "post":
                                api.setApiRequestType(0);
                                break;
                            case "get":
                                api.setApiRequestType(1);
                                break;
                            case "put":
                                api.setApiRequestType(2);
                                break;
                            case "delete":
                                api.setApiRequestType(3);
                                break;
                            case "head":
                                api.setApiRequestType(4);
                                break;
                            case "options":
                                api.setApiRequestType(5);
                                break;
                            case "patch":
                                api.setApiRequestType(6);
                                break;
                            default:
                                api.setApiRequestType(0);
                        }
                        if (Objects.isNull(api.getApiRequestRaw())) {
                            api.setApiRequestRaw("");
                        }
                        if (Objects.isNull(api.getApiRemark())) {
                            api.setApiRemark("");
                        }
                        if (Objects.isNull(api.getApiDesc())) {
                            api.setApiDesc("");
                        }

                        api.setGroupID(groupInfo.get(groupName));
                        api.setProjectID(projectID);
                        api.setUpdateUsername(userName);
                        api.setApiRequestParamType(Consts.FORM_DATA_TYPE);
                        api.setApiResponseParamType(Consts.JSON_DATA_TYPE);
                        List<Map<String, Object>> apiHeader = new ArrayList<>();
                        JsonArray consumes = apiInfo.getAsJsonArray("consumes");
                        if (consumes != null) {
                            for (Object headerValue : consumes) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("headerName", "Content-Type");
                                map.put("headerValue", String.valueOf(headerValue));
                                apiHeader.add(map);
                            }
                        }
                        JsonArray produces = apiInfo.getAsJsonArray("produces");
                        if (produces != null) {
                            for (Object headerValue : produces) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("headerName", "Accept");
                                map.put("headerValue", String.valueOf(headerValue));
                                apiHeader.add(map);
                            }
                        }

                        List<HttpFormParamBo> formParams = new ArrayList<>();
                        List<HttpJsonParamBo> jsonParams = new ArrayList<>();
                        if (apiInfo.getAsJsonArray("parameters") != null) {
                            try {
                                JsonArray parameters = apiInfo.getAsJsonArray("parameters");
                                for (Iterator<JsonElement> iterator = parameters.iterator(); iterator.hasNext(); ) {
                                    JsonObject parameter = (JsonObject) iterator.next();
                                    int paramType = 0;
                                    //maybe header info
                                    if ("header".equals(parameter.get("in").getAsString())) {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("headerName", parameter.get("name").getAsString());
                                        map.put("headerValue", "");
                                        apiHeader.add(map);
                                    } else if ("path".equals((parameter.get("in").getAsString())) || "query".equals((parameter.get("in").getAsString())) || "formData".equals((parameter.get("in").getAsString()))) {
                                        String typeStr = parameter.get("type").getAsString();
                                        if (StringUtils.isNotEmpty(typeStr)) {
                                            paramType = stringType2IntType(typeStr);
                                        }
                                        HttpFormParamBo formParam = new HttpFormParamBo();
                                        if (parameter.get("description") != null){
                                            formParam.setParamName(parameter.get("description").getAsString());
                                        }else {
                                            formParam.setParamName(parameter.get("name").getAsString());
                                        }
                                        formParam.setParamKey(parameter.get("name").getAsString());
                                        if (randomGen){
                                            formParam.setParamValue(mockService.generateParamValue(paramType, "").toString());
                                        }else {
                                            formParam.setParamValue(mockService.generateDefaultValue(paramType).toString());
                                        }
                                        formParam.setParamType(String.valueOf(paramType));
                                        parameter.get("required").getAsBoolean();
                                        formParam.setParamNotNull(parameter.get("required").getAsBoolean());
                                        formParams.add(formParam);
                                        api.setApiRequestParamType(Consts.FORM_DATA_TYPE);
                                    } else if ("body".equals(parameter.get("in").getAsString())) {
                                        if (Objects.nonNull(parameter.getAsJsonObject("schema")) && Objects.nonNull(parameter.getAsJsonObject("schema").get("$ref"))) {
                                            HttpJsonParamBo jsonParam = parseSwaggerBody(swagger, parameter.getAsJsonObject("schema"),randomGen,0);
                                            jsonParams.add(jsonParam);
                                            api.setApiRequestParamType(Consts.JSON_DATA_TYPE);
                                        } else if (Objects.nonNull(parameter.getAsJsonObject("schema"))) {
                                            api.setApiRequestParamType(Consts.JSON_DATA_TYPE);
                                            HttpJsonParamBo jsonParamsBo = new HttpJsonParamBo();
                                            jsonParamsBo.setParamKey("root");
                                            jsonParamsBo.setParamName("root node");
                                            jsonParamsBo.setParamType("13");
                                            parseSchemaBody(jsonParamsBo, parameter.getAsJsonObject("schema"),randomGen);
                                            jsonParams.add(jsonParamsBo);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                log.warn("import apiName:{},error,cause:{}",api.getApiName(),e.getMessage());
                                continue;
                            }
                        }else if (apiInfo.getAsJsonObject("requestBody") != null){
                            api.setApiRequestParamType(Consts.JSON_DATA_TYPE);
                            HttpJsonParamBo jsonParamsBo = new HttpJsonParamBo();
                            JsonObject schema = parseReqBody(apiInfo.getAsJsonObject("requestBody"));
                            if (Objects.nonNull(schema)){
                                jsonParamsBo = parseSwaggerBodyV3(swagger, schema,randomGen,0);
                            }
                            jsonParams.add(jsonParamsBo);
                        }

                        List<HttpJsonParamBo> jsonResultParams = new ArrayList<>();
                        JsonObject responses = apiInfo.getAsJsonObject("responses");
                        if (responses != null ) {
                            try {
                                for (String paramKey : responses.keySet()) {
                                    if (!paramKey.equals("200")){
                                        continue;
                                    }
                                    HttpJsonParamBo jsonRspParam = new HttpJsonParamBo();
                                    HttpJsonParamBo jsonParamsBo = new HttpJsonParamBo();
                                    jsonParamsBo.setParamKey("root");
                                    jsonParamsBo.setParamName("root node");
                                    jsonParamsBo.setParamType("13");
                                    JsonObject parameter = responses.getAsJsonObject(paramKey);
                                    if (Objects.nonNull(parameter.getAsJsonObject("schema")) && Objects.nonNull(parameter.getAsJsonObject("schema").get("$ref"))) {
                                        jsonRspParam = parseSwaggerRespBody(swagger, parameter,randomGen);
                                        jsonResultParams.add(jsonRspParam);
                                        break;
                                    } else if (Objects.nonNull(parameter.getAsJsonObject("schema"))) {
                                        jsonRspParam = parseSchemaBody(jsonParamsBo, parameter.getAsJsonObject("schema"),randomGen);
                                    }else if (Objects.nonNull(parameter.getAsJsonObject("content"))){
                                        jsonRspParam = parseSwaggerRespBody2(swagger, parameter,randomGen);
                                    }
                                    jsonResultParams.add(jsonRspParam);
                                }
                            }catch (Exception e) {
                                log.warn("import apiName:{},error,cause:{}",api.getApiName(),e.getMessage());
                                continue;
                            }
                        }

                        Map<String, Object> cache = new HashMap<>();
                        if (api.getApiRequestParamType().equals(Consts.JSON_DATA_TYPE)) {
                            cache.put("requestInfo", jsonParams);
                        } else {
                            cache.put("requestInfo", formParams);
                        }
                        httpApiService.addHttpApi(api, gson.toJson(apiHeader), gson.toJson(cache.get("requestInfo")), gson.toJson(jsonResultParams), Consts.IMPORT_SWAGGER_FLAG,randomGen);
                        apiCount++;
                    }
                }
                return Result.success(apiCount);
            }
        }
        return Result.fail(CommonError.UnknownError);
    }

    private JsonObject parseReqBody(JsonObject requestBody){
        JsonObject content = requestBody.getAsJsonObject("content");
        if (Objects.nonNull(content)){
            JsonObject applicationJson = content.getAsJsonObject("application/json");
            if (Objects.nonNull(applicationJson)){
                return applicationJson.getAsJsonObject("schema");
            }
        }
        return null;
    }
    private HttpJsonParamBo parseSwaggerRespBody(JsonObject swagger, JsonObject parameter,boolean randomGen) {
        JsonObject schema = parameter.getAsJsonObject("schema");

        HttpJsonParamBo resultJson = new HttpJsonParamBo();

        if (Objects.nonNull(schema)) {
            resultJson.setParamKey("");
            if (Objects.nonNull(schema.get("type")) && StringUtils.isNotEmpty(schema.get("type").getAsString())) {
                resultJson.setParamType(String.valueOf(stringType2IntType(schema.get("type").getAsString())));
            }
            if (Objects.equals(resultJson.getParamType(), "12")) {
                //array
                List<HttpJsonParamBo> paramsBos = new ArrayList<>();
                if (Objects.nonNull(schema.get("items").getAsString())) {
                    HttpJsonParamBo tempBo = parseSwaggerBody(swagger, schema,randomGen,0);
                    paramsBos.add(tempBo);
                }
                resultJson.setChildList(paramsBos);
            }
            if (Objects.nonNull(schema.get("$ref").getAsString())) {
                resultJson = parseSwaggerBody(swagger, schema,randomGen,0);
            }

        } else {
            resultJson.setParamType("0");
            resultJson.setParamKey("");
            if (StringUtils.isNotEmpty(parameter.get("description").getAsString())) {
                resultJson.setParamValue(parameter.get("description").getAsString());
            }
        }
        return resultJson;
    }

    private HttpJsonParamBo parseSwaggerRespBody2(JsonObject swagger, JsonObject parameter,boolean randomGen) {
        HttpJsonParamBo resultJson = new HttpJsonParamBo();
        JsonObject content = parameter.getAsJsonObject("content");

        if (Objects.isNull(content) || Objects.isNull(content.getAsJsonObject("*/*"))){
            return resultJson;
        }
        JsonObject schema = content.getAsJsonObject("*/*").getAsJsonObject("schema");

        if (Objects.isNull(schema)){
            return resultJson;
        }
        String ref = schema.get("$ref").getAsString();
        if (!ref.startsWith("#/components/schemas")){
            return resultJson;
        }
        String filed = ref.substring(("#/components/schemas".length()+1));
        if (Objects.nonNull(swagger.getAsJsonObject("components")) && Objects.nonNull(swagger.getAsJsonObject("components").getAsJsonObject("schemas"))){
            schema = swagger.getAsJsonObject("components").getAsJsonObject("schemas").getAsJsonObject(filed);
        }
        resultJson = parseSchema(swagger,schema,randomGen);
        return resultJson;
    }

    private HttpJsonParamBo parseSchema(JsonObject swagger,JsonObject schema,boolean randomGen){
        HttpJsonParamBo resultJson = new HttpJsonParamBo();
        if (Objects.nonNull(schema)) {
            resultJson.setParamKey("root");
            if (Objects.nonNull(schema.get("type").getAsString())) {
                resultJson.setParamType(String.valueOf(stringType2IntType(schema.get("type").getAsString())));
            }
            if (Objects.equals(resultJson.getParamType(), "12")) {
                //array
                List<HttpJsonParamBo> paramsBos = new ArrayList<>();
                if (Objects.nonNull(schema.get("items").getAsString())) {
                    HttpJsonParamBo tempBo = parseSwaggerBody(swagger, schema,randomGen,0);
                    paramsBos.add(tempBo);
                }
                resultJson.setChildList(paramsBos);
            } else if (Objects.equals(resultJson.getParamType(), "13")){
                //object
                List<HttpJsonParamBo> paramsBos = new ArrayList<>();
                if (Objects.nonNull(schema.getAsJsonObject("properties"))){
                    schema.getAsJsonObject("properties").entrySet().forEach(entry -> {
                        HttpJsonParamBo paramBo = parseProperties(swagger,entry,randomGen);
                        paramsBos.add(paramBo);
                    });
                    resultJson.setChildList(paramsBos);
                }
            }
        }
        return resultJson;
    }

    private HttpJsonParamBo parseProperties(JsonObject swagger,Map.Entry<String,JsonElement> entry,boolean randomGen){
        HttpJsonParamBo paramBo = new HttpJsonParamBo();
        paramBo.setParamKey(entry.getKey());
        if (Objects.nonNull(entry.getValue().getAsJsonObject().get("type"))){
            paramBo.setParamType(String.valueOf(stringType2IntType(entry.getValue().getAsJsonObject().get("type").getAsString())));
        }
        if (Objects.nonNull(entry.getValue().getAsJsonObject().get("description"))){
            paramBo.setParamNote(entry.getValue().getAsJsonObject().get("description").getAsString());
        }
        List<HttpJsonParamBo> childList = new ArrayList<>();
        if (Objects.nonNull(entry.getValue().getAsJsonObject().get("$ref"))){
            paramBo.setParamType("13");
            String childSchema = entry.getValue().getAsJsonObject().get("$ref").getAsString();
            String filed = childSchema.substring(("#/components/schemas".length()+1));
            JsonObject schemas = swagger.getAsJsonObject("components").getAsJsonObject("schemas").getAsJsonObject(filed);
            HttpJsonParamBo child = parseSchema(swagger,schemas,randomGen);
            childList = child.getChildList();
        }
        if (!childList.isEmpty()){
            paramBo.setChildList(childList);
        }
        return paramBo;
    }

    private HttpJsonParamBo parseSwaggerBodyV3(JsonObject swagger, JsonObject schema,boolean randomGen,int layer) {
        HttpJsonParamBo jsonParamsBo = new HttpJsonParamBo();

        if (layer >= 10){
            return jsonParamsBo;
        }
        if (Objects.nonNull(schema.get("$ref"))) {
            String ref = schema.get("$ref").getAsString();
            String entityName = "";
            if (StringUtils.isNotEmpty(ref)) {
                entityName = ref.substring(ref.lastIndexOf("/") + 1);
            }
            JsonObject components = swagger.getAsJsonObject("components");
            if (Objects.nonNull(components)) {
                JsonObject schemas = components.getAsJsonObject("schemas");
                if (Objects.nonNull(schemas) && Objects.nonNull(schemas.getAsJsonObject(entityName))) {
                    JsonObject entity = schemas.getAsJsonObject(entityName);
                    List<String> requiredList = new ArrayList<>();
                    if (Objects.nonNull(entity.getAsJsonArray("required"))) {
                        entity.getAsJsonArray("required").forEach(jsonElement -> requiredList.add(jsonElement.getAsString()));
                    }
                    List<HttpJsonParamBo> childList = new ArrayList<>();
                    jsonParamsBo.setParamKey("root");
                    jsonParamsBo.setParamName("root node");
                    jsonParamsBo.setParamType("13");
                    if (Objects.nonNull(entity.getAsJsonObject("properties"))) {
                        for (String entityKey : entity.getAsJsonObject("properties").keySet()) {
                            JsonObject subParamObj = entity.getAsJsonObject("properties").getAsJsonObject(entityKey);
                            HttpJsonParamBo param = new HttpJsonParamBo();
                            param.setParamKey(entityKey);
                            if ((subParamObj.get("$ref") == null || StringUtils.isEmpty(subParamObj.get("$ref").getAsString())) && (subParamObj.get("type") != null && !subParamObj.get("type").getAsString().equals("array"))) {
                                String type = "string";
                                //基本类型
                                if (StringUtils.isNotEmpty(subParamObj.get("type").getAsString())) {
                                    type = subParamObj.get("type").getAsString();
                                }
                                if (subParamObj.get("description") != null && StringUtils.isNotEmpty(subParamObj.get("description").getAsString())) {
                                    param.setParamNote(subParamObj.get("description").getAsString());
                                }
                                param.setParamType(String.valueOf(stringType2IntType(type)));
                                if (randomGen) {
                                    param.setParamValue(mockService.generateParamValue(stringType2IntType(subParamObj.get("type").getAsString()), "").toString());
                                } else {
                                    param.setParamValue(mockService.generateDefaultValue(stringType2IntType(subParamObj.get("type").getAsString())).toString());
                                }
//                        param.setParamNote("");
                            } else if ((subParamObj.get("type") != null && subParamObj.get("type").getAsString().equals("array"))) {
                                //数组
                                param.setParamType(String.valueOf(stringType2IntType("array")));
                                List<HttpJsonParamBo> items = new ArrayList<>();
                                HttpJsonParamBo item = parseSwaggerBody(swagger, subParamObj.getAsJsonObject("items"), randomGen,0);
                                items.add(item);
                                param.setChildList(items);
                            } else {
                                //复合类型,递归构造
                                param = parseSwaggerBody(swagger, subParamObj, randomGen,0);
                            }
                            if (requiredList.contains(entityKey)) {
                                param.setParamNotNull(true);
                            }
                            childList.add(param);
                        }
                    }
                    jsonParamsBo.setChildList(childList);
                }
            }
        }else if (Objects.nonNull(schema.get("type")) && schema.get("type").getAsString().equals("array")){
            //数组
            if (Objects.nonNull(schema.get("items"))){
                jsonParamsBo.setParamKey("root");
                jsonParamsBo.setParamName("root node");
                jsonParamsBo.setParamType("12");
                HttpJsonParamBo child = parseSwaggerBodyV3(swagger,schema.getAsJsonObject("items"),randomGen,layer+1);
                List<HttpJsonParamBo> childList = new ArrayList<>();
                childList.add(child);
                jsonParamsBo.setChildList(childList);
            }
        }
        return jsonParamsBo;
    }

    private HttpJsonParamBo parseSwaggerBody(JsonObject swagger, JsonObject schema,boolean randomGen,int layer) {
        HttpJsonParamBo jsonParamsBo = new HttpJsonParamBo();
        if (layer >= 10){
            return jsonParamsBo;
        }
        try {
            if (Objects.nonNull(schema.get("$ref"))) {
                String ref = schema.get("$ref").getAsString();
                String entityName = "";
                if (StringUtils.isNotEmpty(ref)) {
                    entityName = ref.substring(ref.lastIndexOf("/") + 1);
                }
                JsonObject definitions = swagger.getAsJsonObject("definitions");
                if (Objects.nonNull(definitions) && Objects.nonNull(definitions.getAsJsonObject(entityName))) {
                    JsonObject entity = definitions.getAsJsonObject(entityName);
                    List<String> requiredList = new ArrayList<>();
                    if (Objects.nonNull(entity.getAsJsonArray("required"))) {
                        entity.getAsJsonArray("required").forEach(jsonElement -> requiredList.add(jsonElement.getAsString()));
                    }
                    List<HttpJsonParamBo> childList = new ArrayList<>();
                    jsonParamsBo.setParamKey("root");
                    jsonParamsBo.setParamName("root node");
                    jsonParamsBo.setParamType("13");
                    if (Objects.nonNull(entity.getAsJsonObject("properties"))) {
                        for (String entityKey : entity.getAsJsonObject("properties").keySet()) {
                            JsonObject subParamObj = entity.getAsJsonObject("properties").getAsJsonObject(entityKey);
                            HttpJsonParamBo param = new HttpJsonParamBo();
                            param.setParamKey(entityKey);
                            if ((subParamObj.get("$ref") == null || StringUtils.isEmpty(subParamObj.get("$ref").getAsString())) && (subParamObj.get("type") != null && !subParamObj.get("type").getAsString().equals("array"))) {
                                String type = "string";
                                //基本类型
                                if (StringUtils.isNotEmpty(subParamObj.get("type").getAsString())) {
                                    type = subParamObj.get("type").getAsString();
                                }
                                if (subParamObj.get("description") != null && StringUtils.isNotEmpty(subParamObj.get("description").getAsString())) {
                                    param.setParamNote(subParamObj.get("description").getAsString());
                                }
                                param.setParamType(String.valueOf(stringType2IntType(type)));
                                if (randomGen) {
                                    param.setParamValue(mockService.generateParamValue(stringType2IntType(subParamObj.get("type").getAsString()), "").toString());
                                } else {
                                    param.setParamValue(mockService.generateDefaultValue(stringType2IntType(subParamObj.get("type").getAsString())).toString());
                                }
    //                        param.setParamNote("");
                            } else if ((subParamObj.get("type") != null && subParamObj.get("type").getAsString().equals("array"))) {
                                //数组
                                param.setParamType(String.valueOf(stringType2IntType("array")));
                                List<HttpJsonParamBo> items = new ArrayList<>();
                                HttpJsonParamBo item = parseSwaggerBody(swagger, subParamObj.getAsJsonObject("items"), randomGen,layer+1);
                                items.add(item);
                                param.setChildList(items);
                            } else {
                                //复合类型,递归构造
                                param = parseSwaggerBody(swagger, subParamObj, randomGen,layer+1);
                            }
                            if (requiredList.contains(entityKey)) {
                                param.setParamNotNull(true);
                            }
                            childList.add(param);
                        }
                    }
                    jsonParamsBo.setChildList(childList);
                }
            }else if (Objects.nonNull(schema.getAsJsonPrimitive("type"))){
                jsonParamsBo.setParamKey("item");
                jsonParamsBo.setParamType(Integer.toString(stringType2IntType(schema.getAsJsonPrimitive("type").getAsString())));
            }
        } catch (Exception ignored) {
        }
        return jsonParamsBo;
    }

    /**
     * normal type body
     *
     */
    private HttpJsonParamBo parseSchemaBody(HttpJsonParamBo parentBo, JsonObject schema,boolean randomGen) {
        if (Objects.isNull(schema)) {
            return parentBo;
        }
        List<String> requiredList = new ArrayList<>();
        if (Objects.nonNull(schema.getAsJsonArray("required"))) {
            schema.getAsJsonArray("required").forEach(jsonElement -> requiredList.add(jsonElement.getAsString()));
        }
        List<HttpJsonParamBo> childList = new ArrayList<>();

        if (Objects.nonNull(schema.getAsJsonObject("properties"))) {
            //obj
            for (String entityKey : schema.getAsJsonObject("properties").keySet()) {
                JsonObject paramValueObj = schema.getAsJsonObject("properties").getAsJsonObject(entityKey);
                HttpJsonParamBo param = new HttpJsonParamBo();
                param.setParamKey(entityKey);
                if (!(paramValueObj.get("type").getAsString().equals("array") || paramValueObj.get("type").getAsString().equals("object"))) {
                    String type = "string";
                    //normal type
                    if (StringUtils.isNotEmpty(paramValueObj.get("type").getAsString())) {
                        type = paramValueObj.get("type").getAsString();
                    }
                    if (paramValueObj.get("description") != null && StringUtils.isNotEmpty(paramValueObj.get("description").getAsString())){
                        param.setParamNote(paramValueObj.get("description").getAsString());
                    }
                    param.setParamType(String.valueOf(stringType2IntType(type)));
                    if (requiredList.contains(entityKey)) {
                        param.setParamNotNull(true);
                    }
                    if (randomGen){
                        param.setParamValue(mockService.generateDefaultValue(stringType2IntType(paramValueObj.get("type").getAsString())).toString());
                    }else {
                        param.setParamValue(mockService.generateParamValue(stringType2IntType(paramValueObj.get("type").getAsString()), "").toString());
                    }
                } else {
                    param.setParamType(String.valueOf(stringType2IntType(paramValueObj.get("type").getAsString())));
                    if (requiredList.contains(entityKey)) {
                        param.setParamNotNull(true);
                    }
                    parseSchemaBody(param, paramValueObj,randomGen);
                }
                if (StringUtils.isNotEmpty(paramValueObj.get("description").getAsString())) {
                    param.setParamNote(paramValueObj.get("description").getAsString());
                }
                childList.add(param);
            }
        } else if (Objects.nonNull(schema.getAsJsonObject("items"))) {
            JsonObject items = schema.getAsJsonObject("items");
            HttpJsonParamBo param = new HttpJsonParamBo();
            if (items.get("type") == null){
                param.setParamType(String.valueOf(stringType2IntType(schema.get("type").getAsString())));
            }else {
                param.setParamType(String.valueOf(stringType2IntType(items.get("type").getAsString())));
            }
            param.setParamName("item");
            param.setParamKey("item");
            parseSchemaBody(param, items,randomGen);
            childList.add(param);
        }
        parentBo.setChildList(childList);
        return parentBo;
    }


    private int stringType2IntType(String strType) {
        switch (strType) {
            case "int":
            case "integer":
                return 3;
            case "float":
                return 4;
            case "double":
                return 5;
            case "boolean":
                return 8;
            case "byte":
                return 9;
            case "short":
                return 10;
            case "long":
                return 11;
            case "array":
                return 12;
            case "object":
                return 13;
            case "number":
                return 14;
            default:
                return 0;
        }
    }
}
