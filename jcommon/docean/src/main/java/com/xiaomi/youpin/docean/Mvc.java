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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.bo.Bean;
import com.xiaomi.youpin.docean.bo.MvcConfig;
import com.xiaomi.youpin.docean.common.*;
import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.docean.exception.DoceanException;
import com.xiaomi.youpin.docean.listener.event.Event;
import com.xiaomi.youpin.docean.listener.event.EventType;
import com.xiaomi.youpin.docean.mvc.*;
import com.xiaomi.youpin.docean.mvc.common.MvcConst;
import com.xiaomi.youpin.docean.mvc.util.ExceptionUtil;
import io.netty.handler.codec.http.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2020/6/20
 */
@Slf4j
public class Mvc {

    private ThreadPoolExecutor executor;

    @Getter
    private ConcurrentHashMap<String, HttpRequestMethod> requestMethodMap = new ConcurrentHashMap<>();

    @Getter
    private Ioc ioc;

    private static Gson gson = new Gson();

    @Getter
    private MvcConfig mvcConfig = new MvcConfig();

    private MethodInvoker methodInvoker = new MethodInvoker();

    private Mvc(Ioc ioc) {
        this.ioc = ioc;
        setConfig(ioc);
        initHttpRequestMethod();
    }

    private void setConfig(Ioc ioc) {
        this.mvcConfig.setAllowCross(Boolean.valueOf(ioc.getBean(MvcConst.ALLOW_CROSS_DOMAIN, MvcConst.FALSE)));
        this.mvcConfig.setDownload(Boolean.valueOf(ioc.getBean(MvcConst.MVC_DOWNLOAD, MvcConst.FALSE)));Boolean.valueOf(ioc.getBean(MvcConst.RESPONSE_ORIGINAL_VALUE, MvcConst.FALSE));
        this.mvcConfig.setUseCglib(Boolean.valueOf(ioc.getBean(MvcConst.CGLIB, MvcConst.TRUE)));
        this.mvcConfig.setResponseOriginalValue(Boolean.valueOf(ioc.getBean(MvcConst.RESPONSE_ORIGINAL_VALUE, MvcConst.FALSE)));
        this.mvcConfig.setPoolSize(Integer.valueOf(ioc.getBean(MvcConst.MVC_POOL_SIZE, String.valueOf(MvcConst.DEFAULT_MVC_POOL_SIZE))));
        if (mvcConfig.getPoolSize() > 0) {
            executor = new ThreadPoolExecutor(this.mvcConfig.getPoolSize(), this.mvcConfig.getPoolSize(), 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(HttpServerConfig.HTTP_POOL_QUEUE_SIZE), new NamedThreadFactory("docean_mvc"));
        }
        ioc.publishEvent(new Event(EventType.mvcBegin, this.mvcConfig));
    }

    private void initHttpRequestMethod() {
        ioc.beans().entrySet().stream().forEach(entry -> {
            Bean bean = entry.getValue();
            if (bean.getType() == Bean.Type.controller.ordinal()) {
                Arrays.stream(bean.getClazz().getMethods()).forEach(m -> {
                    Optional.ofNullable(m.getAnnotation(RequestMapping.class)).ifPresent(rm -> {
                        String path = rm.path();
                        HttpRequestMethod hrm = new HttpRequestMethod();
                        hrm.setTimeout(rm.timeout());
                        hrm.setPath(path);
                        hrm.setObj(bean.getObj());
                        hrm.setMethod(m);
                        hrm.setHttpMethod(rm.method());
                        ioc.publishEvent(new Event(EventType.initController, path));
                        requestMethodMap.put(path, hrm);
                    });
                });
            }
            if (bean.getObj() instanceof MvcServlet) {
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
        });
        log.info("requestMethodMap size:{}", this.requestMethodMap.size());
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

    public void callMethod(MvcContext context, MvcRequest request, MvcResponse response, MvcResult<Object> result, HttpRequestMethod method) {
        Safe.run(() -> {
            JsonElement arguments = gson.fromJson(new String(request.getBody()), JsonElement.class);
            String m = method.getHttpMethod();
            MutableObject mo = getArgs(method, arguments, m);
            Safe.run(() -> context.setParams(arguments));
            JsonElement args = mo.getObj();
            Object[] params = methodInvoker.getMethodParams(method.getMethod(), args);
            setMvcContext(context, params);
            Object data = this.mvcConfig.isUseCglib() ? methodInvoker.invokeFastMethod(method.getObj(), method.getMethod(), params) :
                    methodInvoker.invokeMethod(method.getObj(), method.getMethod(), params);

            if (context.isSync()) {
                context.setResponse(data);
                return;
            }

            if (data instanceof MvcResult) {
                MvcResult<String> mr = (MvcResult) data;
                //使用akka处理
                if (mr.getCode() == -999) {
                    return;
                }
                //需要跳转(302)
                if (mr.getCode() == HttpResponseStatus.FOUND.code()) {
                    FullHttpResponse response302 = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
                    response302.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
                    response302.headers().set(HttpHeaderNames.CONNECTION, "keep-alive");
                    response302.headers().set(HttpHeaderNames.LOCATION, mr.getData());
                    HttpUtil.setKeepAlive(response302, true);
                    response.getCtx().writeAndFlush(response302);
                    return;
                }
            }
            //直接返回构造的结果
            if (data instanceof FullHttpResponse) {
                FullHttpResponse res = (FullHttpResponse) data;
                response.getCtx().writeAndFlush(HttpResponseUtils.create(res));
                return;
            }
            // 获取配置是否返回没有包装的值
            if (this.mvcConfig.isResponseOriginalValue()) {
                if (data instanceof String) {
                    response.writeAndFlush(context, (String) data);
                } else {
                    response.writeAndFlush(context, gson.toJson(data));
                }
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
            result.setMessage(unwrapThrowable.getMessage());
            response.writeAndFlush(context, gson.toJson(result));
        });
    }

    /**
     * 解析参数
     *
     * @param method
     * @param arguments
     * @param m
     * @return
     */
    private MutableObject getArgs(HttpRequestMethod method, JsonElement arguments, String m) {
        MutableObject mo = new MutableObject();
        if (m.equalsIgnoreCase("get")) {
            mo.setObj(Get.getParams(method, arguments));
        } else if (m.equalsIgnoreCase("post")) {
            mo.setObj(Post.getParams(method, arguments));
        } else {
            throw new DoceanException(m);
        }
        return mo;
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
