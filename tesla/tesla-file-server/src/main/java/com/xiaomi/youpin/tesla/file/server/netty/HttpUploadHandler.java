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

package com.xiaomi.youpin.tesla.file.server.netty;

import com.xiaomi.youpin.tesla.file.server.common.Cons;
import com.xiaomi.youpin.tesla.file.server.service.BaseService;
import com.xiaomi.youpin.tesla.file.server.service.TokenService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

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
 * 负责处理上传(优化的点是上传一部分就存储一部分,尽量少的占用内存)
 * 如果用FullHttpRequest 代码会简单些,但这会占用大量内存,不可取
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
                if (request.uri().startsWith(Cons.UPLOAD) && request.method().equals(HttpMethod.POST)) {
                    len = Long.valueOf(request.headers().get(HttpHeaderNames.CONTENT_LENGTH, "0"));
                    log.info("len:{}", len);

                    if (len <= 0) {
                        error = true;
                        BaseService.send(ctx, "error:len");
                        return;
                    }

                    String uri = request.uri();
                    QueryStringDecoder decoder = new QueryStringDecoder(uri);
                    String defaultId = UUID.randomUUID().toString();
                    name = decoder.parameters().getOrDefault("name", Arrays.asList(defaultId)).get(0);
                    String token = decoder.parameters().getOrDefault("token", Arrays.asList("")).get(0);

                    action = Cons.UPLOAD;

                    if (!BaseService.checkParams(ctx, name, token)) {
                        log.warn("param error");
                        error = true;
                        return;
                    }

                    if (!BaseService.checkToken(ctx, name, token, decoder.path())) {
                        log.warn("token error");
                        error = true;
                        return;
                    }

                    log.info("upload file:{} begin {}", this.name, this);

                    file = new File(BaseService.DATAPATH + File.separator + name);
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

            if (action.equals(Cons.UPLOAD)) {
                if (!(httpObject instanceof DefaultHttpContent)) {
                    BaseService.send(ctx, "error:type");
                    return;
                }
                DefaultHttpContent chunk = (DefaultHttpContent) httpObject;
                //处理到这里,可能client 还在发送数据,所以这里需要有error 的处理逻辑
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
                        BaseService.send(ctx, "error:" + name);
                        return;
                    }
                }
            } else {
                ctx.fireChannelRead(httpObject);
                return;
            }
        } finally {
            //改为自己手动释放
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
        BaseService.send(ctx, "ok:upload:" + len + ":" + (System.currentTimeMillis() - this.beginTime) + ":" + new TokenService().generateToken(this.name));
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws IOException {
        log.warn("upload error:" + cause.getMessage(), cause);
        if (null != this.file) {
            FileUtils.forceDelete(this.file);
        }
        if (ctx.channel().isActive()) {
            BaseService.send(ctx, "error:" + cause.getMessage());
        }
        ctx.close();
    }


}
