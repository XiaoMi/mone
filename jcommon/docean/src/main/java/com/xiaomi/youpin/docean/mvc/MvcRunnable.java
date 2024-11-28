package com.xiaomi.youpin.docean.mvc;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.xiaomi.youpin.docean.Mvc;
import com.xiaomi.youpin.docean.bo.MvcConfig;
import com.xiaomi.youpin.docean.common.Cons;
import com.xiaomi.youpin.docean.common.Safe;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.docean.mvc.common.MvcConst;
import com.xiaomi.youpin.docean.mvc.download.Download;
import com.xiaomi.youpin.docean.mvc.html.Html;
import com.xiaomi.youpin.docean.mvc.upload.MvcUpload;
import com.xiaomi.youpin.docean.mvc.util.ExceptionUtil;
import com.xiaomi.youpin.docean.mvc.util.GsonUtils;
import com.xiaomi.youpin.docean.mvc.util.MethodFinder;
import com.xiaomi.youpin.docean.mvc.util.RequestUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
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

    private MvcConfig config;

    private ConcurrentHashMap<String, HttpRequestMethod> requestMethodMap;

    private Mvc mvc;

    private static Gson gson = new Gson();

    public MvcRunnable(Mvc mvc, MvcContext context, MvcRequest request, MvcResponse response, ConcurrentHashMap<String, HttpRequestMethod> requestMethodMap) {
        this.context = context;
        this.request = request;
        this.response = response;
        this.requestMethodMap = requestMethodMap;
        this.mvc = mvc;
        this.config = this.mvc.getMvcConfig();
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
        this.context.setPath(path);
        this.context.setAllowCross(mvc.getMvcConfig().isAllowCross());
        this.context.setClusterSession(mvc.getMvcConfig().isClusterSession());
        this.request.setMethod(method);
        this.request.setPath(path);
        this.request.setBody(body);
        this.response.setCtx(ctx);
        this.mvc = mvc;
        this.config = this.mvc.getMvcConfig();
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
            String reqBody = new String(request.getBody());
            JsonElement element = GsonUtils.gson.toJsonTree(reqBody);
            if (element.isJsonPrimitive()) {
                request.setPath(Cons.WebSocketPath);
            }
            if (element.isJsonObject()) {
                WsRequest req = GsonUtils.gson.fromJson(new String(request.getBody()), WsRequest.class);
                request.setPath(req.getPath());
                request.setBody(GsonUtils.gson.toJson(req.getParams()).getBytes());
            }
        }

        //Directly returning not found when searching for favicon.ico.
        if (isFaviconIco(request)) {
            response.writeAndFlush(context, HttpResponseStatus.NOT_FOUND, "");
            return;
        }
        String path = request.getPath();

        if (config.isOpenStaticFile() && Html.isHtmlFile(path)) {
            String content = Html.view(config.getStaticFilePath() + path);
            if (StringUtils.isEmpty(content)) {
                sendNotFoundResponse();
                return;
            }
            if(path.endsWith(".html")) {
                context.setContentType("text/html; charset=utf-8");
            }
            response.writeAndFlush(context, content);
            return;
        }

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
            sendNotFoundResponse();
            return;
        }
        //rate limited or exceeded quota
        mvc.callService(context, request, response);
    }

    private void sendNotFoundResponse() {
        MvcResult<Object> result = new MvcResult<>();
        result.setCode(HttpResponseStatus.NOT_FOUND.code());
        result.setMessage(HttpResponseStatus.NOT_FOUND.reasonPhrase());
        response.writeAndFlush(context, gson.toJson(result));
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
