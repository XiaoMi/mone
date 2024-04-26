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

package com.xiaomi.youpin.docean;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.bo.Bean;
import com.xiaomi.youpin.docean.bo.MvcConfig;
import com.xiaomi.youpin.docean.common.MethodInvoker;
import com.xiaomi.youpin.docean.common.NamedThreadFactory;
import com.xiaomi.youpin.docean.common.Safe;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.docean.exception.DoceanException;
import com.xiaomi.youpin.docean.listener.event.Event;
import com.xiaomi.youpin.docean.listener.event.EventType;
import com.xiaomi.youpin.docean.mvc.*;
import com.xiaomi.youpin.docean.mvc.common.MvcConst;
import com.xiaomi.youpin.docean.mvc.util.ExceptionUtil;
import com.xiaomi.youpin.docean.mvc.util.GsonUtils;
import com.xiaomi.youpin.docean.mvc.util.Jump;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2020/6/20
 */
@Slf4j
@Data
public class Mvc {

    private ExecutorService executor;

    private ConcurrentHashMap<String, HttpRequestMethod> requestMethodMap = new ConcurrentHashMap<>();

    private Ioc ioc;

    private static Gson gson = new Gson();

    private MvcConfig mvcConfig = new MvcConfig();

    private MethodInvoker methodInvoker = new MethodInvoker();

    private String name = "mvc";

    private Mvc(Ioc ioc) {
        this.ioc = ioc;
        this.name = this.ioc.getName();
        setConfig(ioc);
        this.executor = createPool();
        initHttpRequestMethod();
    }

    private void setConfig(Ioc ioc) {
        this.mvcConfig.setAllowCross(Boolean.valueOf(ioc.getBean(MvcConst.ALLOW_CROSS_DOMAIN, MvcConst.FALSE)));
        this.mvcConfig.setDownload(Boolean.valueOf(ioc.getBean(MvcConst.MVC_DOWNLOAD, MvcConst.FALSE)));
        this.mvcConfig.setUseCglib(Boolean.valueOf(ioc.getBean(MvcConst.CGLIB, MvcConst.TRUE)));

        this.mvcConfig.setOpenStaticFile(Boolean.valueOf(ioc.getBean(MvcConst.OPEN_STATIC_FILE, MvcConst.FALSE)));
        this.mvcConfig.setStaticFilePath(ioc.getBean(MvcConst.STATIC_FILE_PATH, MvcConst.EMPTY));

        this.mvcConfig.setResponseOriginalValue(Boolean.valueOf(ioc.getBean(MvcConst.RESPONSE_ORIGINAL_VALUE, MvcConst.FALSE)));
        this.mvcConfig.setPoolSize(Integer.valueOf(ioc.getBean(MvcConst.MVC_POOL_SIZE, String.valueOf(MvcConst.DEFAULT_MVC_POOL_SIZE))));
        this.mvcConfig.setVirtualThread(Boolean.valueOf(ioc.getBean(MvcConst.VIRTUAL_THREAD, MvcConst.TRUE)));
        this.mvcConfig.setResponseOriginalPath(ioc.getBean(MvcConst.RESPONSE_ORIGINAL_PATH, ""));
        ioc.publishEvent(new Event(EventType.mvcBegin, this.mvcConfig));
    }

    private ExecutorService createPool() {
        if (mvcConfig.isVirtualThread()) {
            return Executors.newVirtualThreadPerTaskExecutor();
        } else {
            return new ThreadPoolExecutor(this.mvcConfig.getPoolSize(), this.mvcConfig.getPoolSize(), 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(HttpServerConfig.HTTP_POOL_QUEUE_SIZE), new NamedThreadFactory("docean_mvc"));
        }
    }

    private void initHttpRequestMethod() {
        ioc.beans().entrySet().stream().forEach(entry -> {
            Bean bean = entry.getValue();
            if (bean.getType() == Bean.Type.controller.ordinal()) {
                registerControllerMethods(bean);
            }
            if (bean.getObj() instanceof MvcServlet) {
                initializeControllerMapping(bean);
            }
        });
        ioc.publishEvent(new Event(EventType.initControllerFinish, this.requestMethodMap));
        log.info("requestMethodMap size:{}", this.requestMethodMap.size());
    }

    private void initializeControllerMapping(Bean bean) {
        MvcServlet ms = (MvcServlet) bean.getObj();
        String path = ms.path();
        HttpRequestMethod hrm = new HttpRequestMethod();
        hrm.setPath(path);
        hrm.setObj(ms);
        hrm.setHttpMethod(ms.method());
        Safe.runAndLog(() -> hrm.setMethod(ms.getClass().getMethod("execute", Object.class)));
        ioc.publishEvent(new Event(EventType.initController, path));
        requestMethodMap.put(path, hrm);
    }

