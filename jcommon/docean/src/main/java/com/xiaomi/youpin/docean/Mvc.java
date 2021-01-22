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
import com.xiaomi.youpin.docean.common.Cons;
import com.xiaomi.youpin.docean.common.MutableObject;
import com.xiaomi.youpin.docean.common.ReflectUtils;
import com.xiaomi.youpin.docean.common.Safe;
import com.xiaomi.youpin.docean.exception.DoceanException;
import com.xiaomi.youpin.docean.mvc.*;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * @author goodjava@qq.com
 * @date 2020/6/20
 */
@Slf4j
public class Mvc {

    private ThreadPoolExecutor executor;

    private ConcurrentHashMap<String, HttpRequestMethod> requestMethodMap = new ConcurrentHashMap<>();

    private Mvc() {
        executor = new ThreadPoolExecutor(500, 500, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1000));
        initHttpRequestMethod();
    }

    private void initHttpRequestMethod() {
        Ioc.ins().beans().entrySet().stream().forEach(entry -> {
            Bean bean = entry.getValue();
            if (bean.getType() == Bean.Type.controller.ordinal()) {
                Arrays.stream(bean.getClazz().getMethods()).forEach(m -> {
                    Optional.ofNullable(m.getAnnotation(RequestMapping.class)).ifPresent(rm -> {
                        String path = rm.path();
                        HttpRequestMethod hrm = new HttpRequestMethod();
                        hrm.setPath(path);
                        hrm.setObj(bean.getObj());
                        hrm.setMethod(m);
                        requestMethodMap.put(path, hrm);
                    });
                });
            }

        });
        log.info("requestMethodMap size:{}", this.requestMethodMap.size());
    }


    private static final class LazyHolder {
        private static final Mvc ins = new Mvc();
    }


    public static final Mvc ins() {
        return LazyHolder.ins;
    }


    public void dispatcher(MvcContext context, MvcRequest request, MvcResponse response) {
        executor.submit(() -> Safe.run(() -> {

            if (context.isWebsocket()) {
                WsRequest req = new Gson().fromJson(new String(request.getBody()), WsRequest.class);
                request.setPath(req.getPath());
                request.setBody(new Gson().toJson(req.getParams()).getBytes());
            }

            String path = request.getPath();

            MvcResult<Object> result = new MvcResult<>();
            HttpRequestMethod method = this.requestMethodMap.get(path);

            //支持指定path执行
            if (null != method) {
                callMethod(context, request, response, result, method);
                return;
            }

            //查找favicon.ioc 的直接返回找不到
            if (isFaviconIco(request)) {
                response.writeAndFlush(context, HttpResponseStatus.NOT_FOUND, "");
                return;
            }

            if (!path.equals(Cons.Service)) {
                result.setCode(HttpResponseStatus.NOT_FOUND.code());
                result.setMessage(HttpResponseStatus.NOT_FOUND.reasonPhrase());
                response.writeAndFlush(context, new Gson().toJson(result));
                return;
            }

            //直接调用服务
            callService(context, request, response);
        }, ex -> {
            MvcResult<String> mr = new MvcResult();
            mr.setMessage(ex.toString());
            mr.setCode(500);
            response.writeAndFlush(context, new Gson().toJson(mr));
        }));
    }

    private boolean isFaviconIco(MvcRequest request) {
        return request.getPath().equals("/favicon.ico");
    }

    private void callService(MvcContext context, MvcRequest request, MvcResponse response) {
        MvcRequest req = new Gson().fromJson(new String(request.getBody()), MvcRequest.class);
        request.setServiceName(req.getServiceName());
        request.setMethodName(req.getMethodName());
        request.setArguments(req.getArguments());
        Object controller = Ioc.ins().getBean(request.getServiceName());
        Object[] params = ReflectUtils.getMethodParams(controller, request.getMethodName(), request.getArguments());
        Object res = ReflectUtils.invokeFastMethod(controller, controller.getClass(), request.getMethodName(), params);
        MvcResult mr = new MvcResult();
        mr.setData(res);
        response.writeAndFlush(context, new Gson().toJson(mr));
    }

    private void callMethod(MvcContext context, MvcRequest request, MvcResponse response, MvcResult<Object> result, HttpRequestMethod method) {
        JsonElement arguments = new Gson().fromJson(new String(request.getBody()), JsonElement.class);
        Safe.run(() -> {
            RequestMapping rm = method.getMethod().getAnnotation(RequestMapping.class);
            String m = rm.method();
            MutableObject mo = getArgs(method, arguments, m);
            JsonElement args = mo.getObj();
            Object[] params = ReflectUtils.getMethodParams(method.getMethod(), args);
            setMvcContext(context, params);

            Object data = ReflectUtils.invokeFastMethod(method.getObj(), method.getMethod(), params);


            //需要跳转(302)
            if (data instanceof MvcResult) {
                MvcResult<String> mr = (MvcResult) data;
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
                response.getCtx().writeAndFlush( HttpResponseUtils.create(res));
                return;
            }


            result.setData(data);
            response.writeAndFlush(context, new Gson().toJson(result));
        }, ex -> {
            result.setCode(500);
            result.setMessage(ex.getMessage());
            response.writeAndFlush(context, new Gson().toJson(result));
        });
    }

    private MutableObject getArgs(HttpRequestMethod method, JsonElement arguments, String m) {
        MutableObject mo = new MutableObject();
        if (m.equals("get")) {
            mo.setObj(Get.getParams(method, arguments));
        } else if (m.equals("post")) {
            mo.setObj(Post.getParams(method, arguments));
        } else {
            throw new DoceanException(m);
        }
        return mo;
    }

    private void setMvcContext(MvcContext context, Object[] params) {
        if (params.length > 0 && params[0].getClass() == MvcContext.class) {
            params[0] = context;
        }
    }
}
