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
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.Map;

/**
 * @author goodjava@qq.com
 */
public abstract class HttpResponseUtils {

    public static final String ContentTypeJson = "application/json; charset=utf-8";
    public static final String ContentTypeText = "text/plain; charset=utf-8";


    public static FullHttpResponse create(String content) {
        return create(content, HttpResponseStatus.OK, ContentTypeJson);
    }

    public static FullHttpResponse create(String content, Map<String, String> headers) {
        return create(content, HttpResponseStatus.OK, ContentTypeJson, headers);
    }

    public static FullHttpResponse create(FullHttpResponse res) {
        ByteBuf content = res.content();
        if (null == content) {
            res.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
        } else {
            res.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
        }
        res.headers().set(HttpHeaderNames.CONNECTION, "close");
        return res;
    }

    /**
     * 效率不高,谨慎使用
     * @param res
     * @param content
     * @return
     */
    public static FullHttpResponse create(FullHttpResponse res, String content) {
        FullHttpResponse response = new DefaultFullHttpResponse(res.protocolVersion(), res.status(), Unpooled.wrappedBuffer(content.getBytes(CharsetUtil.UTF_8)));
        if (content.length() == 0) {
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
        } else {
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.length());
        }
        response.headers().set(HttpHeaderNames.CONNECTION, "close");
        return response;
    }


    public static String getContent(FullHttpResponse res) {
        byte[] data = new byte[res.content().readableBytes()];
        res.content().readBytes(data);
        res.content().readerIndex(0);
        return new String(data);
    }

    public static FullHttpResponse create(String content, String contentType) {
        return create(content, HttpResponseStatus.OK, contentType);
    }

    public static FullHttpResponse createDefaultSuccess() {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
        response.headers().set(HttpHeaderNames.CONNECTION, "close");
        return response;
    }


    public static FullHttpResponse create(String content, HttpResponseStatus code, String contentType) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, code, Unpooled.wrappedBuffer(content.getBytes(CharsetUtil.UTF_8)));
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, "close");
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
        return response;
    }


    public static FullHttpResponse create(ByteBuf buf) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, "close");
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, ContentTypeJson);
        return response;
    }

    public static FullHttpResponse create(String content, HttpResponseStatus code, String contentType, Map<String, String> headers) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, code, Unpooled.wrappedBuffer(content.getBytes(CharsetUtil.UTF_8)));
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, "close");
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
        if (headers != null) {
            headers.entrySet().stream().forEach(it -> response.headers().set(it.getKey(), it.getValue()));
        }
        return response;
    }

}
