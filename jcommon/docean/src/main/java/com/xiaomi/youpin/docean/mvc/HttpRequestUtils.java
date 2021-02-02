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

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 */
public abstract class HttpRequestUtils {


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
        ByteBuf buf = request.content();
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        //使得下游继续可以读取
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
     * 判断是否是内网
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
