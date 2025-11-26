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

import com.xiaomi.youpin.tesla.file.server.common.Cons;
import com.xiaomi.youpin.tesla.file.server.utils.DirUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;

/**
 * @author goodjava@qq.com
 */
public class DownloadService extends BaseService {


    public void download(ChannelHandlerContext ctx, FullHttpRequest request, String userKey, String directoryPath, String name, String id) throws IOException {

        // Validate directory path if provided
        if (StringUtils.isNotEmpty(directoryPath)) {
            if (!DirUtils.isValidDirectoryPath(directoryPath)) {
                BaseService.send(ctx, "error:Invalid directory path format. Path can only contain alphanumeric characters, hyphens, and forward slashes.");
                return;
            }
        }

        final String path = DirUtils.filePath(userKey, directoryPath, name);
        File file = new File(path);
        if (file.isHidden() || !file.exists() || !file.isFile() || !file.getPath().startsWith(Cons.DATAPATH)) {
            send(ctx, NOT_FOUND, NOT_FOUND.reasonPhrase());
            return;
        }
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        long fileLength = raf.length();
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        HttpUtil.setContentLength(response, fileLength);
        setContentTypeHeader(response, file);
        response.headers().set("Content-Disposition", "attachment; filename=" + name);
        if (HttpUtil.isKeepAlive(request)) {
            response.headers().set("CONNECTION", HttpHeaderNames.KEEP_ALIVE);
        }

        ctx.write(response);

        ChannelFuture sendFileFuture =
                ctx.write(new HttpChunkedInput(new ChunkedFile(raf, 0, fileLength, 1024 * 64)), ctx.newProgressivePromise());


        sendFileFuture.addListener(new DownloadListener(id, fileLength));
        ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

        if (!HttpHeaders.isKeepAlive(request)) {
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }


    @SneakyThrows
    private static void setContentTypeHeader(HttpResponse response, File file) {
        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) {
            contentType = "application/octet-stream";
        } else if (!contentType.equals("application/octet-stream")) {
            contentType += "; charset=utf-8";
        }
        response.headers().set(CONTENT_TYPE, contentType);
    }


}
