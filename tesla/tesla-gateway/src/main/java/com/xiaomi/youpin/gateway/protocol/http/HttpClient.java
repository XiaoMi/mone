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

package com.xiaomi.youpin.gateway.protocol.http;

import com.google.common.collect.Maps;
import com.xiaomi.youpin.dubbo.filter.TraceIdUtils;
import com.xiaomi.youpin.gateway.TeslaConstants;
import com.xiaomi.youpin.gateway.common.*;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.protocol.IRpcClient;
import com.xiaomi.youpin.gateway.service.ConfigService;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.youpin.xiaomi.tesla.bo.Flag;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.xiaomi.youpin.gateway.filter.FilterContext.SUPPORT_ORIGIN_BYTES;

/**
 * @author goodjava@qq.com
 */
@Component
public class HttpClient extends IRpcClient {

    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    private static ConcurrentHashMap<String, CloseableHttpClient> clientMap = new ConcurrentHashMap<>();

    @Autowired
    private ConfigService configService;


    public static void closeClients() {
        clientMap.forEachValue(1, v -> {
            try {
                v.close();
            } catch (Throwable e) {
                logger.error(e.getMessage());
            }
        });
        clientMap.clear();
    }

    public static void closeClinet(String id) {
        CloseableHttpClient client = clientMap.get(id);
        if (null != client) {
            try {
                client.close();
            } catch (IOException e) {
                logger.error("close http client:{} error:{}", id, e.getMessage());
            }
        }
    }


    public static int clientSize() {
        return clientMap.size();
    }


    public FullHttpResponse call(FilterContext ctx, String httpMethod, String url, byte[] body, final Map<String, String> headers, int timeOut) {
        setFilterContextHeaders(ctx, headers);
        if (httpMethod.toUpperCase().equals("GET")) {
            return get(ctx, url, headers, timeOut, ctx.getCallId());
        }
        if (httpMethod.toUpperCase().equals("POST")) {
            Map<String, String> _headers = filterHeader(headers);
            return post(ctx, url, body, _headers, timeOut, ctx.getCallId());
        }
        throw new UnsupportedOperationException();
    }


