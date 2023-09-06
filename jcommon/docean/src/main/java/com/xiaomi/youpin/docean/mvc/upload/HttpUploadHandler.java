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

package com.xiaomi.youpin.docean.mvc.upload;

import com.xiaomi.youpin.docean.common.FileUtils;
import com.xiaomi.youpin.docean.mvc.MvcRunnable;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.UUID;


/**
 * @author goodjava@qq.com
 * <p>
 * Responsible for handling uploads (the optimization point is to store a part of the upload as soon as possible, minimizing memory usage).
 * If using FullHttpRequest code will be simpler, but it will consume a large amount of memory, which is not advisable.
 * This is not dealing with the standard form like data-from.
 * Currently, it is not enabled by default (it will be enabled in the future if there are performance issues).
 */
@Slf4j
public class HttpUploadHandler extends SimpleChannelInboundHandler<HttpObject> {

    private FileChannel channel;

    private File file;

    private String action = "";

    private long beginTime;

    private long len;

    private String name;

    private boolean error;

    public HttpUploadHandler() {
        super(false);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject httpObject) throws Exception {
        try {
            if (httpObject instanceof HttpRequest) {
                HttpRequest request = (HttpRequest) httpObject;
                if (MvcRunnable.isUpload(request.uri()) && request.method().equals(HttpMethod.POST)) {
                    len = Long.valueOf(request.headers().get(HttpHeaderNames.CONTENT_LENGTH, "0"));
                    log.info("len:{}", len);

                    if (len <= 0) {
                        error = true;
                        UploadService.send(ctx, "error:len");
                        return;
                    }

                    String uri = request.uri();
                    QueryStringDecoder decoder = new QueryStringDecoder(uri);
                    String defaultId = UUID.randomUUID().toString();
                    name = decoder.parameters().getOrDefault("name", Arrays.asList(defaultId)).get(0);
                    String token = decoder.parameters().getOrDefault("token", Arrays.asList("")).get(0);
                    action = UploadCons.UPLOAD;
                    log.info("upload file:{} begin {}", this.name, this);
                    file = new File(UploadService.DATAPATH + File.separator + name);
                    if (file.exists()) {
                        FileUtils.forceDelete(file);
                    }
                    FileOutputStream fos = new FileOutputStream(file);
                    channel = fos.getChannel();
                    beginTime = System.currentTimeMillis();
                    return;
                } else {
                    ctx.fireChannelRead(httpObject);
                    return;
                }
            }

            if (action.equals(UploadCons.UPLOAD)) {
                if (!(httpObject instanceof DefaultHttpContent)) {
                    UploadService.send(ctx, "error:type");
                    return;
                }
                DefaultHttpContent chunk = (DefaultHttpContent) httpObject;
                //rate limited or exceeded quota
                if (!error) {
                    ByteBuf buf = chunk.content();
                    ByteBuffer buffer = buf.nioBuffer();
                    channel.write(buffer);
                    if (chunk instanceof LastHttpContent) {
                        finish(ctx);
                    }
                    return;
                } else {
                    if (chunk instanceof LastHttpContent) {
                        UploadService.send(ctx, "error:" + name);
                        return;
                    }
                }
            } else {
                ctx.fireChannelRead(httpObject);
                return;
            }
        } finally {
            //rate limited or exceeded quota
            int count = ReferenceCountUtil.refCnt(httpObject);
            log.debug("ref count:{}", count);
            if (count > 0) {
                ReferenceCountUtil.safeRelease(httpObject, count);
            }
        }
    }

    private void finish(ChannelHandlerContext ctx) throws IOException {
        channel.force(true);
        long useTime = System.currentTimeMillis() - this.beginTime;
        log.info("upload file:{} size:{} finish use time:{} {}", this.name, this.len, useTime, this);
        UploadService.send(ctx, "ok:upload:" + len + ":" + (System.currentTimeMillis() - this.beginTime));
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws IOException {
        log.warn("upload error:" + cause.getMessage(), cause);
        if (null != this.file) {
            FileUtils.forceDelete(this.file);
        }
        if (ctx.channel().isActive()) {
            UploadService.send(ctx, "error:" + cause.getMessage());
        }
        ctx.close();
    }


}
