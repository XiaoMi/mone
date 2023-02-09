/*
 * Copyright 2020 XiaoMi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the following link.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaomi.mone.http.docs.core;

import com.google.gson.Gson;
import com.xiaomi.miapi.api.service.bo.BeatInfo;
import com.xiaomi.miapi.api.service.bo.HttpDocDataBo;
import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.core.beans.HttpApiCacheItem;
import com.xiaomi.mone.http.docs.core.beans.HttpLayerItem;
import com.xiaomi.mone.http.docs.core.beans.HttpModuleCacheItem;
import com.xiaomi.mone.http.docs.providers.HttpDocProvider;
import com.xiaomi.mone.http.docs.util.ClassTypeUtil;
import com.xiaomi.mone.http.docs.util.HttpUtils;
import com.xiaomi.mone.http.docs.util.IpUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.xiaomi.mone.http.docs.core.Constants.*;

/**
 * Scan and process http doc annotations.
 */
public class HttpApiScanner implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpApiScanner.class);

    @Autowired
    private ApplicationContext applicationContext;

    public static final Gson gson = new Gson();

    private final ScheduledExecutorService scheduledPool = Executors.newSingleThreadScheduledExecutor();

    /**
     * doc data update notify url suffix
     */
    public static final String NOTIFY_URL = "/OpenApi/httpApiUpdateNotify";

    /**
     * module heart beat url suffix
     */
    public static final String MODULE_BEAT_URL = "/OpenApi/beat";

    /**
     * http data push url suffix
     */
    public static final String PUSH_DATA_URL = "/OpenApi/pushHttpDocData";

    /**
     * mi-api platform address
     */
    @Value("${MiApi.miApiAddr:http://127.0.0.1:8080}")
    public String miApiAddr;

    @Value("${MiApi.opUser:default_user}")
    private String opUser;

    @Value("${MiApi.updateMsg:auto_update}")
    private String updateMsg;

    @Value("${MiApi.autoUpdate:false}")
    private boolean autoUpdate;

    /**
     * staging or online
     */
    @Value("${MiApi.env:staging}")
    public String DEFAULT_ENV = "staging";

    @Value("${server.port:8080}")
    private int port;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        // Register http doc provider
        LOGGER.info("================= HTTP API Docs--Start scanning and processing doc annotations ================");

        String moduleBeatUrl = miApiAddr + MODULE_BEAT_URL;
        String notifyUrl = miApiAddr + NOTIFY_URL;
        String pushDataUrl = miApiAddr + PUSH_DATA_URL;

        Map<String, Object> httpApiModule = applicationContext.getBeansWithAnnotation(HttpApiModule.class);
        List<String> serviceNameList = new ArrayList<>(httpApiModule.size());

        httpApiModule.forEach((key, apiModuleTemp) -> {
            Class<?> apiModuleClass;
            if (AopUtils.isAopProxy(apiModuleTemp)) {
                apiModuleClass = AopUtils.getTargetClass(apiModuleTemp);
            } else {
                apiModuleClass = apiModuleTemp.getClass();
            }
            HttpApiModule moduleAnn = apiModuleClass.getAnnotation(HttpApiModule.class);
            if (!apiModuleClass.isAnnotationPresent(Controller.class) && !apiModuleClass.isAnnotationPresent(RestController.class)) {
                LOGGER.warn("【Warning】" + apiModuleClass.getName() + " @HttpApiModule annotation is used, " +
                        "but it is not a http controller (without " + Controller.class.getName() + " annotation)");
                return;
            }
            HttpModuleCacheItem moduleCacheItem = new HttpModuleCacheItem();
            HttpApiDocsCache.addApiModule(moduleAnn.apiController().getCanonicalName(), moduleCacheItem);

            moduleCacheItem.setHttpModuleDocName(moduleAnn.value());
            //interface name containing package path
            moduleCacheItem.setHttpModuleClassName(moduleAnn.apiController().getCanonicalName());
            //for notify
            serviceNameList.add(moduleAnn.apiController().getCanonicalName());

            Method[] apiModuleMethods = apiModuleClass.getMethods();
            // API basic information list in module cache
            List<HttpApiCacheItem> httpModuleApiList = new ArrayList<>(apiModuleMethods.length);
            moduleCacheItem.setHttpModuleApiList(httpModuleApiList);

            for (Method method : apiModuleMethods) {
                if (method.isAnnotationPresent(HttpApiDoc.class)) {
                    processHttpApiDocAnnotation(method, httpModuleApiList, moduleAnn);
                }
            }
        });

        LOGGER.info("================= HTTP API Docs-- doc annotations scanning and processing completed ================");

        String host = null;
        try {
            host = System.getenv("host.ip") == null ? IpUtil.getLocalHost() : System.getenv("host.ip");
        } catch (Exception e) {
            LOGGER.warn("get local host failed:{}", e.getMessage());
        }
        if (host == null) {
            LOGGER.error("parse local host failed,please set system env: host.ip=xxx.xxx.xxx.xx");
            return;
        }
        //controller info,beat per 30s
        String finalHost = host;
        scheduledPool.scheduleAtFixedRate(() -> beat(moduleBeatUrl,serviceNameList, finalHost), 0, 30, TimeUnit.SECONDS);

        //push http doc data
        try {
            HttpDocDataBo httpDocDataBo = new HttpDocDataBo();
            httpDocDataBo.setAddress(host + ":" + port);
            httpDocDataBo.setHttpApiModuleInfo(HttpDocProvider.allHttpApiModuleInfo());
            httpDocDataBo.setHttpApiModuleListAndApiInfo(gson.toJson(HttpDocProvider.httpApiModuleListAndApiInfo()));
            httpDocDataBo.setHttpApiParamsResponseInfo(HttpDocProvider.allHttpApiParamsResponseInfo());

            Map<String, String> map = new HashMap<>();
            map.put("Content-Type", "application/json");
            LOGGER.info("================= push http data address:" + httpDocDataBo.getAddress());
            HttpUtils.post(PUSH_DATA_URL, map, gson.toJson(httpDocDataBo), 3000);
        } catch (Exception e) {
            LOGGER.error("push http doc data failed,cause by:{}", e.getMessage());
        }

        if (autoUpdate) {
            String finalHost1 = host;
            new Thread(() -> serviceNameList.forEach(serviceName -> {
                Map<String, String> header = new HashMap<>();
                header.put("form_data", "true");
                Map<String, String> body = new HashMap<>();
                if (!opUser.isEmpty()) {
                    body.put("opUsername", opUser);
                }
                if (!updateMsg.isEmpty()) {
                    body.put("updateMsg", updateMsg);
                }
                body.put("env", DEFAULT_ENV);
                body.put("apiController", serviceName);
                body.put("ip", finalHost1);
                body.put("port", String.valueOf(port));
                try {
                    if (!finalHost1.isEmpty() && port != 0) {
                        HttpUtils.post(NOTIFY_URL, header, gson.toJson(body), 3000);
                    }
                } catch (Exception ignored) {
                }
            })).start();
        }
    }


    private void processHttpApiDocAnnotation(Method method, List<HttpApiCacheItem> moduleApiList, HttpApiModule httpModuleAnn) {
        HttpApiDoc httpApi = method.getAnnotation(HttpApiDoc.class);

        //http api basic info
        HttpApiCacheItem basicHttpItem = new HttpApiCacheItem();
        moduleApiList.add(basicHttpItem);

        if (Objects.isNull(httpApi.apiName()) || httpApi.apiName().isEmpty()) {
            basicHttpItem.setApiName(method.getName());
        } else {
            basicHttpItem.setApiName(httpApi.apiName());
        }
        basicHttpItem.setApiMethodName(method.getName());
        basicHttpItem.setApiMethod(httpApi.method().name());
        basicHttpItem.setApiTag(httpModuleAnn.value());
        basicHttpItem.setApiPath(httpApi.value());
        basicHttpItem.setDescription(httpApi.description());

        //http api detail info
        HttpApiCacheItem detailHttpItem = new HttpApiCacheItem();
        String key = String.format("%s.%s", httpModuleAnn.apiController().getCanonicalName(), method.getName());
        HttpApiDocsCache.addApiParamsAndResp(key, detailHttpItem);

        Class<?>[] argsClass = method.getParameterTypes();
        Type[] parametersTypes = method.getGenericParameterTypes();
        List<HttpLayerItem> paramLayerList = new ArrayList<>();
        //the first layer
        HttpLayerItem responseLayer = new HttpLayerItem("root", method.getReturnType(), method.getGenericReturnType());
        detailHttpItem.setApiName(httpApi.apiName());
        detailHttpItem.setApiTag(httpModuleAnn.value());
        detailHttpItem.setApiPath(httpApi.value());
        detailHttpItem.setApiMethod(httpApi.method().name());
        detailHttpItem.setDescription(httpApi.description());

        HttpLayerItem respLayer = processLayer(responseLayer);
        detailHttpItem.setResponseLayer(responseLayer);

        //default res data
        detailHttpItem.setResponse(gson.toJson(initWithDefaultValue(respLayer)));
        detailHttpItem.setParamsLayerList(paramLayerList);

        //anno list on method
        Annotation[][] methodAnno = method.getParameterAnnotations();

        //deal with the input param
        for (int index = 0; index < argsClass.length; index++) {
            Class<?> argClass = argsClass[index];
            Type parameterType = parametersTypes[index];
            if (ClassTypeUtil.isIgnoreType(argClass)) {
                continue;
            }
            HttpLayerItem paramLayer = new HttpLayerItem("arg_" + index, argClass, parameterType);
            HttpLayerItem layerItem = processLayer(paramLayer);
            HttpApiDocClassDefine httpApiDocClassDefine;
            for (Annotation annotation : methodAnno[index]) {
                if (annotation instanceof HttpApiDocClassDefine) {
                    httpApiDocClassDefine = (HttpApiDocClassDefine) annotation;
                    layerItem.setParamKey(httpApiDocClassDefine.value());
                    layerItem.setParamValue(httpApiDocClassDefine.defaultValue());
                    layerItem.setParamNotNull(httpApiDocClassDefine.required());
                    layerItem.setParamNote(httpApiDocClassDefine.description());
                }
            }
            paramLayerList.add(layerItem);
        }
    }

    private Object initWithDefaultValue(HttpLayerItem layerItem) {
        try {
            return initWithDefaultValue0(layerItem);
        } catch (Exception e) {
            LOGGER.warn("HttpApiDocsAnnotationScanner.initWithDefaultValue, error msg: " + e.getMessage());
            return EMPTY_OBJECT_INSTANCE;
        }
    }

    private Object initWithDefaultValue0(HttpLayerItem layerItem) {
        Class<?> classType = layerItem.getItemClass();
        String defaultValue = layerItem.getParamValue();
        if (Integer.class.isAssignableFrom(classType) || int.class.isAssignableFrom(classType)) {
            return (StringUtils.isEmpty(defaultValue) || !NumberUtils.isDigits(defaultValue)) ? 0 : Integer.parseInt(defaultValue);
        } else if (Byte.class.isAssignableFrom(classType) || byte.class.isAssignableFrom(classType)) {
            return StringUtils.isEmpty(defaultValue) ? (byte) 0 : defaultValue;
        } else if (Long.class.isAssignableFrom(classType) || long.class.isAssignableFrom(classType)) {
            return (StringUtils.isEmpty(defaultValue) || !NumberUtils.isDigits(defaultValue)) ? 0L : Long.parseLong(defaultValue);
        } else if (Double.class.isAssignableFrom(classType) || double.class.isAssignableFrom(classType)) {
            return (StringUtils.isEmpty(defaultValue) || !NumberUtils.isNumber(defaultValue)) ? 0.0D : Double.parseDouble(defaultValue);
        } else if (Float.class.isAssignableFrom(classType) || float.class.isAssignableFrom(classType)) {
            return (StringUtils.isEmpty(defaultValue) || !NumberUtils.isNumber(defaultValue)) ? 0.0F : Float.parseFloat(defaultValue);
        } else if (String.class.isAssignableFrom(classType)) {
            return StringUtils.isEmpty(defaultValue)
                    ? (StringUtils.isEmpty(layerItem.getParamValue()) ? "demoString" : layerItem.getParamValue())
                    : defaultValue;
        } else if (Character.class.isAssignableFrom(classType) || char.class.isAssignableFrom(classType)) {
            return StringUtils.isEmpty(defaultValue) ? 'c' : defaultValue;
        } else if (Short.class.isAssignableFrom(classType) || short.class.isAssignableFrom(classType)) {
            return (StringUtils.isEmpty(defaultValue) || !NumberUtils.isDigits(defaultValue)) ? (short) 0 : Short.parseShort(defaultValue);
        } else if (Boolean.class.isAssignableFrom(classType) || boolean.class.isAssignableFrom(classType)) {
            return !StringUtils.isEmpty(defaultValue) && Boolean.parseBoolean(defaultValue);
        } else if (Date.class.isAssignableFrom(classType)) {
            return StringUtils.isEmpty(defaultValue) ? "【" + Date.class.getName() + "】yyyy-MM-dd HH:mm:ss" : defaultValue;
        } else if (LocalDate.class.isAssignableFrom(classType)) {
            return StringUtils.isEmpty(defaultValue) ? "【" + LocalDate.class.getName() + "】yyyy-MM-dd" : defaultValue;
        } else if (LocalDateTime.class.isAssignableFrom(classType)) {
            return StringUtils.isEmpty(defaultValue) ? "【" + LocalDateTime.class.getName() + "】yyyy-MM-dd HH:mm:ss" : defaultValue;
        } else if (BigDecimal.class.isAssignableFrom(classType)) {
            return 0;
        } else if (BigInteger.class.isAssignableFrom(classType)) {
            return 0;
        } else if (Enum.class.isAssignableFrom(classType)) {
            Object[] enumConstants = classType.getEnumConstants();
            StringBuilder sb = new StringBuilder(ENUM_VALUES_SEPARATOR);
            try {
                Method getName = classType.getMethod(METHOD_NAME_NAME);
                for (Object obj : enumConstants) {
                    sb.append(getName.invoke(obj)).append(ENUM_VALUES_SEPARATOR);
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                LOGGER.error(e.getMessage(), e);
            }
            return sb.toString();
        } else if (Map.class.isAssignableFrom(classType)) {
            Map<Object, Object> resMap = new HashMap<>();
            resMap.put(initWithDefaultValue(layerItem.getChildList().get(0)), initWithDefaultValue(layerItem.getChildList().get(1)));
            return resMap;
        } else if (classType.isArray() || Collection.class.isAssignableFrom(classType)) {
            List<Object> resList = new ArrayList<>();
            resList.add(initWithDefaultValue(layerItem.getChildList().get(0)));
            return resList;
        } else {
            if (layerItem.getChildList() == null) {
                return EMPTY_OBJECT_INSTANCE;
            }

            Map<String, Object> res = new HashMap<>();
            for (HttpLayerItem perLayerItem : layerItem.getChildList()) {
                res.put(perLayerItem.getParamKey(), initWithDefaultValue(perLayerItem));
            }
            return res;
        }
    }

    private HttpLayerItem processLayer(HttpLayerItem layerItem) {
        try {
            Map<String, Type> genericParams = new HashMap<>();
            return processLayer0(layerItem, 1, genericParams, false);
        } catch (Exception e) {
            LOGGER.warn("[HttpApiDocsAnnotationScanner.processLayer], something wrong, message: {}", e.getMessage());
            return layerItem;
        }
    }

    private HttpLayerItem processLayer0(HttpLayerItem layerItem, int layer, Map<String, Type> genericParams, boolean isTerminal) {
        if (layerItem == null
                || ClassTypeUtil.isBaseType(layerItem.getItemClass())
                || isTerminal) {
            return layerItem;
        }
        int nowLayer = layer + 1;
        if (nowLayer > 10) {
            LOGGER.warn("[HttpApiDocsAnnotationScanner.processLayer0], The depth of bean has exceeded 10 layers, the deeper layer will be ignored! " +
                    "Please modify the parameter structure or check whether there is circular reference in bean!");
            return layerItem;
        }
        List<HttpLayerItem> layerItems = new ArrayList<>();
        layerItem.setChildList(layerItems);
        if (layerItem.getItemType() instanceof ParameterizedTypeImpl || ifSuperGeri(layerItem)) {
            if (List.class.isAssignableFrom(layerItem.getItemClass())
                    || Set.class.isAssignableFrom(layerItem.getItemClass())
                    || Queue.class.isAssignableFrom(layerItem.getItemClass())) {
                Type type = ((ParameterizedTypeImpl) layerItem.getItemType()).getActualTypeArguments()[0];
                HttpLayerItem paramLayerItem;
                paramLayerItem = initLayerItem("item", genericParams.getOrDefault(type.getTypeName(), type));
                layerItems.add(paramLayerItem);
                processLayer0(paramLayerItem, nowLayer, genericParams, false);
            } else if (Map.class.isAssignableFrom(layerItem.getItemClass())) {
                Type[] types = ((ParameterizedTypeImpl) layerItem.getItemType()).getActualTypeArguments();
                HttpLayerItem paramLayerItemKey = initLayerItem("key", types[0]);
                HttpLayerItem paramLayerItemValue = initLayerItem("value", types[1]);
                layerItems.add(paramLayerItemKey);
                processLayer0(paramLayerItemKey, nowLayer, genericParams, false);
                layerItems.add(paramLayerItemValue);
                processLayer0(paramLayerItemValue, nowLayer, genericParams, false);
            } else {
                // 泛型的处理
                Type[] types;
                if (ifSuperGeri(layerItem)) {
                    types = ((ParameterizedTypeImpl) layerItem.getItemClass().getGenericSuperclass()).getActualTypeArguments();
                    if (types.length > 0) {
                        TypeVariable[] typeParameters = ((ParameterizedTypeImpl) layerItem.getItemClass().getGenericSuperclass()).getRawType().getTypeParameters();
                        List<String> names = Arrays.stream(typeParameters).map(TypeVariable::getName).collect(Collectors.toList());
                        for (int i = 0; i < types.length && i < names.size(); i++) {
                            genericParams.put(names.get(i), types[i]);
                        }
                    }
                } else {
                    types = ((ParameterizedTypeImpl) layerItem.getItemType()).getActualTypeArguments();
                    if (types.length > 0) {
                        TypeVariable[] typeParameters = layerItem.getItemClass().getTypeParameters();
                        List<String> names = Arrays.stream(typeParameters).map(TypeVariable::getName).collect(Collectors.toList());
                        for (int i = 0; i < types.length && i < names.size(); i++) {
                            genericParams.put(names.get(i), types[i]);
                        }
                    }
                }

                List<Field> allFields = ClassTypeUtil.getAllFields(null, layerItem.getItemClass());
                if (allFields.size() > 0) {
                    for (Field field : allFields) {
                        if (SKIP_FIELD_SERIALVERSIONUID.equals(field.getName()) || SKIP_FIELD_THIS$0.equals(field.getName())) {
                            continue;
                        }
                        HttpLayerItem paramLayerItem;
                        if (genericParams.containsKey(field.getGenericType().getTypeName())) {
                            paramLayerItem = initLayerItem(field.getName(), genericParams.get(field.getGenericType().getTypeName()));
                        } else {
                            paramLayerItem = new HttpLayerItem(field.getName(), field.getType(), field.getGenericType());
                        }
                        layerItems.add(paramLayerItem);
                        if (field.isAnnotationPresent(HttpApiDocClassDefine.class)) {
                            // Handling @ApiDocClassDefine annotations on properties
                            HttpApiDocClassDefine httpApiDocClassDefine = field.getAnnotation(HttpApiDocClassDefine.class);
                            paramLayerItem.setParamName(httpApiDocClassDefine.value());
                            paramLayerItem.setParamValue(httpApiDocClassDefine.defaultValue());
                            paramLayerItem.setParamNotNull(httpApiDocClassDefine.required());
                            paramLayerItem.setParamNote(httpApiDocClassDefine.description());
                        }
                        processLayer0(paramLayerItem, nowLayer, genericParams, false);
                    }
                } else {
                    return layerItem;
                }
            }
        } else {
            if (Map.class.isAssignableFrom(layerItem.getItemClass()) || List.class.isAssignableFrom(layerItem.getItemClass())
                    || Set.class.isAssignableFrom(layerItem.getItemClass())
                    || Queue.class.isAssignableFrom(layerItem.getItemClass())) {
                return layerItem;
            }
            List<Field> allFields = ClassTypeUtil.getAllFields(null, layerItem.getItemClass());
            if (allFields.size() > 0) {
                for (Field field : allFields) {
                    if (SKIP_FIELD_SERIALVERSIONUID.equals(field.getName()) || SKIP_FIELD_THIS$0.equals(field.getName())) {
                        continue;
                    }
                    if (field.isAnnotationPresent(HttpApiDocClassDefine.class) && field.getAnnotation(HttpApiDocClassDefine.class).ignore()) {
                        continue;
                    }

                    boolean isTerminal0 = false;
                    if (field.getGenericType().getTypeName().contains(layerItem.getItemClass().getTypeName()) && !field.getGenericType().getTypeName().contains("$")) {
                        isTerminal0 = true;
                    }
                    HttpLayerItem paramLayerItem = new HttpLayerItem(field.getName(), field.getType(), field.getGenericType());
                    layerItems.add(paramLayerItem);
                    if (field.isAnnotationPresent(HttpApiDocClassDefine.class)) {
                        // Handling @ApiDocClassDefine annotations on properties
                        HttpApiDocClassDefine httpApiDocClassDefine = field.getAnnotation(HttpApiDocClassDefine.class);
                        paramLayerItem.setParamName(httpApiDocClassDefine.value());
                        paramLayerItem.setParamValue(httpApiDocClassDefine.defaultValue());
                        paramLayerItem.setParamNotNull(httpApiDocClassDefine.required());
                        paramLayerItem.setParamNote(httpApiDocClassDefine.description());
                    }
                    processLayer0(paramLayerItem, nowLayer, genericParams, isTerminal0);
                }
            } else {
                return layerItem;
            }
        }
        return layerItem;
    }

    private boolean ifSuperGeri(HttpLayerItem layerItem) {
        if (layerItem.getItemClass().getGenericSuperclass() != null && layerItem.getItemClass().getGenericSuperclass().getTypeName().startsWith("java.lang.Enum")) {
            return false;
        }
        return layerItem.getItemClass().getGenericSuperclass() != null && layerItem.getItemClass().getGenericSuperclass() instanceof ParameterizedTypeImpl;
    }

    private HttpLayerItem initLayerItem(String itemName, Type itemType) {
        if (itemType instanceof ParameterizedTypeImpl) {
            return new HttpLayerItem(itemName, ((ParameterizedTypeImpl) itemType).getRawType(), itemType);
        } else if (itemType instanceof Class) {
            return new HttpLayerItem(itemName, (Class) itemType, itemType);
        } else {
            return null;
        }
    }


    /**
     * do heartbeat controller for loading on platform
     */
    private void beat(String moduleBeatUrl,List<String> serviceNameList, String host) {
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        BeatInfo beatInfo = new BeatInfo();
        beatInfo.setModuleNames(serviceNameList);
        beatInfo.setAddress(host + ":" + port);
        try {
            HttpUtils.post(moduleBeatUrl, map, gson.toJson(beatInfo), 3000);
        } catch (Exception e) {
            LOGGER.error("do beat failed,beat info:{},cause:{}", beatInfo, e.getMessage());
        }
    }
}
