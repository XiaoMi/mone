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

package com.xiaomi.youpin.tesla.file.server.service;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author goodjava@qq.com
 */
public class UploadService extends BaseService {

    public void upload(ChannelHandlerContext ctx, FullHttpRequest request, String name) {
        ByteBuf buf = request.content();
        try (FileOutputStream foStream = new FileOutputStream(DATAPATH + File.separator + name)) {
            byte[] dst = new byte[buf.readableBytes()];
            buf.readBytes(dst);
            foStream.write(dst);
            foStream.flush();
            send(ctx, "ok:" + name);
        } catch (Exception e) {
            e.printStackTrace();
            send(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, "error:" + name);
        }
    }
}