    private void registerControllerMethods(Bean bean) {
        Arrays.stream(bean.getClazz().getMethods()).forEach(m -> Optional.ofNullable(m.getAnnotation(RequestMapping.class)).ifPresent(rm -> {
            //支持类上添加RequestMapping
            RequestMapping classMapping = bean.getClazz().getAnnotation(RequestMapping.class);
            String path = rm.path();
            if (Optional.ofNullable(classMapping).isPresent()) {
                path = classMapping.path() + path;
            }
            HttpRequestMethod hrm = new HttpRequestMethod();
            hrm.setTimeout(rm.timeout());
            hrm.setPath(path);
            hrm.setObj(bean.getObj());
            hrm.setMethod(m);
            hrm.setHttpMethod(rm.method());
            hrm.setGenericSuperclassTypeArguments(getGenericSuperclassTypeArguments(bean.getClazz()));
            ioc.publishEvent(new Event(EventType.initController, path));
            requestMethodMap.put(path, hrm);
        }));
    }

    public static Map<String, Class> getGenericSuperclassTypeArguments(Class clazz) {
        List<String> list = Arrays.stream(clazz.getSuperclass().getTypeParameters()).map(it -> it.getName()).collect(Collectors.toList());
        if (list.size() == 0) {
            return Maps.newHashMap();
        }
        Map<String, Class> map = new HashMap<>();
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            for (int i = 0; i < typeArguments.length; i++) {
                Type argument = typeArguments[i];
                if (argument instanceof Class<?>) {
                    map.put(list.get(i), (Class) argument);
                }
            }
        }
        return map;
    }


    private static final class LazyHolder {
        private static final Mvc ins = new Mvc(Ioc.ins());
    }

    public static final Mvc ins() {
        return LazyHolder.ins;
    }

    public static final Mvc create(Ioc ioc) {
        return new Mvc(ioc);
    }

    public void dispatcher(MvcContext context, MvcRequest request, MvcResponse response) {
        context.setAllowCross(mvcConfig.isAllowCross());
        if (context.isSync()) {
            new MvcRunnable(this, context, request, response, requestMethodMap).run();
        } else {
            executor.submit(new MvcRunnable(this, context, request, response, requestMethodMap));
        }
    }

    public void dispatcher(HttpServerConfig config, ChannelHandlerContext ctx, FullHttpRequest httpRequest, String uri, byte[] body) {
        executor.submit(new MvcRunnable(this, config, ctx, httpRequest, uri, body, requestMethodMap));
    }


    public void callService(MvcContext context, MvcRequest request, MvcResponse response) {
        MvcRequest req = gson.fromJson(new String(request.getBody()), MvcRequest.class);
        request.setServiceName(req.getServiceName());
        request.setMethodName(req.getMethodName());
        request.setArguments(req.getArguments());
        Object controller = ioc.getBean(request.getServiceName());
        Object[] params = methodInvoker.getMethodParams(controller, request.getMethodName(), request.getArguments());
        MvcServlet ms = (MvcServlet) controller;
        HttpRequestMethod method = this.requestMethodMap.get(ms.path());
        Object res = this.mvcConfig.isUseCglib() ? methodInvoker.invokeFastMethod(controller, controller.getClass(), request.getMethodName(), params) :
                methodInvoker.invokeMethod(controller, method.getMethod(), params);

        MvcResult mr = new MvcResult();
        mr.setData(res);
        response.writeAndFlush(context, new Gson().toJson(mr));
    }

    public List<Class> mapMethodParametersToClasses(Method method, Map<String, Class> map) {
        return Arrays.stream(method.getParameters()).map(it -> {
            String name = it.getParameterizedType().getTypeName();
            if ((it.getType() instanceof Object) && map.containsKey(name)) {
                return map.get(name);
            }
            return it.getType();
        }).collect(Collectors.toList());
    }

    public void callMethod(MvcContext context, MvcRequest request, MvcResponse response, MvcResult<Object> result, HttpRequestMethod method) {
        Safe.run(() -> {
            Object[] params = getMethodParams(context, request, method);

            Object data = invokeControllerMethod(method, params);

            if (context.isSync()) {
                context.setResponse(data);
                return;
            }

            if (data instanceof MvcResult) {
                MvcResult<String> mr = (MvcResult) data;
                // use akka to handle
                if (mr.getCode() == -999) {
                    return;
                }
                // need to jump (302)
                if (mr.getCode() == HttpResponseStatus.FOUND.code()) {
                    Jump.jump(response, mr.getData());
                    return;
                }
            }
            // directly returns the result of the construction
            if (data instanceof FullHttpResponse) {
                FullHttpResponse res = (FullHttpResponse) data;
                response.getCtx().writeAndFlush(HttpResponseUtils.create(res));
                return;
            }
            // get whether the configuration returns an unwrapped value
            boolean needOriginalValue = this.mvcConfig.isResponseOriginalValue();
            if (!needOriginalValue && StringUtils.isNotBlank(this.mvcConfig.getResponseOriginalPath())) {
                needOriginalValue = Arrays.stream(mvcConfig.getResponseOriginalPath().split(","))
                        .anyMatch(i -> i.equals(method.getPath()));
            }
            if (needOriginalValue) {
                String responseData = data instanceof String ? (String) data : gson.toJson(data);
                response.writeAndFlush(context, responseData);
            } else {
                result.setData(data);
                response.writeAndFlush(context, gson.toJson(result));
            }
        }, ex -> {
            if (context.isSync()) {
                context.setResponse(ex);
                return;
            }
            Throwable unwrapThrowable = ExceptionUtil.unwrapThrowable(ex);
            result.setCode(500);
            int httpCode = 200;
            if (ex instanceof DoceanException de) {
                int code = de.getCode();
                if (0 != code) {
                    result.setCode(code);
                    httpCode = code;
                }
            }
            result.setMessage(unwrapThrowable.getMessage());
            response.writeAndFlush(context, gson.toJson(result), httpCode);
        });
    }

    private Object[] getMethodParams(MvcContext context, MvcRequest request, HttpRequestMethod method) {
        Object[] params = new Object[]{null};
        //If there is only one parameter and it is a String, no further parsing is necessary; it can be used directly.
        if (isSingleStringParameterMethod(method) && request.getMethod().toUpperCase().equals("POST")) {
            params[0] = new String(request.getBody());
        } else {
            JsonElement args = getArgs(method, request.getMethod().toLowerCase(Locale.ROOT), request, context);
            if (isSingleMvcContextParameterMethod(method)) {
                params[0] = context;
            } else {
                try {
                    Class[] types = getClasses(method);
                    //参数有可能从session中取
                    params = methodInvoker.getMethodParams(args, types, name -> {
                        Object obj = context.session().getAttribute(name);
                        //提取失败,用户需要登录
                        if (null == obj) {
                            throw new DoceanException("You are required to log in first.", 401);
                        }
                        return obj;
                    });
                } catch (DoceanException doceanException) {
                    throw doceanException;
                } catch (Exception e) {
                    log.error("getMethodParams error,path:{},params:{},method:{}", context.getPath(),
                            GsonUtils.gson.toJson(context.getParams()), request.getMethod().toLowerCase(Locale.ROOT), e);
                }
            }
        }
        return params;
    }

    private Class[] getClasses(HttpRequestMethod method) {
        //可能方法中有泛型,这里给fix调,用实际的Class
        List<Class> list = mapMethodParametersToClasses(method.getMethod(), method.getGenericSuperclassTypeArguments());
        Class[] types = list.toArray(new Class[]{});
        return types;
    }

    private Object invokeControllerMethod(HttpRequestMethod method, Object[] params) {
        Object data = this.mvcConfig.isUseCglib() ? methodInvoker.invokeFastMethod(method.getObj(), method.getMethod(), params) :
                methodInvoker.invokeMethod(method.getObj(), method.getMethod(), params);
        return data;
    }

    private static boolean isSingleMvcContextParameterMethod(HttpRequestMethod method) {
        return method.getMethod().getParameterTypes().length == 1 && method.getMethod().getParameterTypes()[0].equals(MvcContext.class);
    }

    private static boolean isSingleStringParameterMethod(HttpRequestMethod method) {
        return method.getMethod().getParameterTypes().length == 1 && method.getMethod().getParameterTypes()[0].equals(String.class);
    }

    /**
     * parsing parameters
     *
     * @param method
     * @param httpMethod
     * @return
     */
    private JsonElement getArgs(HttpRequestMethod method, String httpMethod, MvcRequest req, MvcContext context) {
        if (httpMethod.equalsIgnoreCase("get")) {
            return Get.getParams(method, req.getUri(), context);
        } else if (httpMethod.equalsIgnoreCase("post")) {
            return Post.getParams(method, req.getBody(), context);
        } else {
            throw new DoceanException("don't support:" + httpMethod);
        }
    }

    private void setMvcContext(MvcContext context, Object[] params) {
        if (params.length > 0 && null != params[0] && params[0].getClass() == MvcContext.class) {
            params[0] = context;
        }
    }

    public void destory() {
        this.methodInvoker.clear();
    }
}
