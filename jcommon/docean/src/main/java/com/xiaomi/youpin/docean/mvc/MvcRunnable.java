package com.xiaomi.youpin.docean.mvc;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.xiaomi.youpin.docean.Mvc;
import com.xiaomi.youpin.docean.common.Cons;
import com.xiaomi.youpin.docean.common.Safe;
import com.xiaomi.youpin.docean.listener.event.Event;
import com.xiaomi.youpin.docean.listener.event.EventType;
import com.xiaomi.youpin.docean.mvc.download.Download;
import com.xiaomi.youpin.docean.mvc.util.ExceptionUtil;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 * @date 2022/4/9 20:13
 */
@Slf4j
public class MvcRunnable implements Runnable {

    private MvcContext context;

    private MvcRequest request;

    private MvcResponse response;

    private ConcurrentHashMap<String, HttpRequestMethod> requestMethodMap;

    private Mvc mvc;

    private Gson gson = new Gson();

    public MvcRunnable(Mvc mvc, MvcContext context, MvcRequest request, MvcResponse response, ConcurrentHashMap<String, HttpRequestMethod> requestMethodMap) {
        this.context = context;
        this.request = request;
        this.response = response;
        this.requestMethodMap = requestMethodMap;
        this.mvc = mvc;
    }

    @Override
    public void run() {
        Safe.run(() -> {
            try {
                log.debug("call mvc path:{}", request.getPath());
                ContextHolder.getContext().set(context);
                if (context.isWebsocket()) {
                    WsRequest req = new Gson().fromJson(new String(request.getBody()), WsRequest.class);
                    request.setPath(req.getPath());
                    request.setBody(new Gson().toJson(req.getParams()).getBytes());
                }
                String path = request.getPath();
                MvcResult<Object> result = new MvcResult<>();
                HttpRequestMethod method = this.requestMethodMap.get(path);
                //支持文件下载(/download) 并且必须开启下载
                if (isDownload(path) && mvc.getMvcConfig().isDownload()) {
                    new Download().download(context, request, response);
                    return;
                }

                //支持上传文件(/upload)
                if (isUpload(path)) {
                    Map<String, String> m = new HashMap<>(2);
                    if (null != request.getParams()) {
                        m.putAll(request.getParams());
                    }
                    m.put("fileName", new String(request.getBody()));
                    mvc.getIoc().publishEvent(new Event(EventType.mvcUploadFinish, m));
                    response.writeAndFlush(context, "upload success");
                    return;
                }

                //支持模糊匹配
                if (null == method) {
                    String[] array = path.split("/");
                    if (array.length > 1) {
                        array[array.length - 1] = "*";
                        String p = Joiner.on("/").join(array);
                        method = this.requestMethodMap.get(p);
                    }
                }
                //多层次模糊匹配(/a/** 匹配 /a/b/c /a/b/d)
                if (null == method) {
                    final String p = path;
                    Optional<Map.Entry<String, HttpRequestMethod>> optional = this.requestMethodMap.entrySet().stream().filter(it -> {
                        String key = it.getKey();
                        if (key.endsWith("/**")) {
                            key = key.replace("/**", "");
                            return p.startsWith(key);
                        }
                        return false;
                    }).findAny();
                    if (optional.isPresent()) {
                        method = optional.get().getValue();
                    }
                }
                //支持指定path执行
                if (null != method) {
                    mvc.callMethod(context, request, response, result, method);
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
                    response.writeAndFlush(context, gson.toJson(result));
                    return;
                }
                //直接调用服务
                mvc.callService(context, request, response);
            } finally {
                ContextHolder.getContext().close();
            }
        }, ex -> {
            MvcResult<String> mr = new MvcResult();
            Throwable unwrapThrowable = ExceptionUtil.unwrapThrowable(ex);
            mr.setMessage(unwrapThrowable.toString());
            mr.setCode(500);
            response.writeAndFlush(context, gson.toJson(mr));
        });
    }

    private boolean isDownload(String path) {
        return path.equals("/download");
    }


    public static final boolean isUpload(String path) {
        return path.equals("/upload");
    }


    private boolean isFaviconIco(MvcRequest request) {
        return request.getPath().equals("/favicon.ico");
    }
}
