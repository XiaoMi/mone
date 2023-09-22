package com.xiaomi.youpin.docean.mvc;

import com.google.gson.Gson;
import com.xiaomi.youpin.docean.Mvc;
import com.xiaomi.youpin.docean.common.Cons;
import com.xiaomi.youpin.docean.common.Safe;
import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.docean.mvc.common.MvcConst;
import com.xiaomi.youpin.docean.mvc.download.Download;
import com.xiaomi.youpin.docean.mvc.upload.MvcUpload;
import com.xiaomi.youpin.docean.mvc.util.ExceptionUtil;
import com.xiaomi.youpin.docean.mvc.util.MethodFinder;
import com.xiaomi.youpin.docean.mvc.util.RequestUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import jdk.incubator.concurrent.ScopedValue;
import lombok.extern.slf4j.Slf4j;

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

    private static Gson gson = new Gson();

    public MvcRunnable(Mvc mvc, MvcContext context, MvcRequest request, MvcResponse response, ConcurrentHashMap<String, HttpRequestMethod> requestMethodMap) {
        this.context = context;
        this.request = request;
        this.response = response;
        this.requestMethodMap = requestMethodMap;
        this.mvc = mvc;
    }


    public MvcRunnable(Mvc mvc, HttpServerConfig config, ChannelHandlerContext ctx, FullHttpRequest httpRequest, String path, byte[] body, ConcurrentHashMap<String, HttpRequestMethod> requestMethodMap) {
        String method = httpRequest.method().name();
        this.context = new MvcContext();
        this.request = new MvcRequest();
        this.response = new MvcResponse();
        this.context.setRequest(httpRequest);
        this.context.setMethod(method);
        this.context.setHandlerContext(ctx);
        this.context.setCookie(config.isCookie());
        this.request.setHeaders(RequestUtils.headers(httpRequest));
        this.request.setUri(httpRequest.uri());
        this.context.setHeaders(this.request.getHeaders());
        this.context.setVirtualThread(mvc.getMvcConfig().isVirtualThread());
        this.request.setMethod(method);
        this.request.setPath(path);
        this.request.setBody(body);
        this.response.setCtx(ctx);
        this.mvc = mvc;
        this.requestMethodMap = requestMethodMap;
    }


    @Override
    public void run() {
        Safe.run(() -> {
            log.debug("call mvc path:{}", request.getPath());
            if (mvc.getMvcConfig().isVirtualThread()) {
                ScopedValue.where(MvcConst.MVC_CONTEXT, context).run(() -> {
                    call();
                });
            } else {
                ContextHolder.getContext().set(context);
                try {
                    call();
                } finally {
                    ContextHolder.getContext().close();
                }
            }
        }, ex -> {
            MvcResult<String> mr = new MvcResult();
            Throwable unwrapThrowable = ExceptionUtil.unwrapThrowable(ex);
            mr.setMessage(unwrapThrowable.toString());
            mr.setCode(500);
            response.writeAndFlush(context, gson.toJson(mr));
        });
    }

    private void call() {
        if (context.isWebsocket()) {
            WsRequest req = new Gson().fromJson(new String(request.getBody()), WsRequest.class);
            request.setPath(req.getPath());
            request.setBody(new Gson().toJson(req.getParams()).getBytes());
        }
        //Directly returning not found when searching for favicon.ico.
        if (isFaviconIco(request)) {
            response.writeAndFlush(context, HttpResponseStatus.NOT_FOUND, "");
            return;
        }
        String path = request.getPath();

        //Support file download (/download) and must enable downloads.
        if (isDownload(path) && mvc.getMvcConfig().isDownload()) {
            Download.download(context, request, response);
            return;
        }

        //Support uploading files (/upload)
        if (isUpload(path)) {
            MvcUpload.upload(mvc, request, response, context);
            return;
        }

        HttpRequestMethod method = MethodFinder.find(path, this.requestMethodMap);
        //rate limited or exceeded quota
        if (null != method) {
            mvc.callMethod(context, request, response, new MvcResult<>(), method);
            return;
        }

        if (!path.equals(Cons.Service)) {
            MvcResult<Object> result = new MvcResult<>();
            result.setCode(HttpResponseStatus.NOT_FOUND.code());
            result.setMessage(HttpResponseStatus.NOT_FOUND.reasonPhrase());
            response.writeAndFlush(context, gson.toJson(result));
            return;
        }
        //rate limited or exceeded quota
        mvc.callService(context, request, response);
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
