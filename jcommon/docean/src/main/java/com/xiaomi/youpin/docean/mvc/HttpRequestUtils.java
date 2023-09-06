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

package com.xiaomi.youpin.docean.mvc;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 */
public abstract class HttpRequestUtils {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String X_WWW_FORM_URLENCODED = "x-www-form-urlencoded";
    private static final String FORM_DATA = "form-data";

    private static Gson gson = new Gson();

    public static Map<String, String> getQueryParams(String uri) {
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        return decoder.parameters().entrySet().stream().collect(Collectors.toMap(it -> it.getKey(), it -> it.getValue().get(0)));
    }


    public static String getQueryString(FullHttpRequest request) {
        String uri = request.uri();
        int indx = uri.indexOf('?');
        if (indx != -1) {
            return uri.substring(indx + 1);
        } else {
            return "";
        }
    }


    public static String getBasePath(FullHttpRequest request) {
        String uri = request.uri();
        int indx = uri.indexOf('?');
        if (indx != -1) {
            return uri.substring(0, indx);
        } else {
            return uri;
        }
    }

    public static byte[] getRequestBody(FullHttpRequest request) {
        String contentType = request.headers().get(CONTENT_TYPE, "").trim();
        //Support form submission.
        if (contentType.contains(X_WWW_FORM_URLENCODED) || contentType.contains(FORM_DATA)) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);
            List<InterfaceHttpData> postData = decoder.getBodyHttpDatas();
            Map<String, String> kv = new HashMap<>();
            for (InterfaceHttpData data : postData) {
                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                    MemoryAttribute attribute = (MemoryAttribute) data;
                    kv.put(attribute.getName(), attribute.getValue());
                }
            }
            return gson.toJson(kv).getBytes();
        }
        ByteBuf buf = request.content();
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        //Enable downstream to continue reading.
        buf.readerIndex(0);
        return data;
    }


    public static String getClientIp(FullHttpRequest request, Channel channel) {
        String ip = request.headers().get("X-Real-IP");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.headers().get("x-forwarded-for");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            InetSocketAddress insocket = (InetSocketAddress) channel.remoteAddress();
            ip = insocket.getAddress().getHostAddress();
        }
        if (ip.contains(",")) {
            return ip.split(",")[0];
        } else {
            return ip;
        }
    }

    /**
     * Determine whether it is an intranet.
     *
     * @param request
     * @return
     */
    public static boolean intranet(FullHttpRequest request) {
        String v = request.headers().get("PREVIEW-USER", "0");
        if (v.equals("1")) {
            return true;
        }
        return false;
    }


}