    private static Map<String, String> filterHeader(Map<String, String> headers) {
        return headers.entrySet().stream().filter(it -> {
            String key = it.getKey().toLowerCase();
            if (key.equals("content-length")) {
                return false;
            }
            return true;
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    public static byte[] getBytes(HttpEntity entity) {
        if (null != entity) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                entity.writeTo(baos);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
            return baos.toByteArray();
        }
        return new byte[]{};
    }


    public FullHttpResponse get(FilterContext ctx, String url, Map<String, String> headers, int timeOut, String callId) {
        String redirects = ctx.getAttachment("redirectsEnabled", "true");

        boolean redirectsEnabled = "false".equals(redirects) ? false : true;
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(timeOut)
                .setRedirectsEnabled(redirectsEnabled)
                .build();

        ctx.getAttachments().put("param", url);

        HttpGet get = new HttpGet(url);

        headers.entrySet().stream().forEach(it -> {
            if (!it.getKey().equals("Host")) {
                get.setHeader(it.getKey(), it.getValue());
            }
        });
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        String responseContent = "";


        try {
            httpClient = HttpClients.createDefault();
            get.setConfig(requestConfig);

            clientMap.put(callId, httpClient);

            response = httpClient.execute(get);
            entity = response.getEntity();

            boolean originBytes = isOriginBytes(ctx);
            byte[] data = new byte[]{};

            if (null != entity) {
                if (originBytes) {
                    data = getBytes(entity);
                    logger.debug("data:{}", Arrays.toString(data));
                } else {
                    responseContent = EntityUtils.toString(entity, "UTF-8");
                }
            }
            logger.debug("http get url:{} body:{} code:{} result:{}", url, responseContent, response.getStatusLine().getStatusCode(), responseContent);

            if (ctx.getAttachments().containsKey(TeslaCons.RecordRes)) {
                ctx.getAttachments().put(TeslaCons.Res, responseContent);
            }

            ByteBuf buf = null;
            if (originBytes) {
                buf = Unpooled.wrappedBuffer(data);
            } else {
                buf = ByteBufUtils.createBuf(ctx, responseContent, configService.isAllowDirectBuf());
            }
            FullHttpResponse r = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(response.getStatusLine().getStatusCode()), buf);

            Arrays.stream(response.getAllHeaders()).forEach(it -> r.headers().add(it.getName(), it.getValue()));

            return HttpResponseUtils.create(r);
        } catch (Throwable e) {
            logger.warn(url + ":" + e.getMessage());
            return HttpResponseUtils.create(Result.fail(GeneralCodes.InternalError, Msg.msgFor500, e.getMessage()));
        } finally {
            clientMap.remove(callId);
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }

    }

    private boolean isOriginBytes(FilterContext ctx) {
        String str = ctx.getAttachment(SUPPORT_ORIGIN_BYTES, "false");
        if ("true".equals(str)) {
            return true;
        }
        return false;
    }



    public FullHttpResponse post(FilterContext ctx, String url, byte[] body, Map<String, String> headers, int timeOut, String callId) {

        logger.debug("post params, ctx: {}, url: {}, headers: {}, callId: {}", ctx, url, headers, callId);

        ctx.getAttachments().put("param", new String(body));

        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(timeOut)
                .build();


        HttpPost post = new HttpPost(url);
        headers.entrySet().stream().forEach(it -> {
            if (!it.getKey().equals("Host")) {
                post.setHeader(it.getKey(), it.getValue());
            }
        });


        post.setEntity(new ByteArrayEntity(body));

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        String responseContent = null;
        try {
            httpClient = HttpClients.createDefault();
            post.setConfig(requestConfig);
            clientMap.put(callId, httpClient);
            response = httpClient.execute(post);
            entity = response.getEntity();


            boolean originBytes = isOriginBytes(ctx);
            byte[] data = new byte[]{};

            if (originBytes) {
                data = getBytes(entity);
            } else {
                responseContent = EntityUtils.toString(entity, "UTF-8");
            }


            logger.debug("----------> http post url:{} body:{} result:{}", url, body, responseContent);
            ByteBuf buf = null;
            if (originBytes) {
                buf = Unpooled.wrappedBuffer(data);
            } else {
                buf = ByteBufUtils.createBuf(ctx, responseContent, configService.isAllowDirectBuf());
            }
            FullHttpResponse r = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(response.getStatusLine().getStatusCode()), buf);

            Arrays.stream(response.getAllHeaders()).forEach(it -> r.headers().add(it.getName(), it.getValue()));
            return HttpResponseUtils.create(r);
        } catch (Throwable e) {
            logger.warn(e.getMessage());
            return HttpResponseUtils.create(Result.fail(GeneralCodes.InternalError, Msg.msgFor500, e.getMessage()));
        } finally {
            if (null != callId) {
                clientMap.remove(callId);
            }
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public FullHttpResponse call(FilterContext ctx, ApiInfo apiInfo, FullHttpRequest request) {
        try {
            HashMap<String, String> headers = Maps.newHashMap();
            request.headers().forEach(it -> headers.put(it.getKey(), it.getValue()));
            //如果需要放入uid
            setUid(ctx, apiInfo, headers);
            setTraceId(headers);

            String realUri = "";
            String queryString = HttpRequestUtils.getQueryString(request);

            String path = apiInfo.getPath();
            //灰度发布,使用预发地址
            String aPath = ctx.getAttachments().get("Path");
            if (StringUtils.isNotEmpty(aPath)) {
                path = aPath;
            }

            if (StringUtils.isNotEmpty(queryString)) {
                realUri = path + "?" + queryString;
            } else {
                realUri = path;
            }

            int timeOut = Math.max(300, apiInfo.getTimeout());
            return call(ctx, request.method().toString(), realUri, getBody(ctx, request), headers, timeOut);
        } catch (Throwable ex) {
            logger.warn("http client:" + ex.getMessage(), ex);
            return HttpResponseUtils.create(Result.fromException(ex));
        }
    }

    private byte[] getBody(FilterContext ctx, FullHttpRequest request) {
        if (StringUtils.isNotEmpty(ctx.getAttachments().get(FilterContext.New_Body))) {
            return ctx.getAttachments().get(FilterContext.New_Body).getBytes();
        } else {
            return HttpRequestUtils.getRequestBody(request);
        }
    }

    private void setUid(FilterContext ctx, ApiInfo apiInfo, HashMap<String, String> headers) {
        if (apiInfo.isAllow(Flag.ALLOW_AUTH)) {
            headers.put(TeslaConstants.FrontUid, ctx.getUid());
        }
    }

    private void setTraceId(HashMap<String, String> headers) {
        if (StringUtils.isEmpty(headers.get(TeslaConstants.TraceId))) {
            String traceId = TraceIdUtils.getUUID();
            headers.put(TeslaConstants.TraceId, traceId);
        }
    }

}
